package batch.example.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
