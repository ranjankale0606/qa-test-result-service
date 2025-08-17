package aero.airfi.qa.repository;

import aero.airfi.qa.model.TestMethodResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestMethodResultRepository extends MongoRepository<TestMethodResult, String> {
    List<TestMethodResult> findByRunIdAndIata(String runId, String iata);
    List<TestMethodResult> findByRunIdAndSuiteTypeAndIata(String runId, String suiteType, String iata);
    List<TestMethodResult> findByRunIdAndFeatureNameAndIata(String runId, String featureName, String iata);
    List<TestMethodResult> findByRunIdAndStatusAndIata(String runId, String status, String iata);
    List<TestMethodResult> findByRunIdAndSuiteTypeAndStatusAndIata(String runId, String suiteType, String status, String iata);
    List<TestMethodResult> findByRunIdAndFeatureNameAndStatusAndIata(String runId, String featureName, String status, String iata);
}
