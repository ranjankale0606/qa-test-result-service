package aero.airfi.qa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentInfo {
    private String name; // QA, Staging, Prod
    private String browser;
    private String browserVersion;
    private String os;
    private String buildNumber;
}