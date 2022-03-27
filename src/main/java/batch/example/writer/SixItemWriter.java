package batch.example.writer;

import batch.example.response.StudentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SixItemWriter implements ItemWriter<StudentResponse> {
    @Override
    public void write(List<? extends StudentResponse> items) throws Exception {
        log.info("Inside Item writer");
        items.stream()
             .forEach(System.out::println);
    }
}
