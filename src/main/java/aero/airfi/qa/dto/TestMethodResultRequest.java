package aero.airfi.qa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestMethodResultRequest {
    private String runId;
    private String suiteType;
    private String featureName;
    private String methodName;
    private String className;
    private String status;
    private String assertType;
    private Instant startTime;
    private Instant endTime;
    private long durationMs;
    private List<String> jiraTestCases;
    private EnvironmentInfoDto environment;
    private List<String> logs;
    private List<String> screenshotUrls;
    private List<FailureDetailDto> failures;
}


