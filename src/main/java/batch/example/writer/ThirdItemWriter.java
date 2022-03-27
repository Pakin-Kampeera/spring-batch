package batch.example.writer;

import batch.example.model.StudentJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ThirdItemWriter implements ItemWriter<StudentJson> {
    @Override
    public void write(List<? extends StudentJson> items) throws Exception {
        log.info("Inside Item writer");
        items.stream()
            .forEach(System.out::println);
    }
}
