package batch.example.writer;

import batch.example.model.StudentJdbc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FifthItemWriter implements ItemWriter<StudentJdbc> {
    @Override
    public void write(List<? extends StudentJdbc> items) throws Exception {
        log.info("Inside Item writer");
        items.stream()
             .forEach(System.out::println);
    }
}
