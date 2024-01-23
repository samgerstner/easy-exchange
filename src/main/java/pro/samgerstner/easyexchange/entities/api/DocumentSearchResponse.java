package pro.samgerstner.easyexchange.entities.api;

import pro.samgerstner.easyexchange.entities.Document;

public class DocumentSearchResponse
{
   private String status;
   private String message;
   private String timestamp;
   private Document[] documents;

   public DocumentSearchResponse(String status, String message, String timestamp, Document[] documents)
   {
      this.status = status;
      this.message = message;
      this.timestamp = timestamp;
      this.documents = documents;
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

   public Document[] getDocuments()
   {
      return documents;
   }

   public void setDocuments(Document[] documents)
   {
      this.documents = documents;
   }
}