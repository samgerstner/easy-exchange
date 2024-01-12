package pro.samgerstner.easyexchange.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pro.samgerstner.easyexchange.entities.Document;
import pro.samgerstner.easyexchange.entities.DownloadRequest;
import pro.samgerstner.easyexchange.entities.repositories.DocumentRepository;
import java.util.Optional;

@Controller
public class PublicController
{
   @Value("${application.title}")
   private String title;

   @Autowired
   private DocumentRepository docRepo;

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

      response.addHeader("X-Document-GUID", doc.getGuid());
      response.addHeader("X-Download-Nonce", doc.getDownloadNonce());
      return "redirect:/documents/public-download";
   }
}