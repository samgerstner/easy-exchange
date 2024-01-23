package pro.samgerstner.easyexchange.entities.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionSearchRequest
{
   private String guid;

   @JsonProperty("client_email")
   private String clientEmail;

   public SessionSearchRequest(String guid, String clientEmail)
   {
      this.guid = guid;
      this.clientEmail = clientEmail;
   }

   public String getGuid()
   {
      return guid;
   }

   public void setGuid(String guid)
   {
      this.guid = guid;
   }

   public String getClientEmail()
   {
      return clientEmail;
   }

   public void setClientEmail(String clientEmail)
   {
      this.clientEmail = clientEmail;
   }
}