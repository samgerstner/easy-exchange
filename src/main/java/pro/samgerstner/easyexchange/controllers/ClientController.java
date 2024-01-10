package pro.samgerstner.easyexchange.controllers;

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
import pro.samgerstner.easyexchange.entities.Client;
import pro.samgerstner.easyexchange.entities.repositories.ClientRepository;
import org.springframework.data.domain.Sort.Order;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/clients")
public class ClientController
{
   @Value("${application.title}")
   private String title;

   @Autowired
   private ClientRepository clientRepo;

   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public String badRequest()
   {
      return "Bad request";
   }

   @GetMapping(value = "/create")
   public String getCreate(Model model)
   {
      model.addAttribute("appTitle", title);
      model.addAttribute("client", new Client());
      return "client_create";
   }

   @PostMapping(value = "/create")
   public String postCreate(@ModelAttribute Client client)
   {
      clientRepo.save(client);
      return "redirect:/clients/view";
   }

   @GetMapping(value = "/edit")
   public String getEdit(@RequestParam int id,  Model model)
   {
      Optional<Client> clientOptional = clientRepo.findById(id);

      if(clientOptional.isEmpty())
      {
         return "redirect:/clients/bad-request";
      }

      model.addAttribute("appTitle", title);
      model.addAttribute("client", clientOptional.get());
      return "client_edit";
   }

   @PostMapping(value = "/edit")
   public String postEdit(@RequestParam int id, @ModelAttribute Client formClient)
   {
      Optional<Client> clientOptional = clientRepo.findById(id);

      if(clientOptional.isEmpty())
      {
         return "redirect:/clients/bad-request";
      }

      Client client = clientOptional.get();
      client.setFirstName(formClient.getFirstName());
      client.setLastName(formClient.getLastName());
      client.setEmail(formClient.getEmail());
      clientRepo.save(client);
      return "redirect:/clients/view";
   }

   @GetMapping(value = "/view")
   public String view(@RequestParam(required = false) String search, @RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id,asc") String[] sort,
                      Model model)
   {
      model.addAttribute("appTitle", title);
      String sortField = sort[0];
      String sortDirection = sort[1];
      Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
      Order order = new Order(direction, sortField);
      Pageable pageable = PageRequest.of(page - 1, size, Sort.by(order));

      Page<Client> pageClients;
      if(search == null)
      {
         pageClients = clientRepo.findAllPageable(pageable);
      }
      else
      {
         pageClients = clientRepo.findByFirstNameOrLastNameOrEmail(search, pageable);
         model.addAttribute("search", search);
      }

      List<Client> clients = pageClients.getContent();
      model.addAttribute("clients", clients);
      model.addAttribute("currentPage", pageClients.getNumber() + 1);
      model.addAttribute("totalItems", pageClients.getTotalElements());
      model.addAttribute("totalPages", pageClients.getTotalPages());
      model.addAttribute("pageSize", size);
      model.addAttribute("sortField", sortField);
      model.addAttribute("sortDirection", sortDirection);
      model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
      return "client_view";
   }

   @GetMapping(value = "/delete")
   public String getDelete(@RequestParam int id, Model model)
   {
      Optional<Client> clientOptional = clientRepo.findById(id);

      if(clientOptional.isEmpty())
      {
         return "redirect:/clients/bad-request";
      }

      model.addAttribute("appTitle", title);
      model.addAttribute("client", clientOptional.get());
      return "client_delete";
   }

   @PostMapping(value = "/delete")
   public String postDelete(@RequestParam int id)
   {
      Optional<Client> clientOptional = clientRepo.findById(id);

      if(clientOptional.isEmpty())
      {
         return "redirect:/clients/bad-request";
      }

      clientRepo.deleteById(id);
      return "redirect:/clients/view";
   }
}