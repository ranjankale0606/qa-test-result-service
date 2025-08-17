package aero.airfi.qa.controller;

import aero.airfi.qa.dto.TestMethodResultRequest;
import aero.airfi.qa.dto.TestMethodResultResponse;
import aero.airfi.qa.model.TestMethodResult;
import aero.airfi.qa.service.TestMethodResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import aero.airfi.qa.dto.ApiResponse;
import java.time.Instant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/{iata}/test-results")
@Tag(name = "Test  Result Service", description = "Endpoints to manage and query test method results")
@Slf4j
public class TestMethodResultController {

    @Autowired
    private TestMethodResultService testMethodResultService;
 

    @PostMapping
    @Operation(summary = "Create a test result", description = "Stores a new test method result and returns its id")
    public ResponseEntity<ApiResponse<String>> createTestResult(@PathVariable String iata, @RequestBody TestMethodResultRequest request) {
        log.info("POST createTestResult iata={} runId={} feature={} status={}", iata, request.getRunId(), request.getFeatureName(), request.getStatus());
        TestMethodResult toSave = ControllerMapper.toEntity(iata, request);
        TestMethodResult saved = testMethodResultService.createTestResult(toSave);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        ApiResponse<String> body = ApiResponse.<String>builder()
                .success(true)
                .data(saved.getId())
                .message("Created")
                .timestamp(Instant.now())
                .path(location.getPath())
                .build();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{runId}")
    @Operation(summary = "List results by run id", description = "Returns all test method results for the provided run id. Optionally filter by status using ?status=PASS|FAIL|SKIP")
    public ResponseEntity<ApiResponse<List<TestMethodResultResponse>>> getAllTestResults(
            @PathVariable String iata,
            @PathVariable String runId,
            @RequestParam(name = "status", required = false) String status) {
        String normalizedStatus = null;
        if (status != null && !status.isBlank()) {
            normalizedStatus = status.trim().toUpperCase();
            if (!("PASS".equals(normalizedStatus) || "FAIL".equals(normalizedStatus) || "SKIP".equals(normalizedStatus))) {
                throw new IllegalArgumentException("Invalid value for query parameter 'status': '" + status + "'. Allowed values are PASS, FAIL, SKIP. Use ?status=PASS|FAIL|SKIP");
            }
        }
        log.info("GET results by runId iata={} runId={}{}", iata, runId, normalizedStatus == null ? "" : ", status=" + normalizedStatus);
        List<TestMethodResult> testMethodResults = normalizedStatus == null
                ? testMethodResultService.getTestResultsByRunId(runId, iata)
                : testMethodResultService.getTestResultsByRunIdAndStatus(runId, normalizedStatus, iata);
        List<TestMethodResultResponse> dto = testMethodResults.stream().map(ControllerMapper::toResponse).toList();
        ApiResponse<List<TestMethodResultResponse>> body = ApiResponse.<List<TestMethodResultResponse>>builder()
                .success(true)
                .data(dto)
                .message("OK")
                .timestamp(Instant.now())
                .path(ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath())
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/suite/{suiteType}")
    @Operation(summary = "List results by run id and suite type", description = "Returns all results for a given run id filtered by suite type. Optionally filter by status using ?status=PASS|FAIL|SKIP")
    public ResponseEntity<ApiResponse<List<TestMethodResultResponse>>> getBySuiteType(
            @PathVariable String iata,
            @PathVariable String suiteType,
            @RequestParam String runId,
            @RequestParam(name = "status", required = false) String status) {
        String normalizedStatus = null;
        if (status != null && !status.isBlank()) {
            normalizedStatus = status.trim().toUpperCase();
            if (!("PASS".equals(normalizedStatus) || "FAIL".equals(normalizedStatus) || "SKIP".equals(normalizedStatus))) {
                throw new IllegalArgumentException("Invalid value for query parameter 'status': '" + status + "'. Allowed values are PASS, FAIL, SKIP. Use ?status=PASS|FAIL|SKIP");
            }
        }
        log.info("GET results by suiteType iata={} runId={} suiteType={}{}", iata, runId, suiteType, normalizedStatus == null ? "" : ", status=" + normalizedStatus);
        List<TestMethodResult> results = normalizedStatus == null
                ? testMethodResultService.getTestResultsByRunIdAndSuiteType(runId, suiteType, iata)
                : testMethodResultService.getTestResultsByRunIdAndSuiteTypeAndStatus(runId, suiteType, normalizedStatus, iata);
        List<TestMethodResultResponse> dto = results.stream().map(ControllerMapper::toResponse).toList();
        ApiResponse<List<TestMethodResultResponse>> body = ApiResponse.<List<TestMethodResultResponse>>builder()
                .success(true)
                .data(dto)
                .message("OK")
                .timestamp(Instant.now())
                .path(ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath())
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/feature/{featureName}")
    @Operation(summary = "List results by run id and feature name", description = "Returns all results for a given run id filtered by feature name. Optionally filter by status using ?status=PASS|FAIL|SKIP")
    public ResponseEntity<ApiResponse<List<TestMethodResultResponse>>> getByFeatureName(
            @PathVariable String iata,
            @PathVariable String featureName,
            @RequestParam String runId,
            @RequestParam(name = "status", required = false) String status) {
        String normalizedStatus = null;
        if (status != null && !status.isBlank()) {
            normalizedStatus = status.trim().toUpperCase();
            if (!("PASS".equals(normalizedStatus) || "FAIL".equals(normalizedStatus) || "SKIP".equals(normalizedStatus))) {
                throw new IllegalArgumentException("Invalid value for query parameter 'status': '" + status + "'. Allowed values are PASS, FAIL, SKIP. Use ?status=PASS|FAIL|SKIP");
            }
        }
        log.info("GET results by feature iata={} runId={} feature={}{}", iata, runId, featureName, normalizedStatus == null ? "" : ", status=" + normalizedStatus);
        List<TestMethodResult> results = normalizedStatus == null
                ? testMethodResultService.getTestResultsByRunIdAndFeatureName(runId, featureName, iata)
                : testMethodResultService.getTestResultsByRunIdAndFeatureNameAndStatus(runId, featureName, normalizedStatus, iata);
        List<TestMethodResultResponse> dto = results.stream().map(ControllerMapper::toResponse).toList();
        ApiResponse<List<TestMethodResultResponse>> body = ApiResponse.<List<TestMethodResultResponse>>builder()
                .success(true)
                .data(dto)
                .message("OK")
                .timestamp(Instant.now())
                .path(ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath())
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/run-ids")
    @Operation(summary = "List runIds (paginated)", description = "Returns distinct runIds for the given iata. Page size fixed at 10; supply ?page=0-based")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRunIds(
            @PathVariable String iata,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageIndex = Math.max(page, 0);
        List<String> runIds = testMethodResultService.getDistinctRunIdsByIataPaged(iata, pageIndex, 10);
        Map<String, Object> payload = Map.of(
                "page", pageIndex,
                "size", 10,
                "runIds", runIds
        );
        ApiResponse<Map<String, Object>> body = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .data(payload)
                .message("OK")
                .timestamp(Instant.now())
                .path(ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath())
                .build();
        return ResponseEntity.ok(body);
    }
    
}
