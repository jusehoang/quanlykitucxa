package student.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import student.Room;

@Repository
public interface RoomRepository extends CrudRepository<Room , String>{
}
