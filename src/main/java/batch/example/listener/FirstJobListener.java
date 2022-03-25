package batch.example.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstJobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Before job " + jobExecution.getJobInstance()
                                                       .getJobName());
        System.out.println("Before param " + jobExecution.getJobParameters());
        System.out.println("Before ex context " + jobExecution.getExecutionContext());
        jobExecution.getExecutionContext()
                    .put("name", "pakin");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("After job " + jobExecution.getJobInstance()
                                                      .getJobName());
        System.out.println("After param " + jobExecution.getJobParameters());
        System.out.println("After ex context " + jobExecution.getExecutionContext());
    }
}
