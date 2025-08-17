package aero.airfi.qa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentInfoDto {
    private String name;
    private String browser;
    private String browserVersion;
    private String os;
    private String buildNumber;
}


