package batch.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Getter
@Setter
@ToString
@XmlRootElement(name = "students")
public class StudentJdbc implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
