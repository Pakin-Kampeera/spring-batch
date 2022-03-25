package batch.example.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SecondJobScheduler {
    private final JobLauncher jobLauncher;
    private final Job secondJob;

    public SecondJobScheduler(JobLauncher jobLauncher, @Qualifier("secondJob") Job secondJob) {
        this.jobLauncher = jobLauncher;
        this.secondJob = secondJob;
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void secondJobScheduler() {
        Map<String, JobParameter> params = new HashMap<>();
        params.put("currentTime", new JobParameter(System.currentTimeMillis()));

        JobParameters jobParameters = new JobParameters(params);

        try {
            JobExecution jobExecution = jobLauncher.run(secondJob, jobParameters);
            System.out.println("Job execution ID: " + jobExecution.getId());
        } catch (Exception e) {
            System.out.println("Exception while starting job...");
        }
    }
}
