package pro.samgerstner.easyexchange.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.samgerstner.easyexchange.entities.AuthorizationStatus;
import pro.samgerstner.easyexchange.entities.Client;
import pro.samgerstner.easyexchange.entities.api.ClientResponse;
import pro.samgerstner.easyexchange.entities.api.ClientSearchRequest;
import pro.samgerstner.easyexchange.entities.api.ClientSearchResponse;
import pro.samgerstner.easyexchange.entities.repositories.ClientRepository;
import pro.samgerstner.easyexchange.helpers.AuthorizationHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/clients", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientControllerApi
{
   @Autowired
   private ClientRepository clientRepo;

   @Autowired
   private AuthorizationHelper authHelper;

   private final String[] allowedRoles = {"Client Manager", "Administrator", "Super Admin"};

   @PostMapping(path = "/create")
   public ResponseEntity<?> create(@RequestHeader Map<String, String> headers, @RequestBody Client client)
   {
      if(authHelper.authorizeUserByApiKey(headers, allowedRoles) != AuthorizationStatus.AUTHORIZED)
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
      if(authHelper.authorizeUserByApiKey(headers, allowedRoles) != AuthorizationStatus.AUTHORIZED)
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
      if(authHelper.authorizeUserByApiKey(headers, allowedRoles) != AuthorizationStatus.AUTHORIZED)
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

   @GetMapping(path = "/search")
   public ResponseEntity<?> search(@RequestHeader Map<String, String> headers, @RequestBody ClientSearchRequest req)
   {
      if(authHelper.authorizeUserByApiKey(headers, allowedRoles) != AuthorizationStatus.AUTHORIZED)
      {
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      //Verify at least one search field was provided
      if(req.getId() == 0 && req.getFirstName() == null && req.getLastName() == null && req.getEmail() == null)
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      Client[] clients = clientRepo.findByFirstNameAndLastNameAndEmailAndId(req.getFirstName(), req.getLastName(), req.getEmail(), req.getId());
      String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
      ClientSearchResponse response = new ClientSearchResponse("", "", timestamp, clients);
      return ResponseEntity.ok().body(response);
   }
}