package student.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import student.guest;
@Repository
public interface GuestRepository extends CrudRepository<guest , Long>{
    
}
