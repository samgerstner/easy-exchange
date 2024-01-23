package pro.samgerstner.easyexchange.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import pro.samgerstner.easyexchange.entities.Client;

import java.util.List;

public interface ClientRepository extends CrudRepository<Client, Integer>, PagingAndSortingRepository<Client, Integer>
{
   @Query("select c from Client c where c.firstName like %?1% or c.lastName like %?1% or c.email like %?1%")
   Page<Client> findByFirstNameOrLastNameOrEmail(String search, Pageable pageable);

   @Query("select c from Client c")
   Page<Client> findAllPageable(Pageable pageable);

   @Query("select c from Client c where c.firstName like %?1% and c.lastName like %?2% and c.email like %?3% and c.id = ?4")
   Client[] findByFirstNameAndLastNameAndEmailAndId(String firstName, String lastName, String email, int id);
}