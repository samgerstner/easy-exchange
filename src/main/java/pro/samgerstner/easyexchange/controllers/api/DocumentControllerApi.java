package pro.samgerstner.easyexchange.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.samgerstner.easyexchange.entities.AuthorizationStatus;
import pro.samgerstner.easyexchange.entities.Document;
import pro.samgerstner.easyexchange.entities.api.DocumentResponse;
import pro.samgerstner.easyexchange.entities.api.DocumentSearchRequest;
import pro.samgerstner.easyexchange.entities.api.DocumentSearchResponse;
import pro.samgerstner.easyexchange.entities.repositories.DocumentRepository;
import pro.samgerstner.easyexchange.helpers.AuthorizationHelper;
import pro.samgerstner.easyexchange.helpers.S3Helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/documents", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentControllerApi
{
   @Autowired
   private DocumentRepository docRepo;

   @Autowired
   private AuthorizationHelper authHelper;

   @Value("${aws.access-key}")
   private String accessKey;

   @Value("${aws.secret-key}")
   private String secretKey;

   @Value("${aws.region}")
   private String region;

   @Value("${aws.bucket-name}")
   private String bucketName;

   private final String[] allowedRoles = {"Administrator", "Super Admin"};

   @GetMapping(path = "/search")
   public ResponseEntity<?> search(@RequestHeader Map<String, String> headers, @RequestBody DocumentSearchRequest req)
   {
      if(authHelper.authorizeUserByApiKey(headers, allowedRoles) != AuthorizationStatus.AUTHORIZED)
      {
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      //Verify at least one search field was provided
      if(req.getGuid() == null && req.getSessionGuid() == null && req.getClientEmail() == null)
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      Document[] documents = docRepo.findByGuidAndSessionGuidAndClientEmail(req.getGuid(), req.getSessionGuid(), req.getClientEmail());
      String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
      DocumentSearchResponse response = new DocumentSearchResponse("complete", "Successfully searched documents.", timestamp, documents);
      return ResponseEntity.ok().body(response);
   }

   @DeleteMapping(path = "/delete")
   public ResponseEntity<?> delete(@RequestHeader Map<String, String> headers, @RequestBody Document doc)
   {
      if(authHelper.authorizeUserByApiKey(headers, allowedRoles) != AuthorizationStatus.AUTHORIZED)
      {
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      Optional<Document> docOptional = docRepo.findById(doc.getGuid());
      if(docOptional.isEmpty())
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      Document docReal = docOptional.get();
      S3Helper s3 = new S3Helper(accessKey, secretKey, region, bucketName);
      s3.deleteSessionFile(docReal.getUploadSession().getGuid(), docReal.getFileName());
      docRepo.delete(docReal);


      String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
      DocumentResponse response = new DocumentResponse("", "", timestamp, docReal);
      return ResponseEntity.ok().body(response);
   }

   @GetMapping(path = "/download")
   public ResponseEntity<?> download(@RequestHeader Map<String, String> headers, @RequestBody Document doc)
   {
      if(authHelper.authorizeUserByApiKey(headers, allowedRoles) != AuthorizationStatus.AUTHORIZED)
      {
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      Optional<Document> docOptional = docRepo.findById(doc.getGuid());
      if(docOptional.isEmpty())
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      Document docReal = docOptional.get();
      S3Helper s3 = new S3Helper(accessKey, secretKey, region, bucketName);
      HttpHeaders responseHeaders = new HttpHeaders();

      byte[] fileData = s3.downloadSessionFile(docReal.getUploadSession().getGuid(), docReal.getFileName());
      responseHeaders.setContentType(MediaType.parseMediaType(docReal.getFileType()));
      responseHeaders.set("Content-Disposition", "attachment;filename=" + docReal.getFileName());
      return new ResponseEntity<>(fileData, responseHeaders, HttpStatus.OK);
   }
}