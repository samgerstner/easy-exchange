package pro.samgerstner.easyexchange.entities.api;

import pro.samgerstner.easyexchange.entities.UploadSession;

public class SessionResponse
{
   private String status;
   private String message;
   private String timestamp;
   private UploadSession session;

   public SessionResponse(String status, String message, String timestamp, UploadSession session)
   {
      this.status = status;
      this.message = message;
      this.timestamp = timestamp;
      this.session = session;
   }

   public String getStatus()
   {
      return status;
   }

   public void setStatus(String status)
   {
      this.status = status;
   }

   public String getMessage()
   {
      return message;
   }

   public void setMessage(String message)
   {
      this.message = message;
   }

   public String getTimestamp()
   {
      return timestamp;
   }

   public void setTimestamp(String timestamp)
   {
      this.timestamp = timestamp;
   }

   public UploadSession getSession()
   {
      return session;
   }

   public void setSession(UploadSession session)
   {
      this.session = session;
   }
}