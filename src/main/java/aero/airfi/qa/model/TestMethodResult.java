package aero.airfi.qa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "testMethodResults")
public class TestMethodResult {

    @Id
    private String id; // MongoDB ObjectId

    @Field("runId")
    private String runId; // UUID for the entire TestNG run

    @Indexed
    @Field("iata")
    private String iata; // IATA code to partition data (e.g., airline/station)

    @Field("suiteType")
    private String suiteType; // PAX1 / PAX2 / CC

    @Field("featureName")
    private String featureName; // Feature under test

    @Field("methodName")
    private String methodName; // TestNG method name

    @Field("className")
    private String className; // Fully qualified class name

    @Field("status")
    private String status; // PASS / FAIL / SKIP

    @Field("assertType")
    private String assertType; // HARD / SOFT

    @Field("startTime")
    private Instant startTime;

    @Field("endTime")
    private Instant endTime;

    @Field("durationMs")
    private long durationMs;

    @Field("jiraTestCases")
    private List<String> jiraTestCases; // e.g., ["TC-01", "TC-02"]

    @Field("environment")
    private EnvironmentInfo environment;

    @Field("logs")
    private List<String> logs; // General execution logs (for PASS case)

    @Field("screenshotUrls")
    private List<String> screenshotUrls; // Screenshot URLs (for PASS case)

    @Field("failures")
    private List<FailureDetail> failures; // Multiple failures for SOFT assert
}
