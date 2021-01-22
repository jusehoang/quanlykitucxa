package student.data;

import org.springframework.data.repository.CrudRepository;

import student.Roombill;

public interface BillRoomRepository extends CrudRepository<Roombill, Long> {
    
}
