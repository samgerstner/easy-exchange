package pro.samgerstner.easyexchange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.samgerstner.easyexchange.S3Helper;
import pro.samgerstner.easyexchange.entities.Document;
import pro.samgerstner.easyexchange.entities.repositories.DocumentRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(value = "/documents")
public class DocumentController
{
   @Value("${application.title}")
   private String title;

   @Autowired
   private DocumentRepository docRepo;

   @GetMapping(value = "/view")
   public String view(@RequestParam(required = false) String search, @RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id,asc") String[] sort,
                      Model model)
   {
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
   public ResponseEntity<byte[]> adminDownload(@RequestParam String guid, @RequestParam String nonce)
   {

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

      S3Helper s3 = new S3Helper();
      byte[] responseBody = s3.downloadSessionFile(doc.getUploadSession().getGuid(), doc.getFileName());
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType(doc.getFileType()));
      return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
   }

   @GetMapping(value = "/puublic-download")
   public ResponseEntity<byte[]> publicDownload(@RequestHeader Map<String, String> headers)
   {
      //Verify required headers exist
      if(!headers.containsKey("X-Document-GUID") || !headers.containsKey("X-Download-Nonce"))
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      //Get document from the repo
      Optional<Document> docOptional = docRepo.findById(headers.get("X-Document-GUID"));
      if(docOptional.isEmpty())
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      Document doc = docOptional.get();

      //Verify the download nonce matches
      if(!doc.getDownloadNonce().equals(headers.get("X-Download-Nonce")))
      {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      S3Helper s3 = new S3Helper();
      byte[] responseBody = s3.downloadSessionFile(doc.getUploadSession().getGuid(), doc.getFileName());
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.setContentType(MediaType.parseMediaType(doc.getFileType()));
      return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
   }
}