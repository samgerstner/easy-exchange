package pro.samgerstner.easyexchange.entities.repositories;

import org.springframework.data.repository.CrudRepository;
import pro.samgerstner.easyexchange.entities.Document;

public interface DocumentRepository extends CrudRepository<Document, String>
{
}