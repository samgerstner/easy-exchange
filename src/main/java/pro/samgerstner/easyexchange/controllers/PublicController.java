package pro.samgerstner.easyexchange.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pro.samgerstner.easyexchange.S3Helper;
import pro.samgerstner.easyexchange.entities.Document;
import pro.samgerstner.easyexchange.entities.DownloadRequest;
import pro.samgerstner.easyexchange.entities.UploadRequest;
import pro.samgerstner.easyexchange.entities.UploadSession;
import pro.samgerstner.easyexchange.entities.repositories.DocumentRepository;
import pro.samgerstner.easyexchange.entities.repositories.UploadSessionRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PublicController
{
   @Value("${application.title}")
   private String title;

   @Autowired
   private DocumentRepository docRepo;

   @Autowired
   private UploadSessionRepository sessionRepo;

   @Value("${aws.access-key}")
   private String accessKey;

   @Value("${aws.secret-key}")
   private String secretKey;

   @Value("${aws.region}")
   private String region;

   @Value("${aws.bucket-name}")
   private String bucketName;

   @GetMapping(value = "download")
   public String getDownload(Model model)
   {
      model.addAttribute("appTitle", title);
      model.addAttribute("downloadReq", new DownloadRequest());
      model.addAttribute("errorMsg", "");
      return "download";
   }

   @PostMapping(value = "download")
   public String postDownload(@ModelAttribute DownloadRequest req, Model model, HttpServletResponse response)
   {
      //Verify that the document exists
      Optional<Document> docOptional = docRepo.findById(req.getDocGUID());
      if(docOptional.isEmpty())
      {
         model.addAttribute("appTitle", title);
         model.addAttribute("downloadReq", req);
         model.addAttribute("errorMsg", "A document with the provided GUID does not exist.");
         return "download";
      }
      Document doc = docOptional.get();

      //Verify the provided session GUID matches
      if(!req.getSessionGUID().equals(doc.getUploadSession().getGuid()))
      {
         model.addAttribute("appTitle", title);
         model.addAttribute("downloadReq", req);
         model.addAttribute("errorMsg", "The provided upload session GUID does not match our records.");
         return "download";
      }

      //Verify that the provided client email matches
      if(!req.getClientEmail().equals(doc.getUploadSession().getClient().getEmail()))
      {
         model.addAttribute("appTitle", title);
         model.addAttribute("downloadReq", req);
         model.addAttribute("errorMsg", "The provided client email address does not match our records.");
         return "download";
      }

      //Verify that download is not locked
      if(doc.getUploadSession().isDownloadLocked())
      {
         model.addAttribute("appTitle", title);
         model.addAttribute("downloadReq", req);
         model.addAttribute("errorMsg", "File download is not allowed at this time. Please contact your administrator.");
         return "download";
      }

      response.addHeader("X-Document-GUID", doc.getGuid());
      response.addHeader("X-Download-Nonce", doc.getDownloadNonce());
      return "redirect:/documents/public-download";
   }

   @GetMapping(value = "/upload")
   public String getUpload(Model model)
   {
      model.addAttribute("appTitle", title);
      model.addAttribute("uploadReq", new UploadRequest());
      model.addAttribute("errorMsg", "");
      model.addAttribute("uploadSuccess", false);
      return "upload";
   }

   @PostMapping(value = "/upload")
   public String postUpload(@ModelAttribute UploadRequest req, Model model) throws IOException
   {
      //Verify session exists
      Optional<UploadSession> sessionOptional = sessionRepo.findById(req.getSessionGUID());
      if(sessionOptional.isEmpty())
      {
         model.addAttribute("appTitle", title);
         model.addAttribute("uploadReq", req);
         model.addAttribute("errorMsg", "An upload session with the provided GUID does not exist.");
         model.addAttribute("uploadSuccess", false);
         return "upload";
      }
      UploadSession session = sessionOptional.get();

      //Verify client email matches
      if(!session.getClient().getEmail().equals(req.getClientEmail()))
      {
         model.addAttribute("appTitle", title);
         model.addAttribute("uploadReq", req);
         model.addAttribute("errorMsg", "The provided client email does not match our records.");
         model.addAttribute("uploadSuccess", false);
         return "upload";
      }

      //Verify that upload is not locked
      if(session.isUploadLocked())
      {
         model.addAttribute("appTitle", title);
         model.addAttribute("uploadReq", req);
         model.addAttribute("errorMsg", "File upload is not allowed at this time. Please contact your administrator.");
         model.addAttribute("uploadSuccess", false);
         return "upload";
      }

      //Upload document to S3
      S3Helper s3 = new S3Helper(accessKey, secretKey, region, bucketName);
      s3.uploadSessionFile(req.getSessionGUID(), req.getFile().getName(), req.getFile().getBytes());

      //Build document object
      Document newDoc = new Document();
      newDoc.setGuid(UUID.randomUUID().toString());
      newDoc.setFileName(req.getFile().getName());
      newDoc.setFileType(req.getFile().getContentType());
      newDoc.setUploadedAt(new SimpleDateFormat("MM-dd-yyyy HH:mm").format(new Date()));
      newDoc.setDownloadNonce(s3.generateDownloadNonce());
      docRepo.save(newDoc);

      model.addAttribute("appTitle", title);
      model.addAttribute("uploadReq", req);
      model.addAttribute("errorMsg", "");
      model.addAttribute("uploadSuccess", true);
      return "upload";
   }
}