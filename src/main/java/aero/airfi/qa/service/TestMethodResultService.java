package aero.airfi.qa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.bson.Document;
import lombok.extern.slf4j.Slf4j;

import aero.airfi.qa.model.TestMethodResult;
import aero.airfi.qa.repository.TestMethodResultRepository;
import aero.airfi.qa.exception.ResourceNotFoundException;

@Service
@Slf4j
public class TestMethodResultService {

    private final TestMethodResultRepository testMethodResultRepository;
    private final MongoTemplate mongoTemplate;

    public TestMethodResultService(TestMethodResultRepository testMethodResultRepository, MongoTemplate mongoTemplate) {
        this.testMethodResultRepository = testMethodResultRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public TestMethodResult createTestResult(TestMethodResult testMethodResult) {
        log.info("Saving test result runId={} iata={} feature={} status={}", testMethodResult.getRunId(),
        testMethodResult.getIata(), 
        testMethodResult.getFeatureName(),
         testMethodResult.getStatus());
        
         return testMethodResultRepository.save(testMethodResult);
    }

    public List<TestMethodResult> getAllTestResults() {
        return testMethodResultRepository.findAll();
    }

    public Optional<TestMethodResult> getTestResultById(String id) {
        return testMethodResultRepository.findById(id);
    }

    public List<TestMethodResult> getTestResultsByRunId(String runId, String iata) {
        log.debug("Fetching results by runId and iata runId={} iata={}", runId, iata);
        List<TestMethodResult> results = testMethodResultRepository.findByRunIdAndIata(runId, iata);
        if (results == null || results.isEmpty()) {
            throw new ResourceNotFoundException("Run id not found: " + runId + ", iata=" + iata);
        }
        return results;
    }

    public List<TestMethodResult> getTestResultsByRunIdAndSuiteType(String runId, String suiteType, String iata) {
        log.debug("Fetching results by runId, suiteType and iata runId={} suiteType={} iata={}", runId, suiteType, iata);
        List<TestMethodResult> results = testMethodResultRepository.findByRunIdAndSuiteTypeAndIata(runId, suiteType, iata);
        if (results == null || results.isEmpty()) {
            throw new ResourceNotFoundException("No results for runId=" + runId + ", suiteType=" + suiteType + ", iata=" + iata);
        }
        return results;
    }

    public List<TestMethodResult> getTestResultsByRunIdAndSuiteTypeAndStatus(String runId, String suiteType, String status, String iata) {
        log.debug("Fetching results by runId, suiteType, status and iata runId={} suiteType={} status={} iata={}", runId, suiteType, status, iata);
        List<TestMethodResult> results = testMethodResultRepository.findByRunIdAndSuiteTypeAndStatusAndIata(runId, suiteType, status, iata);
        return results;
    }

    public List<TestMethodResult> getTestResultsByRunIdAndFeatureName(String runId, String featureName, String iata) {
        log.debug("Fetching results by runId, feature and iata runId={} feature={} iata={}", runId, featureName, iata);
        List<TestMethodResult> results = testMethodResultRepository.findByRunIdAndFeatureNameAndIata(runId, featureName, iata);
        if (results == null || results.isEmpty()) {
            throw new ResourceNotFoundException("No results for runId=" + runId + ", featureName=" + featureName + ", iata=" + iata);
        }
        return results;
    }

    public List<TestMethodResult> getTestResultsByRunIdAndFeatureNameAndStatus(String runId, String featureName, String status, String iata) {
        log.debug("Fetching results by runId, feature, status and iata runId={} feature={} status={} iata={}", runId, featureName, status, iata);
        List<TestMethodResult> results = testMethodResultRepository.findByRunIdAndFeatureNameAndStatusAndIata(runId, featureName, status, iata);
        return results;
    }

    public List<TestMethodResult> getTestResultsByRunIdAndStatus(String runId, String status, String iata) {
        List<TestMethodResult> results = testMethodResultRepository.findByRunIdAndStatusAndIata(runId, status, iata);
       return results;
    }

    public TestMethodResult updateTestResult(String id, TestMethodResult testMethodResult) {
        if (testMethodResultRepository.existsById(id)) {
            log.info("Updating test result id={} status={} feature={}", id, testMethodResult.getStatus(), testMethodResult.getFeatureName());
            testMethodResult.setId(id);
            return testMethodResultRepository.save(testMethodResult);
        }
        throw new ResourceNotFoundException("Test result not found with id: " + id);
    }

    public void deleteTestResult(String id) {
        if (!testMethodResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Test result not found with id: " + id);
        }
        log.info("Deleting test result id={}", id);
        testMethodResultRepository.deleteById(id);
    }

    public List<String> getDistinctRunIdsByIataPaged(String iata, int page, int size) {
        int pageIndex = Math.max(page, 0);
        int pageSize = Math.max(size, 1);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("iata").is(iata)),
                Aggregation.group("runId"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "_id")),
                Aggregation.skip((long) pageIndex * pageSize),
                Aggregation.limit(pageSize),
                Aggregation.project().and("_id").as("runId")
        );
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "testMethodResults", Document.class);
        return results.getMappedResults().stream()
                .map(doc -> doc.getString("runId"))
                .toList();
    }
}
