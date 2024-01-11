package pro.samgerstner.easyexchange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.samgerstner.easyexchange.entities.Document;
import pro.samgerstner.easyexchange.entities.repositories.DocumentRepository;
import java.util.List;

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
}