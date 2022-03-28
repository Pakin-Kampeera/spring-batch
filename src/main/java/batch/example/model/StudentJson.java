package batch.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
public class StudentJson implements Serializable {
    private Long id;
    @JsonProperty("first_name")
    private String firstName;
    private String lastName;
    private String email;
}
