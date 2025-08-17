package aero.airfi.qa.controller;

import aero.airfi.qa.dto.EnvironmentInfoDto;
import aero.airfi.qa.dto.FailureDetailDto;
import aero.airfi.qa.dto.TestMethodResultRequest;
import aero.airfi.qa.dto.TestMethodResultResponse;
import aero.airfi.qa.model.EnvironmentInfo;
import aero.airfi.qa.model.FailureDetail;
import aero.airfi.qa.model.TestMethodResult;

import java.util.List;
import java.util.stream.Collectors;

final class ControllerMapper {

    private ControllerMapper() {}

    static TestMethodResult toEntity(String iata, TestMethodResultRequest request) {
        return TestMethodResult.builder()
                .runId(request.getRunId())
                .iata(iata)
                .suiteType(request.getSuiteType())
                .featureName(request.getFeatureName())
                .methodName(request.getMethodName())
                .className(request.getClassName())
                .status(request.getStatus())
                .assertType(request.getAssertType())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .durationMs(request.getDurationMs())
                .jiraTestCases(request.getJiraTestCases())
                .environment(toEnvironment(request.getEnvironment()))
                .logs(request.getLogs())
                .screenshotUrls(request.getScreenshotUrls())
                .failures(toFailures(request.getFailures()))
                .build();
    }

    static TestMethodResultResponse toResponse(TestMethodResult entity) {
        return TestMethodResultResponse.builder()
                .id(entity.getId())
                .runId(entity.getRunId())
                .iata(entity.getIata())
                .suiteType(entity.getSuiteType())
                .featureName(entity.getFeatureName())
                .methodName(entity.getMethodName())
                .className(entity.getClassName())
                .status(entity.getStatus())
                .assertType(entity.getAssertType())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .durationMs(entity.getDurationMs())
                .jiraTestCases(entity.getJiraTestCases())
                .environment(toEnvironmentDto(entity.getEnvironment()))
                .logs(entity.getLogs())
                .screenshotUrls(entity.getScreenshotUrls())
                .failures(toFailureDtos(entity.getFailures()))
                .build();
    }

    private static EnvironmentInfo toEnvironment(EnvironmentInfoDto dto) {
        if (dto == null) return null;
        return EnvironmentInfo.builder()
                .name(dto.getName())
                .browser(dto.getBrowser())
                .browserVersion(dto.getBrowserVersion())
                .os(dto.getOs())
                .buildNumber(dto.getBuildNumber())
                .build();
    }

    private static EnvironmentInfoDto toEnvironmentDto(EnvironmentInfo env) {
        if (env == null) return null;
        return EnvironmentInfoDto.builder()
                .name(env.getName())
                .browser(env.getBrowser())
                .browserVersion(env.getBrowserVersion())
                .os(env.getOs())
                .buildNumber(env.getBuildNumber())
                .build();
    }

    private static List<FailureDetail> toFailures(List<FailureDetailDto> dtos) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(d -> FailureDetail.builder()
                        .errorMessage(d.getErrorMessage())
                        .stackTrace(d.getStackTrace())
                        .screenshotUrl(d.getScreenshotUrl())
                        .logs(d.getLogs())
                        .build())
                .collect(Collectors.toList());
    }

    private static List<FailureDetailDto> toFailureDtos(List<FailureDetail> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(e -> FailureDetailDto.builder()
                        .errorMessage(e.getErrorMessage())
                        .stackTrace(e.getStackTrace())
                        .screenshotUrl(e.getScreenshotUrl())
                        .logs(e.getLogs())
                        .build())
                .collect(Collectors.toList());
    }
}


