package pro.samgerstner.easyexchange.entities.api;

import pro.samgerstner.easyexchange.entities.Client;

public class ClientSearchResponse
{
   private String status;
   private String message;
   private String timestamp;
   private Client[] clients;

   public ClientSearchResponse(String status, String message, String timestamp, Client[] clients)
   {
      this.status = status;
      this.message = message;
      this.timestamp = timestamp;
      this.clients = clients;
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

   public Client[] getClients()
   {
      return clients;
   }

   public void setClients(Client[] client)
   {
      this.clients = client;
   }
}