package pro.samgerstner.easyexchange.entities.api;

import pro.samgerstner.easyexchange.entities.Document;

public class DocumentResponse
{
   private String status;
   private String message;
   private String timestamp;
   private Document document;

   public DocumentResponse(String status, String message, String timestamp, Document document)
   {
      this.status = status;
      this.message = message;
      this.timestamp = timestamp;
      this.document = document;
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

   public Document getDocument()
   {
      return document;
   }

   public void setDocument(Document document)
   {
      this.document = document;
   }
}