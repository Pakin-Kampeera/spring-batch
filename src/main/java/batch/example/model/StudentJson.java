package batch.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class StudentJson implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
