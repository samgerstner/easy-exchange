package pro.samgerstner.easyexchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pro.samgerstner.easyexchange.entities.ApplicationRole;
import pro.samgerstner.easyexchange.entities.repositories.ApplicationRoleRepository;
import java.util.Arrays;

@Component
public class InitializeDatabase implements CommandLineRunner
{
   @Autowired
   ApplicationRoleRepository roleRepo;

   @Override
   public void run(String... args) throws Exception
   {
      if(!roleRepo.existsById(1))
      {
         roleRepo.saveAll(Arrays.asList(
                 new ApplicationRole(1, "Client Manager"),
                 new ApplicationRole(2, "Session Manager"),
                 new ApplicationRole(3, "Administrator"),
                 new ApplicationRole(4, "Super Admin")
         ));
      }
   }
}