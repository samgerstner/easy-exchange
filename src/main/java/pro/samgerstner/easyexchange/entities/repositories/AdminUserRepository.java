package pro.samgerstner.easyexchange.entities.repositories;

import org.springframework.data.repository.CrudRepository;
import pro.samgerstner.easyexchange.entities.AdminUser;

public interface AdminUserRepository extends CrudRepository<AdminUser, Integer>
{
}