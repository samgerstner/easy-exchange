package pro.samgerstner.easyexchange.entities.repositories;

import org.springframework.data.repository.CrudRepository;
import pro.samgerstner.easyexchange.entities.UploadSession;

public interface UploadSessionRepository extends CrudRepository<UploadSession, String>
{
}