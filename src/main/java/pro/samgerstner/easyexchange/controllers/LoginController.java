package pro.samgerstner.easyexchange.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pro.samgerstner.easyexchange.entities.AdminUser;
import pro.samgerstner.easyexchange.entities.ApplicationRole;
import pro.samgerstner.easyexchange.entities.repositories.AdminUserRepository;
import pro.samgerstner.easyexchange.entities.repositories.ApplicationRoleRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class LoginController
{
   private static final String authorizationRequestBaseUri = "oauth2/authorization";
   Map<String, String> oauthAuthenticationUrls = new HashMap<>();

   @Autowired
   private ClientRegistrationRepository clientRegistrationRepository;

   @Autowired
   private OAuth2AuthorizedClientService authorizedClientService;

   @Autowired
   private AdminUserRepository adminRepo;

   @Autowired
   private ApplicationRoleRepository roleRepo;

   @GetMapping(value = "/login")
   public String getLoginPage(Model model) {
      Iterable<ClientRegistration> clientRegistrations = null;
      ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
              .as(Iterable.class);
      if (type != ResolvableType.NONE &&
              ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
         clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
      }

      clientRegistrations.forEach(registration ->
              oauthAuthenticationUrls.put(registration.getClientName(),
                      authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
      model.addAttribute("urls", oauthAuthenticationUrls);

      return "login";
   }

   @GetMapping(value = "/login-success")
   public String loginSuccess(OAuth2AuthenticationToken auth, HttpSession session)
   {
      OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(auth.getAuthorizedClientRegistrationId(), auth.getName());

      //Check if this is the first admin user
      if(adminRepo.findAllList().isEmpty())
      {
         ApplicationRole userRole = roleRepo.findByName("Super Admin").get();
         AdminUser newUser = new AdminUser();
         newUser.setUsername(client.getClientRegistration().getClientId());
         newUser.setUserRole(userRole);
         newUser.setApiEnabled(true);
         newUser.populateApiKey();
         adminRepo.save(newUser);
      }

      //Check if an admin user exists for this uid
      Optional<AdminUser> userOptional = adminRepo.findByUsername(client.getClientRegistration().getClientId());
      if(userOptional.isEmpty())
      {
         session.setAttribute("uid", client.getClientRegistration().getClientId());
         return "redirect:/admin-no-access";
      }
      AdminUser user = userOptional.get();

      //Add uid and role as session variables
      session.setAttribute("uid", client.getClientRegistration().getClientId());
      session.setAttribute("role", user.getUserRole().getRoleName());

      return "redirect:/admin-home";
   }
}