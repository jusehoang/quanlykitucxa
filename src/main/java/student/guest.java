package student;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor(force = true)
public class guest {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
	@GenericGenerator(name = "native",strategy = "native")
    private Long guest_id;
    @NotBlank(message = "CMT is not blank")
    private String CMT;
    @NotBlank(message = "Name is not blank")
    private String Name ;
    @DateTimeFormat (pattern="yyyy-MM-dd")
    private Date dateOfBirth ;
    @DateTimeFormat (pattern="yyyy-MM-dd")
    private Date date;
    private String StudentID ;
}
