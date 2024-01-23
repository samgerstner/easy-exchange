package pro.samgerstner.easyexchange.entities.api;

import pro.samgerstner.easyexchange.entities.UploadSession;

public class SessionSearchResponse
{
   private String status;
   private String message;
   private String timestamp;
   private UploadSession[] sessions;

   public SessionSearchResponse(String status, String message, String timestamp, UploadSession[] sessions)
   {
      this.status = status;
      this.message = message;
      this.timestamp = timestamp;
      this.sessions = sessions;
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

   public UploadSession[] getSessions()
   {
      return sessions;
   }

   public void setSessions(UploadSession[] sessions)
   {
      this.sessions = sessions;
   }
}