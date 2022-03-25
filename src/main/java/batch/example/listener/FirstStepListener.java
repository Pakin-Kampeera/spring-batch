package batch.example.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Before step " + stepExecution.getStepName());
        System.out.println("Job exec context " + stepExecution.getJobExecution()
                .getExecutionContext());
        System.out.println("Step exec context " + stepExecution.getExecutionContext());
        stepExecution.getExecutionContext()
                .put("lastName", "Kampeera");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("After step " + stepExecution.getStepName());
        System.out.println("Job exec context " + stepExecution.getJobExecution()
                .getExecutionContext());
        System.out.println("Step exec context " + stepExecution.getExecutionContext());
        return null;
    }
}
