package pro.samgerstner.easyexchange.entities;

public class DownloadRequest
{
   private String docGUID;
   private String sessionGUID;
   private String clientEmail;

   public String getDocGUID()
   {
      return docGUID;
   }

   public void setDocGUID(String docGUID)
   {
      this.docGUID = docGUID;
   }

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