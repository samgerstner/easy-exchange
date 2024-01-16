package pro.samgerstner.easyexchange.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pro.samgerstner.easyexchange.entities.AdminUser;
import java.util.Optional;

public interface AdminUserRepository extends CrudRepository<AdminUser, Integer>
{
   Optional<AdminUser> findByUsername(String username);

   @Query("select u from AdminUser u where u.username like '%?1%'")
   Page<AdminUser> findAllByUsername(String username, Pageable pageable);

   @Query("select u from AdminUser u")
   Page<AdminUser> findAllPageable(Pageable pageable);
}