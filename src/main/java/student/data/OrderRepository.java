package student.data;

import org.springframework.data.repository.CrudRepository;

import student.serviceOrder;

public interface OrderRepository extends CrudRepository<serviceOrder , Long>{
    
}
