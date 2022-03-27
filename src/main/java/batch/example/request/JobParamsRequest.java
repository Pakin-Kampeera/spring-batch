package batch.example.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JobParamsRequest {
    private String paramKey;
    private String paramValue;
}
