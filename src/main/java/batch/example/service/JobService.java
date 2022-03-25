package batch.example.service;

import batch.example.request.JobParamsRequest;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobService {

    private final JobLauncher jobLauncher;
    private final Job firstJob;
    private final Job secondJob;

    public JobService(JobLauncher jobLauncher, @Qualifier("firstJob") Job firstJob, @Qualifier("secondJob") Job secondJob) {
        this.jobLauncher = jobLauncher;
        this.firstJob = firstJob;
        this.secondJob = secondJob;
    }

    @Async
    public void startJob(String jobName, List<JobParamsRequest> jobParamsRequests) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Map<String, JobParameter> params = new HashMap<>();
        params.put("currentTime", new JobParameter(System.currentTimeMillis()));

        jobParamsRequests.stream()
                         .forEach(jobParamsRequest -> params.put(jobParamsRequest.getParamKey(), new JobParameter(jobParamsRequest.getParamValue())));

        JobParameters jobParameters = new JobParameters(params);

        try {
            JobExecution jobExecution = null;
            if (jobName.equals("First Job")) {
                jobExecution = jobLauncher.run(firstJob, jobParameters);
            } else if (jobName.equals("Second Job")) {
                jobExecution = jobLauncher.run(secondJob, jobParameters);
            }
            System.out.println("Job execution ID: " + jobExecution.getId());
        } catch (Exception e) {
            System.out.println("Exception while starting job...");
        }
    }
}
