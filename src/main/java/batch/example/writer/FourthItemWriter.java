package batch.example.writer;

import batch.example.model.StudentXml;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FourthItemWriter implements ItemWriter<StudentXml> {
    @Override
    public void write(List<? extends StudentXml> items) throws Exception {
        System.out.println("Inside Item writer");
        items.stream()
             .forEach(System.out::println);
    }
}
