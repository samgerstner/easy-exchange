package pro.samgerstner.easyexchange.entities.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pro.samgerstner.easyexchange.entities.ApplicationRole;

import java.util.Optional;

public interface ApplicationRoleRepository extends CrudRepository<ApplicationRole, Integer>
{
   @Query("select r from ApplicationRole r where r.roleName = ?1")
   Optional<ApplicationRole> findByName(String name);
}