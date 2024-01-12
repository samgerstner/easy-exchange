package pro.samgerstner.easyexchange.entities;

import org.springframework.web.multipart.MultipartFile;

public class UploadRequest
{
   private String sessionGUID;
   private String clientEmail;
   private MultipartFile file;

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

   public MultipartFile getFile()
   {
      return file;
   }

   public void setFile(MultipartFile file)
   {
      this.file = file;
   }
}