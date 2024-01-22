package pro.samgerstner.easyexchange.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.samgerstner.easyexchange.entities.AuthorizationStatus;
import pro.samgerstner.easyexchange.entities.Client;
import pro.samgerstner.easyexchange.entities.api.ClientResponse;
import pro.samgerstner.easyexchange.entities.repositories.ClientRepository;
import pro.samgerstner.easyexchange.helpers.AuthorizationHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/clients")
public class ClientControllerApi
{
   @Autowired
   private ClientRepository clientRepo;

   @Autowired
   private AuthorizationHelper authHelper;

   @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<?> create(@RequestHeader Map<String, String> headers, @RequestBody Client client)
   {
      if(authHelper.authorizeUserByApiKey(headers) != AuthorizationStatus.AUTHORIZED)
      {
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      //Verify all fields were provided
      if(client.getFirstName() == null || client.getLastName() == null || client.getEmail() == null || client.getId() != null)
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      if(client.getFirstName().isEmpty() || client.getLastName().isEmpty() || client.getEmail().isEmpty())
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      clientRepo.save(client);
      String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
      ClientResponse response = new ClientResponse("complete", "Successfully created client.", timestamp, client);
      return ResponseEntity.ok().body(response);
   }

   @PostMapping(path = "/edit")
   public ResponseEntity<?> edit(@RequestHeader Map<String, String> headers, @RequestBody Client editClient)
   {
      if(authHelper.authorizeUserByApiKey(headers) != AuthorizationStatus.AUTHORIZED)
      {
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      Optional<Client> clientOptional = clientRepo.findById(editClient.getId());
      if(clientOptional.isEmpty())
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      Client client = clientOptional.get();
      client.setFirstName(editClient.getFirstName());
      client.setLastName(editClient.getLastName());
      client.setEmail(editClient.getEmail());
      clientRepo.save(client);
      String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
      ClientResponse response = new ClientResponse("complete", "Successfully edited client.", timestamp, client);
      return ResponseEntity.ok().body(response);
   }

   @DeleteMapping(path = "/delete")
   public ResponseEntity<?> delete(@RequestHeader Map<String, String> headers, @RequestBody Client client)
   {
      if(authHelper.authorizeUserByApiKey(headers) != AuthorizationStatus.AUTHORIZED)
      {
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      Optional<Client> clientOptional = clientRepo.findById(client.getId());
      if(clientOptional.isEmpty())
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      Client clientReal = clientOptional.get();
      clientRepo.delete(clientReal);
      String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
      ClientResponse response = new ClientResponse("complete", "Successfully deleted client.", timestamp, clientReal);
      return ResponseEntity.ok().body(response);
   }
}