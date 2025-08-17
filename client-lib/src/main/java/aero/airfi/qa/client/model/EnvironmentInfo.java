package aero.airfi.qa.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentInfo {
    private String name;
    private String browser;
    private String browserVersion;
    private String os;
    private String buildNumber;
}


