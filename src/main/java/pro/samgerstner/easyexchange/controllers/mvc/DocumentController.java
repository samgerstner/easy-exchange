package pro.samgerstner.easyexchange.controllers.mvc;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pro.samgerstner.easyexchange.helpers.AuthorizationHelper;
import pro.samgerstner.easyexchange.helpers.S3Helper;
import pro.samgerstner.easyexchange.entities.AuthorizationStatus;
import pro.samgerstner.easyexchange.entities.Document;
import pro.samgerstner.easyexchange.entities.repositories.DocumentRepository;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/documents")
public class DocumentController
{
   @Value("${application.title}")
   private String title;

   @Autowired
   private DocumentRepository docRepo;

   @Value("${aws.access-key}")
   private String accessKey;

   @Value("${aws.secret-key}")
   private String secretKey;

   @Value("${aws.region}")
   private String region;

   @Value("${aws.bucket-name}")
   private String bucketName;

   private final String[] allowedRoles = {"Administrator", "Super Admin"};

   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public String badRequest()
   {
      return "Bad request";
   }

   @GetMapping(value = "/view")
   public String view(@RequestParam(required = false) String search, @RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "guid,asc") String[] sort,
                      Model model, HttpSession session)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      model.addAttribute("appTitle", title);
      String sortField = sort[0];
      String sortDirection = sort[1];
      Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
      Sort.Order order = new Sort.Order(direction, sortField);
      Pageable pageable = PageRequest.of(page - 1, size, Sort.by(order));

      Page<Document> pageDocs;
      if(search == null)
      {
         pageDocs = docRepo.findAllPageable(pageable);
      }
      else
      {
         pageDocs = docRepo.findByGuidOrClientOrSession(search, pageable);
         model.addAttribute("search", search);
      }

      List<Document> docs = pageDocs.getContent();
      model.addAttribute("docs", docs);
      model.addAttribute("currentPage", pageDocs.getNumber() + 1);
      model.addAttribute("totalItems", pageDocs.getTotalElements());
      model.addAttribute("totalPages", pageDocs.getTotalPages());
      model.addAttribute("pageSize", size);
      model.addAttribute("sortField", sortField);
      model.addAttribute("sortDirection", sortDirection);
      model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
      return "document_view";
   }

   @GetMapping(value = "/admin-download")
   public ResponseEntity<byte[]> adminDownload(HttpSession session, @RequestParam String guid, @RequestParam String nonce)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus, true) : null;
      HttpHeaders authHeaders = new HttpHeaders();
      authHeaders.add("Location", redirect);
      if(redirect != null){ return new ResponseEntity<>(authHeaders, HttpStatus.OK); }

      Optional<Document> docOptional = docRepo.findById(guid);
      if(docOptional.isEmpty())
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      Document doc = docOptional.get();

      if(!doc.getDownloadNonce().equals(nonce))
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      S3Helper s3 = new S3Helper(accessKey, secretKey, region, bucketName);
      byte[] responseBody = s3.downloadSessionFile(doc.getUploadSession().getGuid(), doc.getFileName());
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType(doc.getFileType()));
      headers.set("Content-Disposition", "attachment;filename=" + doc.getFileName());
      return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
   }

   @GetMapping(value = "/public-download")
   public ResponseEntity<byte[]> publicDownload(HttpSession session)
   {
      //Get a list of session attributes
      Enumeration<String> sessionAttributesRaw = session.getAttributeNames();
      List<String> sessionAttributes = new ArrayList<String>();
      while(sessionAttributesRaw.hasMoreElements())
      {
         sessionAttributes.add(sessionAttributesRaw.nextElement());
      }

      //Verify required session attributes exist
      if(!sessionAttributes.contains("X-Document-GUID") || !sessionAttributes.contains("X-Download-Nonce"))
      {
         System.out.println("Error: Missing required session attribute.");
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      System.out.printf("\nX-Document-GUID: %s\nX-Download-Nonce: %s\n", session.getAttribute("X-Document-GUID").toString(),
              session.getAttribute("X-Download-Nonce").toString());

      //Get document from the repo
      Optional<Document> docOptional = docRepo.findById(session.getAttribute("X-Document-GUID").toString());
      if(docOptional.isEmpty())
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      Document doc = docOptional.get();

      //Verify the download nonce matches
      if(!doc.getDownloadNonce().equals(session.getAttribute("X-Download-Nonce").toString()))
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      S3Helper s3 = new S3Helper(accessKey, secretKey, region, bucketName);
      byte[] responseBody = s3.downloadSessionFile(doc.getUploadSession().getGuid(), doc.getFileName());
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.setContentType(MediaType.parseMediaType(doc.getFileType()));
      responseHeaders.set("Content-Disposition", "attachment;filename=" + doc.getFileName());
      session.removeAttribute("X-Document-GUID");
      session.removeAttribute("X-Download-Nonce");
      return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
   }

   @GetMapping(value = "/delete")
   public String getDelete(HttpSession session, @RequestParam String guid, Model model)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      Optional<Document> docOptional = docRepo.findById(guid);
      if(docOptional.isEmpty())
      {
         return "redirect:/documents/bad-request";
      }

      model.addAttribute("appTitle", title);
      model.addAttribute("doc", docOptional.get());
      return "document_delete";
   }

   @PostMapping(value = "/delete")
   public String postDelete(HttpSession session, @RequestParam String guid)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      Optional<Document> docOptional = docRepo.findById(guid);
      if(docOptional.isEmpty())
      {
         return "redirect:/documents/bad-request";
      }

      Document doc = docOptional.get();
      S3Helper s3 = new S3Helper(accessKey, secretKey, region, bucketName);
      s3.deleteSessionFile(doc.getUploadSession().getGuid(), doc.getFileName());
      docRepo.deleteById(guid);
      return "redirect:/documents/view";
   }
}