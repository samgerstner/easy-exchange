package pro.samgerstner.easyexchange.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.samgerstner.easyexchange.entities.AuthorizationStatus;
import pro.samgerstner.easyexchange.entities.Client;
import pro.samgerstner.easyexchange.entities.UploadSession;
import pro.samgerstner.easyexchange.entities.api.*;
import pro.samgerstner.easyexchange.entities.repositories.ClientRepository;
import pro.samgerstner.easyexchange.entities.repositories.UploadSessionRepository;
import pro.samgerstner.easyexchange.helpers.AuthorizationHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/upload-sessions", produces = MediaType.APPLICATION_JSON_VALUE)
public class UploadSessionControllerApi
{
   @Autowired
   private UploadSessionRepository sessionRepo;

   @Autowired
   private ClientRepository clientRepo;

   @Autowired
   private AuthorizationHelper authHelper;

   private final String[] allowedRoles = {"Session Manager", "Administrator", "Super Admin"};

   @PostMapping(path = "/create")
   public ResponseEntity<?> create(@RequestHeader Map<String, String> headers, @RequestBody SessionCreateRequest req)
   {
      if(authHelper.authorizeUserByApiKey(headers, allowedRoles) != AuthorizationStatus.AUTHORIZED)
      {
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      //Verify all fields were provided
      if(req.getClientId() == 0)
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      Optional<Client> clientOptional = clientRepo.findById(req.getClientId());
      if(clientOptional.isEmpty())
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      UploadSession session = new UploadSession();
      session.setMaxDocuments(req.getMaxDocuments());
      session.setUploadLocked(req.isUploadLocked());
      session.setDownloadLocked(req.isDownloadLocked());
      session.setClient(clientOptional.get());
      sessionRepo.save(session);
      String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
      SessionResponse response = new SessionResponse("complete", "Successfully created upload session.", timestamp, session);
      return ResponseEntity.ok().body(response);
   }

   @PostMapping(path = "/edit")
   public ResponseEntity<?> edit(@RequestHeader Map<String, String> headers, @RequestBody SessionEditRequest req)
   {
      if(authHelper.authorizeUserByApiKey(headers, allowedRoles) != AuthorizationStatus.AUTHORIZED)
      {
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      Optional<UploadSession> sessionOptional = sessionRepo.findById(req.getGuid());
      if(sessionOptional.isEmpty())
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      UploadSession session = sessionOptional.get();

      session.setMaxDocuments(req.getMaxDocuments());
      session.setUploadLocked(req.isUploadLocked());
      session.setDownloadLocked(req.isDownloadLocked());

      if(req.getClientId() != 0)
      {
         Optional<Client> clientOptional = clientRepo.findById(req.getClientId());
         if(clientOptional.isEmpty())
         {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }

         session.setClient(clientOptional.get());
      }

      sessionRepo.save(session);
      String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
      SessionResponse response = new SessionResponse("complete", "Successfully edited upload session.", timestamp, session);
      return ResponseEntity.ok().body(response);
   }

   @PostMapping(path = "/delete")
   public ResponseEntity<?> delete(@RequestHeader Map<String, String> headers, @RequestBody UploadSession session)
   {
      if(authHelper.authorizeUserByApiKey(headers, allowedRoles) != AuthorizationStatus.AUTHORIZED)
      {
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      Optional<UploadSession> sessionOptional = sessionRepo.findById(session.getGuid());
      if(sessionOptional.isEmpty())
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      UploadSession sessionReal = sessionOptional.get();
      String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
      SessionResponse response = new SessionResponse("complete", "Successfully deleted upload session.", timestamp, sessionReal);
      return ResponseEntity.ok().body(response);
   }

   @GetMapping(path = "/search")
   public ResponseEntity<?> search(@RequestHeader Map<String, String> headers, @RequestBody SessionSearchRequest req)
   {
      if(authHelper.authorizeUserByApiKey(headers, allowedRoles) != AuthorizationStatus.AUTHORIZED)
      {
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      //Verify at least one search field was provided
      if(req.getGuid() == null && req.getClientEmail() == null)
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      UploadSession[] sessions = sessionRepo.findByGuidAndClientEmail(req.getGuid(), req.getClientEmail());
      String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
      SessionSearchResponse response = new SessionSearchResponse("complete", "Successfully searched upload sessions.", timestamp, sessions);
      return ResponseEntity.ok().body(response);
   }
}