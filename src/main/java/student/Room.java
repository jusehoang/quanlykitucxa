package student;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;


@Data
@NoArgsConstructor
@Entity
public class Room { 
    @Id
    @NotBlank(message = "ID is blank")
    private String id ;
    @NotBlank(message = "Type is blank")
    private String type;
    @NotNull
    private int price;
    @NotNull
    private int maximum ;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="pc_fid" , referencedColumnName = "id")
    List<Student> students = new ArrayList<>();

}
