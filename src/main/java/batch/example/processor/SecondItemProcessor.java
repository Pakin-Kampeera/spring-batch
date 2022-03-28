package batch.example.processor;

import batch.example.model.StudentJdbc;
import batch.example.model.StudentJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecondItemProcessor implements ItemProcessor<StudentJdbc, StudentJson> {
    @Override
    public StudentJson process(StudentJdbc studentJdbc) throws Exception {
        log.info("Inside Item processor");
        return StudentJson.builder()
                          .id(studentJdbc.getId())
                          .email(studentJdbc.getEmail())
                          .firstName(studentJdbc.getFirstName())
                          .lastName(studentJdbc.getLastName())
                          .build();
    }
}
