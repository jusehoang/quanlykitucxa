package student;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;


@Data
@NoArgsConstructor(access=AccessLevel.PUBLIC, force=true)
@Entity
@Table(name = "student")
public class Student {
	@Id
	@NotBlank(message = "ID is blank")
	private String sv_id;
	@NotBlank(message = "CMT is blank")
	private String sv_cmt;
	private Date sv_date;
	@NotBlank(message = "Class field is blank")
	private String sv_class;
	@NotBlank(message = "Country field is blank")
	private String sv_country;

	@OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="pc_sv_id" , referencedColumnName = "sv_id")
	private List<guest> guests = new ArrayList<>(); 
}
