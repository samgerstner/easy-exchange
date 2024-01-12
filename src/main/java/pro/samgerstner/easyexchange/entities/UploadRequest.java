package pro.samgerstner.easyexchange.entities;

public class UploadRequest
{
   private String sessionGUID;
   private String clientEmail;

   public String getSessionGUID()
   {
      return sessionGUID;
   }

   public void setSessionGUID(String sessionGUID)
   {
      this.sessionGUID = sessionGUID;
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