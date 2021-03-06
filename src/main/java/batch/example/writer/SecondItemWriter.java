package batch.example.writer;

import batch.example.model.StudentCsv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SecondItemWriter implements ItemWriter<StudentCsv> {
    @Override
    public void write(List<? extends StudentCsv> items) throws Exception {
        log.info("Inside Item writer");
        items.stream()
             .forEach(System.out::println);
    }
}
