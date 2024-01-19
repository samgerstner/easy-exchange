package pro.samgerstner.easyexchange.helpers;

import jakarta.servlet.http.HttpSession;
import pro.samgerstner.easyexchange.entities.AuthorizationStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class AuthorizationHelper
{
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
}