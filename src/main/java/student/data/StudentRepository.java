package student.data;

import org.springframework.data.repository.CrudRepository;

import student.Student;

public interface StudentRepository extends CrudRepository<Student,String>{
}
