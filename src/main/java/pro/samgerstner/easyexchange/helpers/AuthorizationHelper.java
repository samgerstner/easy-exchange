package pro.samgerstner.easyexchange.helpers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import pro.samgerstner.easyexchange.entities.AdminUser;
import pro.samgerstner.easyexchange.entities.AuthorizationStatus;
import pro.samgerstner.easyexchange.entities.repositories.AdminUserRepository;
import java.util.*;

@Service
@Configurable
public class AuthorizationHelper
{
   @Autowired
   private AdminUserRepository adminRepo;

   public static AuthorizationStatus authorizeUserByRole(HttpSession session, String[] allowedRoles)
   {
      //Verify that user role session variable exists
      Enumeration<String> sessionAttributesRaw = session.getAttributeNames();
      List<String> sessionAttributes = new ArrayList<String>();

      while(sessionAttributesRaw.hasMoreElements())
      {
         sessionAttributes.add(sessionAttributesRaw.nextElement());
      }

      if(!sessionAttributes.contains("role"))
      {
         return AuthorizationStatus.MISSING_SESSION_VAR;
      }

      //Check if user's role is in list of allowed roles
      if(!Arrays.stream(allowedRoles).toList().contains(session.getAttribute("role").toString()))
      {
         return AuthorizationStatus.UNAUTHORIZED;
      }

      return AuthorizationStatus.AUTHORIZED;
   }

   public static String getAuthorizationRedirect(AuthorizationStatus status)
   {
      return status == AuthorizationStatus.MISSING_SESSION_VAR ? "redirect:/oauth2/authorization/generic" : "redirect:/access-denied";
   }

   public static String getAuthorizationRedirect(AuthorizationStatus status, boolean omitPrefix)
   {
      if(omitPrefix)
      {
         return status == AuthorizationStatus.MISSING_SESSION_VAR ? "/oauth2/authorization/generic" : "/access-denied";
      }

      return status == AuthorizationStatus.MISSING_SESSION_VAR ? "redirect:/oauth2/authorization/generic" : "redirect:/access-denied";
   }

   public AuthorizationStatus authorizeUserByApiKey(Map<String, String> headers)
   {
      if(!headers.containsKey("x-api-user") || !headers.containsKey("x-api-key"))
      {
         return AuthorizationStatus.UNAUTHORIZED;
      }

      Optional<AdminUser> userOptional = adminRepo.findByUsername(headers.get("x-api-user"));
      if(userOptional.isEmpty())
      {
         return AuthorizationStatus.UNAUTHORIZED;
      }
      AdminUser user = userOptional.get();

      if(!user.getApiKey().equals(headers.get("x-api-key")))
      {
         return AuthorizationStatus.UNAUTHORIZED;
      }

      return AuthorizationStatus.AUTHORIZED;
   }
}