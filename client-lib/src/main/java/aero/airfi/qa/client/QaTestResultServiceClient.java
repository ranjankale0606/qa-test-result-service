package aero.airfi.qa.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ClassicHttpResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import aero.airfi.qa.client.model.TestMethodResult;

public class QaTestResultServiceClient {
    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.custom().build();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private final String baseUrl;

    public QaTestResultServiceClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public String createTestResult(TestMethodResult testMethodResult) throws IOException {
        String url = baseUrl + "/api/test-results";
        HttpPost post = new HttpPost(url);
        String json = OBJECT_MAPPER.writeValueAsString(testMethodResult);
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        ClassicHttpResponse response = (ClassicHttpResponse) HTTP_CLIENT.executeOpen(null, post, null).getResult();
        int status = response.getCode();
        if (status >= 200 && status < 300) {
            return OBJECT_MAPPER.readTree(response.getEntity().getContent()).get("data").asText();
        }
        throw new IOException("Unexpected response status: " + status);
    }

    public List<TestMethodResult> getByRunId(String runId) throws IOException {
        String url = baseUrl + "/api/test-results/" + encode(runId);
        HttpGet get = new HttpGet(url);
        return readList(get, TestMethodResult.class);
    }

    public List<TestMethodResult> getByRunIdAndSuiteType(String runId, String suiteType) throws IOException {
        String url = baseUrl + "/api/test-results/suite/" + encode(suiteType) + "?runId=" + encode(runId);
        HttpGet get = new HttpGet(url);
        return readList(get, TestMethodResult.class);
    }

    public List<TestMethodResult> getByRunIdAndFeatureName(String runId, String featureName) throws IOException {
        String url = baseUrl + "/api/test-results/feature/" + encode(featureName) + "?runId=" + encode(runId);
        HttpGet get = new HttpGet(url);
        return readList(get, TestMethodResult.class);
    }

    private <T> List<T> readList(HttpUriRequestBase request, Class<T> elementType) throws IOException {
        ClassicHttpResponse response = (ClassicHttpResponse) HTTP_CLIENT.executeOpen(null, request, null).getResult();
        int status = response.getCode();
        if (status >= 200 && status < 300) {
            var node = OBJECT_MAPPER.readTree(response.getEntity().getContent());
            var dataNode = node.get("data");
            return OBJECT_MAPPER.readerFor(OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, elementType))
                    .readValue(dataNode);
        }
        throw new IOException("Unexpected response status: " + status);
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public static void shutdown() throws IOException {
        HTTP_CLIENT.close();
    }
}


