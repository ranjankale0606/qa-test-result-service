package aero.airfi.qa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailureDetailDto {
    private String errorMessage;
    private String stackTrace;
    private String screenshotUrl;
    private List<String> logs;
}


