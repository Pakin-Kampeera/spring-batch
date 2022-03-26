package batch.example.writer;

import batch.example.model.StudentJson;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ThirdItemWriter implements ItemWriter<StudentJson> {
    @Override
    public void write(List<? extends StudentJson> items) throws Exception {
        System.out.println("Inside Item writer");
        items.stream()
            .forEach(System.out::println);
    }
}
