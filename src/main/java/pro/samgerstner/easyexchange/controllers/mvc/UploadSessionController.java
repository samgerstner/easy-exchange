package pro.samgerstner.easyexchange.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pro.samgerstner.easyexchange.helpers.AuthorizationHelper;
import pro.samgerstner.easyexchange.helpers.S3Helper;
import pro.samgerstner.easyexchange.entities.AuthorizationStatus;
import pro.samgerstner.easyexchange.entities.UploadSession;
import pro.samgerstner.easyexchange.entities.repositories.ClientRepository;
import pro.samgerstner.easyexchange.entities.repositories.UploadSessionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping(value = "/upload-sessions")
public class UploadSessionController
{
   @Value("${application.title}")
   private String title;

   @Autowired
   private UploadSessionRepository sessionRepo;

   @Autowired
   private ClientRepository clientRepo;

   @Value("${aws.access-key}")
   private String accessKey;

   @Value("${aws.secret-key}")
   private String secretKey;

   @Value("${aws.region}")
   private String region;

   @Value("${aws.bucket-name}")
   private String bucketName;

   private final String[] allowedRoles = {"Session Manager", "Administrator", "Super Admin"};

   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public String badRequest()
   {
      return "Bad request";
   }

   @GetMapping(value = "/create")
   public String getCreate(HttpSession session, Model model)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      model.addAttribute("appTitle", title);
      model.addAttribute("us", new UploadSession());
      model.addAttribute("clients", clientRepo.findAll());
      return "session_create";
   }

   @PostMapping(value = "/create")
   public String postCreate(HttpSession httpSession, @ModelAttribute UploadSession session)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(httpSession, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      session.setGuid(UUID.randomUUID().toString());
      S3Helper s3 = new S3Helper(accessKey, secretKey, region, bucketName);
      s3.createUploadSessionFolder(session.getGuid());
      sessionRepo.save(session);
      return "redirect:/upload-sessions/view";
   }

   @GetMapping(value = "/edit")
   public String getEdit(HttpSession session, @RequestParam String guid, Model model)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      Optional<UploadSession> sessionOptional = sessionRepo.findById(guid);

      if(sessionOptional.isEmpty())
      {
         return "redirect:/upload-sessions/bad-request";
      }

      model.addAttribute("appTitle", title);
      model.addAttribute("us", sessionOptional.get());
      model.addAttribute("clients", clientRepo.findAll());
      return "session_edit";
   }

   @PostMapping(value = "/edit")
   public String postEdit(HttpSession httpSession, @RequestParam String guid, @ModelAttribute UploadSession formSession)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(httpSession, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      Optional<UploadSession> sessionOptional = sessionRepo.findById(guid);

      if(sessionOptional.isEmpty())
      {
         return "redirect:/upload-sessions/bad-request";
      }

      UploadSession session = sessionOptional.get();
      session.setMaxDocuments(formSession.getMaxDocuments());
      session.setUploadLocked(formSession.isUploadLocked());
      session.setDownloadLocked(formSession.isDownloadLocked());
      sessionRepo.save(session);
      return "redirect:/upload-sessions/view";
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

      Page<UploadSession> pageSessions;
      if(search == null)
      {
         pageSessions = sessionRepo.findAllPageable(pageable);
      }
      else
      {
         pageSessions = sessionRepo.findByGuidOrClientEmail(search, pageable);
         model.addAttribute("search", search);
      }

      List<UploadSession> sessions = pageSessions.getContent();
      model.addAttribute("usessions", sessions);
      model.addAttribute("currentPage", pageSessions.getNumber() + 1);
      model.addAttribute("totalItems", pageSessions.getTotalElements());
      model.addAttribute("totalPages", pageSessions.getTotalPages());
      model.addAttribute("pageSize", size);
      model.addAttribute("sortField", sortField);
      model.addAttribute("sortDirection", sortDirection);
      model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
      return "session_view";
   }

   @GetMapping(value = "/delete")
   public String getDelete(HttpSession session, @RequestParam String guid, Model model)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      Optional<UploadSession> clientOptional = sessionRepo.findById(guid);

      if(clientOptional.isEmpty())
      {
         return "redirect:/upload-sessions/bad-request";
      }

      model.addAttribute("appTitle", title);
      model.addAttribute("us", clientOptional.get());
      return "session_delete";
   }

   @PostMapping(value = "/delete")
   public String postDelete(HttpSession session, @RequestParam String guid)
   {
      AuthorizationStatus authStatus = AuthorizationHelper.authorizeUserByRole(session, allowedRoles);
      String redirect = authStatus != AuthorizationStatus.AUTHORIZED ? AuthorizationHelper.getAuthorizationRedirect(authStatus) : null;
      if(redirect != null){ return redirect; }

      Optional<UploadSession> clientOptional = sessionRepo.findById(guid);

      if(clientOptional.isEmpty())
      {
         return "redirect:/upload-sessions/bad-request";
      }

      S3Helper s3 = new S3Helper(accessKey, secretKey, region, bucketName);
      s3.deleteUploadSessionFolder(guid);
      sessionRepo.deleteById(guid);
      return "redirect:/upload-sessions/view";
   }
}