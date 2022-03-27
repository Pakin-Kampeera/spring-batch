package batch.example.controller;

import batch.example.response.StudentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class StudentController {

    @GetMapping("/students")
    public List<StudentResponse> students() {
        log.info("Call endpoint /students");
        return Arrays.asList(
                new StudentResponse(1L, "Merle", "Millda", "Merle.Millda@hotmail.com"),
                new StudentResponse(2L, "Karolina", "Millda", "Karolina.Tound@hotmail.com"),
                new StudentResponse(3L, "Bill", "Tound", "Karolina.Tound@hotmail.com"),
                new StudentResponse(4L, "Vita", "Weaks", "Bill.Weaks@hotmail.com"),
                new StudentResponse(5L, "Larine", "Old", "Larine.Carleen@hotmail.com"),
                new StudentResponse(6L, "Krystle", "Carleen", "Krystle.Yusuk@hotmail.com"),
                new StudentResponse(7L, "Paule", "Wenoa", "Paule.Wenoa@hotmail.com"),
                new StudentResponse(8L, "Elmira", "Dudley", "Elmira.Dudley@hotmail.com"),
                new StudentResponse(9L, "Daune", "Dash", "Daune.Dash@hotmail.com"),
                new StudentResponse(10L, "Viviene", "O'Carroll", "Viviene.O'Carroll@hotmail.com")
        );
    }
}
