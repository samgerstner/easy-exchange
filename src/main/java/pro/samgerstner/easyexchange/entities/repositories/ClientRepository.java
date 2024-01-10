package pro.samgerstner.easyexchange.entities.repositories;

import org.springframework.data.repository.CrudRepository;
import pro.samgerstner.easyexchange.entities.Client;

public interface ClientRepository extends CrudRepository<Client, Integer>
{
}