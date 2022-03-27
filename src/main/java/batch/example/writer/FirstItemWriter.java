package batch.example.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FirstItemWriter implements ItemWriter<Long> {
    @Override
    public void write(List<? extends Long> items) throws Exception {
        log.info("Inside Item writer");
        items.stream()
             .forEach(System.out::println);
    }
}
