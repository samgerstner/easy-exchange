package pro.samgerstner.easyexchange.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import pro.samgerstner.easyexchange.entities.Document;

public interface DocumentRepository extends CrudRepository<Document, String>, PagingAndSortingRepository<Document, String>
{
   @Query("select d from Document d where d.guid like %?1% or d.uploadSession.guid like %?1% or d.uploadSession.client.email like %?1%")
   Page<Document> findByGuidOrClientOrSession(String search, Pageable pageable);

   @Query("select d from Document d")
   Page<Document> findAllPageable(Pageable pageable);

   @Query("select d from Document d where d.guid like %?1% and d.uploadSession.guid like %?2% and d.uploadSession.client.email like %?3%")
   Document[] findByGuidAndSessionGuidAndClientEmail(String guid, String sessionGuid, String clientEmail);
}