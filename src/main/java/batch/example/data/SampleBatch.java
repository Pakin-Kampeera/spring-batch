package batch.example.data;

import batch.example.listener.FirstJobListener;
import batch.example.listener.FirstStepListener;
import batch.example.model.StudentCsv;
import batch.example.processor.FirstItemProcessor;
import batch.example.reader.FirstItemReader;
import batch.example.service.SecondTasklet;
import batch.example.writer.FirstItemWriter;
import batch.example.writer.SecondItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Configuration
public class SampleBatch {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SecondTasklet secondTasklet;
    private final FirstJobListener firstJobListener;
    private final FirstStepListener firstStepListener;
    private final FirstItemReader firstItemReader;
    private final FirstItemProcessor firstItemProcessor;
    private final FirstItemWriter firstItemWriter;
    private final SecondItemWriter secondItemWriter;

    public SampleBatch(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, SecondTasklet secondTasklet, FirstJobListener firstJobListener, FirstStepListener firstStepListener, FirstItemReader firstItemReader, FirstItemProcessor firstItemProcessor, FirstItemWriter firstItemWriter, SecondItemWriter secondItemWriter) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.secondTasklet = secondTasklet;
        this.firstJobListener = firstJobListener;
        this.firstStepListener = firstStepListener;
        this.firstItemReader = firstItemReader;
        this.firstItemProcessor = firstItemProcessor;
        this.firstItemWriter = firstItemWriter;
        this.secondItemWriter = secondItemWriter;
    }

    @Bean
    public Job firstJob() {
        return jobBuilderFactory.get("First Job")
                                .incrementer(new RunIdIncrementer())
                                .listener(firstJobListener)
                                .start(firstStep())
                                .next(secondStep())
                                .build();
    }

    @Bean
    public Job secondJob() {
        return jobBuilderFactory.get("Second Job")
                                .incrementer(new RunIdIncrementer())
                                .start(secondChunkStep())
                                .build();
    }

    private Step firstStep() {
        return stepBuilderFactory.get("First Step")
                                 .listener(firstStepListener)
                                 .tasklet(firstTask())
                                 .build();
    }

    private Step secondStep() {
        return stepBuilderFactory.get("Second Step")
                                 .tasklet(secondTasklet)
                                 .build();
    }

    private Tasklet firstTask() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("This is first tasklet step");
                System.out.println(chunkContext.getStepContext()
                                               .getStepExecutionContext()
                                               .get("lastName"));
                return RepeatStatus.FINISHED;
            }
        };
    }

    private Step firstChunkStep() {
        return stepBuilderFactory.get("First Chunk Step")
                                 .<Integer, Long>chunk(3)
                                 .reader(firstItemReader)
                                 .processor(firstItemProcessor)
                                 .writer(firstItemWriter)
                                 .build();
    }

    private Step secondChunkStep() {
        return stepBuilderFactory.get("Second Chunk Step")
                                 .<StudentCsv, StudentCsv>chunk(3)
                                 .reader(flatFileItemReader())
                                 .writer(secondItemWriter)
                                 .build();
    }

    public FlatFileItemReader<StudentCsv> flatFileItemReader() {
        FlatFileItemReader<StudentCsv> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(new File("InputFiles/students.csv")));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[]{"ID", "First name", "Last name", "Email"});
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
                    {
                        setTargetType(StudentCsv.class);
                    }
                });
            }
        });

        return reader;
    }
}
