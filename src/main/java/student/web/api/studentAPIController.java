package student.web.api;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import student.data.*;
import student.*;
@RestController
@RequestMapping(path = "getall" , produces = "application/json")
@CrossOrigin(origins = "*")
public class studentAPIController {
    private StudentRepository stuRepo;
    @Autowired
    EntityLinks entityLinks ;
    public studentAPIController(StudentRepository stuRepo) {
        this.stuRepo = stuRepo;
    }
    
    @GetMapping
    public Iterable<Student> getAllStudent(){
        return stuRepo.findAll();
    }

    @GetMapping("/{sv_id}")
    public Student getByID(@PathVariable("sv_id") String id){
        Optional<Student> optStudent = stuRepo.findById(id);
        if(optStudent.isPresent()){
            return optStudent.get();
        }
        return null;
    }
}
