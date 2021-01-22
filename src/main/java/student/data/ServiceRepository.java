package student.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import student.service;

@Repository 
public interface ServiceRepository extends CrudRepository<service, Long>{
    
}
