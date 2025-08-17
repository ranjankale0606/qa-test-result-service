package aero.airfi.qa.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoRepositories(basePackages = "aero.airfi.qa.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(
                String.format("mongodb://%s:%d/%s", host, port, databaseName)
        );

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder -> 
                    builder.maxSize(100)
                           .minSize(5)
                           .maxWaitTime(5000, TimeUnit.MILLISECONDS)
                           .maxConnectionLifeTime(30, TimeUnit.MINUTES)
                           .maxConnectionIdleTime(10, TimeUnit.MINUTES)
                )
                .applyToSocketSettings(builder -> 
                    builder.connectTimeout(5000, TimeUnit.MILLISECONDS)
                           .readTimeout(30000, TimeUnit.MILLISECONDS)
                )
                .applyToServerSettings(builder -> 
                    builder.heartbeatFrequency(10000, TimeUnit.MILLISECONDS)
                           .minHeartbeatFrequency(500, TimeUnit.MILLISECONDS)
                )
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
