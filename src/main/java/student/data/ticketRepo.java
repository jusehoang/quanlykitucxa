package student.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import student.ticket;

@Repository
public interface ticketRepo extends CrudRepository<ticket , String>{
    
}
