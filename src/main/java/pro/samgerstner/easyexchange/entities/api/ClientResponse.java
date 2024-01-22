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
}