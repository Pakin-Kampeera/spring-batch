package batch.example.writer;

import batch.example.model.StudentXml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FourthItemWriter implements ItemWriter<StudentXml> {
    @Override
    public void write(List<? extends StudentXml> items) throws Exception {
        log.info("Inside Item writer");
        items.stream()
             .forEach(System.out::println);
    }
}
