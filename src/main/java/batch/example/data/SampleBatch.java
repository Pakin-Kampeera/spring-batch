package batch.example.data;

import batch.example.listener.FirstJobListener;
import batch.example.listener.FirstStepListener;
import batch.example.model.StudentCsv;
import batch.example.model.StudentJdbc;
import batch.example.model.StudentJson;
import batch.example.model.StudentXml;
import batch.example.processor.FirstItemProcessor;
import batch.example.reader.FirstItemReader;
import batch.example.response.StudentResponse;
import batch.example.service.SecondTasklet;
import batch.example.service.StudentService;
import batch.example.writer.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

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
    private final ThirdItemWriter thirdItemWriter;
    private final FourthItemWriter fourthItemWriter;
    private final FifthItemWriter fifthItemWriter;
    private final SixItemWriter sixItemWriter;
    private final StudentService studentService;
    private final DataSource dataSource;

    public SampleBatch(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, SecondTasklet secondTasklet, FirstJobListener firstJobListener, FirstStepListener firstStepListener, FirstItemReader firstItemReader, FirstItemProcessor firstItemProcessor, FirstItemWriter firstItemWriter, SecondItemWriter secondItemWriter, ThirdItemWriter thirdItemWriter, FourthItemWriter fourthItemWriter, FifthItemWriter fifthItemWriter, SixItemWriter sixItemWriter, StudentService studentService, DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.secondTasklet = secondTasklet;
        this.firstJobListener = firstJobListener;
        this.firstStepListener = firstStepListener;
        this.firstItemReader = firstItemReader;
        this.firstItemProcessor = firstItemProcessor;
        this.firstItemWriter = firstItemWriter;
        this.secondItemWriter = secondItemWriter;
        this.thirdItemWriter = thirdItemWriter;
        this.fourthItemWriter = fourthItemWriter;
        this.fifthItemWriter = fifthItemWriter;
        this.sixItemWriter = sixItemWriter;
        this.studentService = studentService;
        this.dataSource = dataSource;
    }

    //    @Bean
//    @Primary
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource dataSource() {
//        return DataSourceBuilder.create()
//                                .build();
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.universitydatasource")
//    public DataSource universityDataSource() {
//        return DataSourceBuilder.create()
//                                .build();
//    }

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
                                 .<StudentResponse, StudentResponse>chunk(3)
//                                 .reader(flatFileItemReader(null))
//                                 .reader(jsonItemReader(null))
//                                 .reader(studentXmlStaxEventItemReader(null))
//                                 .reader(jdbcJdbcCursorItemReader())
                                 .reader(itemReaderAdapter())
                                 .writer(sixItemWriter)
                                 .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<StudentCsv> flatFileItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        System.out.println(fileSystemResource);
        FlatFileItemReader<StudentCsv> reader = new FlatFileItemReader<>();
        reader.setResource(fileSystemResource);
        reader.setLinesToSkip(1);

        DefaultLineMapper<StudentCsv> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames("ID", "First name", "Last name", "Email");
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        BeanWrapperFieldSetMapper<StudentCsv> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(StudentCsv.class);
        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);

        reader.setLineMapper(defaultLineMapper);

//        reader.setLineMapper(new DefaultLineMapper() {
//            {
//                setLineTokenizer(new DelimitedLineTokenizer() {
//                    {
//                        setNames(new String[]{"ID", "First name", "Last name", "Email"});
//                    }
//                });
//                setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
//                    {
//                        setTargetType(StudentCsv.class);
//                    }
//                });
//            }
//        });
        return reader;
    }

    @Bean
    @StepScope
    public JsonItemReader<StudentJson> jsonItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        System.out.println(fileSystemResource);
        JsonItemReader<StudentJson> jsonItemReader = new JsonItemReader<>();
        jsonItemReader.setResource(fileSystemResource);
        jsonItemReader.setJsonObjectReader(new JacksonJsonObjectReader<>(StudentJson.class));
        jsonItemReader.setCurrentItemCount(1);
        jsonItemReader.setMaxItemCount(8);
        return jsonItemReader;
    }

    @Bean
    @StepScope
    public StaxEventItemReader<StudentXml> studentXmlStaxEventItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        StaxEventItemReader<StudentXml> staxEventItemReader = new StaxEventItemReader<>();
        staxEventItemReader.setResource(fileSystemResource);
        staxEventItemReader.setFragmentRootElementName("student");

        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(StudentXml.class);

        staxEventItemReader.setUnmarshaller(jaxb2Marshaller);
        return staxEventItemReader;
    }

    public JdbcCursorItemReader<StudentJdbc> jdbcJdbcCursorItemReader() {
        JdbcCursorItemReader<StudentJdbc> jdbcJdbcCursorItemReader = new JdbcCursorItemReader<>();
        jdbcJdbcCursorItemReader.setDataSource(dataSource);
        jdbcJdbcCursorItemReader.setSql("select id, first_name, last_name, email from student");
        jdbcJdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<>(StudentJdbc.class));
        return jdbcJdbcCursorItemReader;
    }

    public ItemReaderAdapter<StudentResponse> itemReaderAdapter() {
        ItemReaderAdapter<StudentResponse> itemReaderAdapter = new ItemReaderAdapter<>();
        itemReaderAdapter.setTargetObject(studentService);
        itemReaderAdapter.setTargetMethod("getStudent");
        return itemReaderAdapter;
    }
}
