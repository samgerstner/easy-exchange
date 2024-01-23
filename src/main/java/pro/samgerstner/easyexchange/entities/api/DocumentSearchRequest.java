package pro.samgerstner.easyexchange.entities.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentSearchRequest
{
   private String guid;

   @JsonProperty("session_guid")
   private String sessionGuid;

   @JsonProperty("client_email")
   private String clientEmail;

   public DocumentSearchRequest(String guid, String sessionGuid, String clientEmail)
   {
      this.guid = guid;
      this.sessionGuid = sessionGuid;
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

   public String getSessionGuid()
   {
      return sessionGuid;
   }

   public void setSessionGuid(String sessionGuid)
   {
      this.sessionGuid = sessionGuid;
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