package student;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;
import student.Room;
import student.Student;

@Data
@Entity
@NoArgsConstructor(force = true)
public class Roombill {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
	@GenericGenerator(name = "native",strategy = "native")
    private Long id ;
    private Date createdAt;
    private Date timeEnd;

    private boolean status;


    @OneToOne
    private Room room;

    @OneToOne
    private Student student;

    private boolean isPay;
    @PrePersist
	void createdAt() {
		this.createdAt = new Date();
    }
    
    
}
