package pro.samgerstner.easyexchange.controllers.mvc;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pro.samgerstner.easyexchange.helpers.AuthorizationHelper;
import pro.samgerstner.easyexchange.entities.AdminUser;
import pro.samgerstner.easyexchange.entities.AuthorizationStatus;
import pro.samgerstner.easyexchange.entities.repositories.AdminUserRepository;
import pro.samgerstner.easyexchange.entities.repositories.ApplicationRoleRepository;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/admin-users")
public class AdminUserController
{
   @Value("${application.title}")
   private String title;

   @Autowired
   private AdminUserRepository adminRepo;

   @Autowired
   private ApplicationRoleRepository roleRepo;

   private final String[] allowedRoles = {"Super Admin"};

   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public String badRequest()
   {
      return "Bad request";
   }

   @GetMapping(value = "/create")
   public String getCreate(HttpSession session, Model model)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      model.addAttribute("appTitle", title);
      model.addAttribute("user", new AdminUser());
      model.addAttribute("roles", roleRepo.findAll());
      model.addAttribute("errorMsg", "");
      return "admin_create";
   }

   @PostMapping(value = "/create")
   public String postCreate(HttpSession session, @ModelAttribute AdminUser user, Model model)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      Optional<AdminUser> userOptional = adminRepo.findByUsername(user.getUsername());
      if(!userOptional.isEmpty())
      {
         model.addAttribute("appTitle", title);
         model.addAttribute("user", user);
         model.addAttribute("errorMsg", "A user with that unique identifier already exists.");
         return "admin_create";
      }

      if(user.isApiEnabled())
      {
         user.populateApiKey();
      }
      adminRepo.save(user);
      return "redirect:/admin-users/view";
   }

   @GetMapping(value = "/edit")
   public String getEdit(HttpSession session, @RequestParam int id,  Model model)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      Optional<AdminUser> userOptional = adminRepo.findById(id);

      if(userOptional.isEmpty())
      {
         return "redirect:/admin-users/bad-request";
      }

      model.addAttribute("appTitle", title);
      model.addAttribute("user", userOptional.get());
      model.addAttribute("roles", roleRepo.findAll());
      return "admin_edit";
   }

   @PostMapping(value = "/edit")
   public String postEdit(HttpSession session, @RequestParam int id,  @ModelAttribute AdminUser modifiedUser)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      Optional<AdminUser> userOptional = adminRepo.findById(id);

      if(userOptional.isEmpty())
      {
         return "redirect:/admin-users/bad-request";
      }

      AdminUser user = userOptional.get();
      user.setUsername(modifiedUser.getUsername());
      user.setUserRole(modifiedUser.getUserRole());
      user.setApiEnabled(modifiedUser.isApiEnabled());
      if(user.isApiEnabled() && user.getApiKey().length() == 0)
      {
         user.populateApiKey();
      }
      adminRepo.save(user);
      return "redirect:/admin-users/view";
   }

   @GetMapping(value = "/view")
   public String view(@RequestParam(required = false) String search, @RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id,asc") String[] sort,
                      Model model, HttpSession session)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      model.addAttribute("appTitle", title);
      String sortField = sort[0];
      String sortDirection = sort[1];
      Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
      Sort.Order order = new Sort.Order(direction, sortField);
      Pageable pageable = PageRequest.of(page - 1, size, Sort.by(order));

      Page<AdminUser> pageUsers;
      if(search == null)
      {
         pageUsers = adminRepo.findAllPageable(pageable);
      }
      else
      {
         pageUsers = adminRepo.findAllByUsername(search, pageable);
         model.addAttribute("search", search);
      }

      List<AdminUser> users = pageUsers.getContent();
      model.addAttribute("ausers", users);
      model.addAttribute("currentPage", pageUsers.getNumber() + 1);
      model.addAttribute("totalItems", pageUsers.getTotalElements());
      model.addAttribute("totalPages", pageUsers.getTotalPages());
      model.addAttribute("pageSize", size);
      model.addAttribute("sortField", sortField);
      model.addAttribute("sortDirection", sortDirection);
      model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
      return "admin_view";
   }

   @GetMapping(value = "/delete")
   public String getDelete(HttpSession session, @RequestParam int id, Model model)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      Optional<AdminUser> userOptional = adminRepo.findById(id);

      if(userOptional.isEmpty())
      {
         return "redirect:/admin-users/bad-request";
      }

      model.addAttribute("appTitle", title);
      model.addAttribute("user", userOptional.get());
      return "admin_delete";
   }

   @PostMapping(value = "/delete")
   public String postDelete(HttpSession session, @RequestParam int id)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      Optional<AdminUser> userOptional = adminRepo.findById(id);

      if(userOptional.isEmpty())
      {
         return "redirect:/admin-users/bad-request";
      }

      adminRepo.deleteById(id);
      return "redirect:/admin-users/view";
   }
}