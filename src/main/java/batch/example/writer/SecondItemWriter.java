package batch.example.writer;

import batch.example.model.StudentCsv;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SecondItemWriter implements ItemWriter<StudentCsv> {
    @Override
    public void write(List<? extends StudentCsv> items) throws Exception {
        System.out.println("Inside Item writer");
        items.stream()
             .forEach(System.out::println);
    }
}
