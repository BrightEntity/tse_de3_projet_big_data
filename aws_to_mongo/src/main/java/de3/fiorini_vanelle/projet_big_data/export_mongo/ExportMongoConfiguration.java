package de3.fiorini_vanelle.projet_big_data.export_mongo;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.internal.MongoClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class ExportMongoConfiguration {
    @Value("${spring.data.mongodb.host}")
    private String mongoHost;
    @Value("${spring.data.mongodb.port}")
    private Integer mongoPort;
    @Bean(name = "mongoHost")
    public String getMongoHost() {return this.mongoHost; }
    @Value("${cloud.aws.region.static}")
    private String awsRegion;
    @Value("${aws.accessKeyId}")
    private String awsAccessKey;
    @Bean(name = "awsAccessKey")
    public String getAwsAccessKey() {return this.awsAccessKey; }

    @Value("${aws.secretKey}")
    private String awsSecretKey;
    @Bean(name = "awsSecretKey")
    public String getAwsSecretKey() {return this.awsSecretKey; }

    @Value("${aws.sessionToken}")
    private String awsSessionToken;
    @Bean(name = "awsSessionToken")
    public String getAwsSessionToken() {return this.awsSessionToken; }

    @Bean(name = "awsRegion")
    public String getAwsRegion() { return this.awsRegion; }

    @Bean(name = "mongoPort")
    public Integer getMongoPort() { return this.mongoPort; }

    @Bean(name = "amazonS3")
    public S3Client getAmazonS3(String awsAccessKey, String awsSecretKey, String awsSessionToken, String awsRegion) {
        return S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(AwsSessionCredentials.create(awsAccessKey, awsSecretKey, awsSessionToken))).region(Region.US_EAST_1).build();
    }

    @Bean(name = "mongoClient")
    public MongoClient getMongoClient(String mongoHost, Integer mongoPort) {
        String connectionString = "mongodb://" + mongoHost + ":" + mongoPort + "/?retryWrites=false";
        return MongoClients.create(connectionString);
    }

    @Bean(name = "clientSession")
    public ClientSession getClientSession(MongoClient mongoClient) {
        return mongoClient.startSession();
    }
}
