package batch.example.service;

import batch.example.response.StudentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentService {
    @Value("${rest.students.controller.url}")
    private String studentUrl;

    private List<StudentResponse> studentArrays;

    public List<StudentResponse> restCallToGetStudents() {
        log.info("Calling " + studentUrl);
        RestTemplate restTemplate = new RestTemplate();
        StudentResponse[] students = restTemplate.getForObject(studentUrl, StudentResponse[].class);
        studentArrays = Arrays.stream(students)
                              .collect(Collectors.toList());
        return studentArrays;
    }

    public StudentResponse getStudent() {
        if (studentArrays == null) {
            restCallToGetStudents();
        }
        if (studentArrays != null && !studentArrays.isEmpty()) {
            return studentArrays.remove(0);
        }
        return null;
    }
}
