package student;

import java.security.Provider.Service;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class serviceOrder {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
	@GenericGenerator(name = "native",strategy = "native")
    private Long id;
    
    private Date buyAt ;

    private boolean status;

    private Date end;

    @ManyToOne
    @JoinColumn(name = "sv_id")
    private Student student ;
    

    @ManyToOne
    @JoinColumn(name = "service_id")
    private service Service;

    @PrePersist
    void buyAt(){
        this.buyAt = new Date();
    }



} 
