package pro.samgerstner.easyexchange.entities.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import pro.samgerstner.easyexchange.entities.UploadSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UploadSessionRepository extends CrudRepository<UploadSession, String>, PagingAndSortingRepository<UploadSession, String>
{
   @Query("select s from UploadSession s where s.guid like %?1% or s.client.email like %?1%")
   Page<UploadSession> findByGuidOrClientEmail(String search, Pageable pageable);

   @Query("select s from UploadSession s")
   Page<UploadSession> findAllPageable(Pageable pageable);

   @Query("select s from UploadSession s where s.guid like %?1% and s.client.email like %?2%")
   UploadSession[] findByGuidAndClientEmail(String guid, String clientEmail);
}