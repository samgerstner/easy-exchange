package pro.samgerstner.easyexchange.entities.api;

import pro.samgerstner.easyexchange.entities.Client;

public class ClientResponse
{
   private String status;
   private String message;
   private String timestamp;
   private Client client;

   public ClientResponse(String status, String message, String timestamp, Client client)
   {
      this.status = status;
      this.message = message;
      this.timestamp = timestamp;
      this.client = client;
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

   public Client getClient()
   {
      return client;
   }

   public void setClient(Client client)
   {
      this.client = client;
   }
}