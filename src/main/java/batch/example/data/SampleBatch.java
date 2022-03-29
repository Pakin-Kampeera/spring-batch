package batch.example.data;

import batch.example.listener.FirstJobListener;
import batch.example.listener.FirstStepListener;
import batch.example.model.StudentCsv;
import batch.example.model.StudentJdbc;
import batch.example.model.StudentJson;
import batch.example.model.StudentXml;
import batch.example.processor.FirstItemProcessor;
import batch.example.processor.SecondItemProcessor;
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
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.CompositeSkipPolicy;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.NeverSkipItemSkipPolicy;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
import java.util.Date;

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
    private final SecondItemProcessor secondItemProcessor;
    private final DataSource dataSource;

    public SampleBatch(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, SecondTasklet secondTasklet, FirstJobListener firstJobListener, FirstStepListener firstStepListener, FirstItemReader firstItemReader, FirstItemProcessor firstItemProcessor, FirstItemWriter firstItemWriter, SecondItemWriter secondItemWriter, ThirdItemWriter thirdItemWriter, FourthItemWriter fourthItemWriter, FifthItemWriter fifthItemWriter, SixItemWriter sixItemWriter, StudentService studentService, SecondItemProcessor secondItemProcessor, DataSource dataSource) {
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
        this.secondItemProcessor = secondItemProcessor;
        this.dataSource = dataSource;
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
                                 .<StudentCsv, StudentJson>chunk(3)
                                 .reader(flatFileItemReader(null))
//                                 .reader(jsonItemReader(null))
//                                 .reader(studentXmlStaxEventItemReader(null))
//                                 .reader(jdbcJdbcCursorItemReader())
//                                 .reader(jdbcJdbcCursorItemReader())
//                                 .processor(secondItemProcessor)
                                 .writer(jsonJsonFileItemWriter(null))
//                                 .writer(flatFileItemWriter(null))
//                                 .writer(staxEventItemWriter(null))
//                                 .writer(jdbcBatchItemWriter())
//                                 .writer(jdbcBatchItemWriter1())
//                                 .writer(itemWriterAdapter())
                                 .faultTolerant()
                                 .skip(FlatFileParseException.class)
//                                 .skipLimit(Integer.MAX_VALUE)
                                 .skipPolicy(new AlwaysSkipItemSkipPolicy())
                                 .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<StudentCsv> flatFileItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
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
        return reader;
    }

    @Bean
    @StepScope
    public JsonItemReader<StudentJson> jsonItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
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

    @Bean
    @StepScope
    public FlatFileItemWriter<StudentJdbc> flatFileItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        FlatFileItemWriter<StudentJdbc> flatFileItemWriter = new FlatFileItemWriter<>();
        flatFileItemWriter.setResource(fileSystemResource);
        flatFileItemWriter.setHeaderCallback(writer -> writer.write("Id,FirstName,LastName,Email"));

        BeanWrapperFieldExtractor beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();
        beanWrapperFieldExtractor.setNames(new String[]{"Id", "firstName", "lastName", "email"});

        DelimitedLineAggregator<StudentJdbc> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);

        flatFileItemWriter.setLineAggregator(delimitedLineAggregator);
        flatFileItemWriter.setFooterCallback(writer -> writer.write("Created @ " + new Date()));
        return flatFileItemWriter;
    }

    @Bean
    @StepScope
    public JsonFileItemWriter<StudentJson> jsonJsonFileItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        JsonFileItemWriter<StudentJson> jsonFileItemWriter = new JsonFileItemWriter<>(fileSystemResource, new JacksonJsonObjectMarshaller<>());
        return jsonFileItemWriter;
    }

    @Bean
    @StepScope
    public StaxEventItemWriter<StudentJdbc> staxEventItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        StaxEventItemWriter<StudentJdbc> staxEventItemWriter = new StaxEventItemWriter<>();
        staxEventItemWriter.setResource(fileSystemResource);
        staxEventItemWriter.setRootTagName("students");

        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(StudentJdbc.class);

        staxEventItemWriter.setMarshaller(jaxb2Marshaller);
        return staxEventItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setDataSource(dataSource);
        jdbcBatchItemWriter.setSql("insert into student(id, first_name, last_name, email) values (:id, :firstName, :lastName, :email)");
        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return jdbcBatchItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter1() {
        JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setDataSource(dataSource);
        jdbcBatchItemWriter.setSql("insert into student(id, first_name, last_name, email) values (?,?,?,?)");
        jdbcBatchItemWriter.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getFirstName());
            ps.setString(3, item.getLastName());
            ps.setString(4, item.getEmail());
        });
        return jdbcBatchItemWriter;
    }

    public ItemWriterAdapter<StudentResponse> itemWriterAdapter() {
        ItemWriterAdapter<StudentResponse> itemWriterAdapter = new ItemWriterAdapter<>();
        itemWriterAdapter.setTargetObject(studentService);
        itemWriterAdapter.setTargetMethod("restCallToPostStudents");
        return itemWriterAdapter;
    }
}
