package batch.example.processor;

import batch.example.model.StudentCsv;
import batch.example.model.StudentJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecondItemProcessor implements ItemProcessor<StudentCsv, StudentJson> {
    @Override
    public StudentJson process(StudentCsv studentJdbc) throws Exception {
//        if (studentJdbc.getId() == 6) {
//            throw new NullPointerException();
//        }
        log.info("Inside Item processor");
        return StudentJson.builder()
                          .id(studentJdbc.getId())
                          .email(studentJdbc.getEmail())
                          .firstName(studentJdbc.getFirstName())
                          .lastName(studentJdbc.getLastName())
                          .build();
    }
}
