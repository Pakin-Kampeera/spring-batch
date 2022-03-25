package batch.example.controller;

import batch.example.request.JobParamsRequest;
import batch.example.service.JobService;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/job")
public class JobController {

    private final JobService jobService;
    private final JobOperator jobOperator;

    public JobController(JobService jobService, JobOperator jobOperator) {
        this.jobService = jobService;
        this.jobOperator = jobOperator;
    }

    @GetMapping("/start/{jobName}")
    public String startJob(@PathVariable("jobName") String jobName, @RequestBody List<JobParamsRequest> jobParamsRequests) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        System.out.println(jobParamsRequests.toString());
        jobService.startJob(jobName, jobParamsRequests);
        return "Job starting...";
    }

    @GetMapping("/stop/{jobExecutionId}")
    public String stopJob(@PathVariable("jobExecutionId") Long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        jobOperator.stop(jobExecutionId);
        return "Job stopped...";
    }
}
