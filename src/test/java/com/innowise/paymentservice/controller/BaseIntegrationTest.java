package com.innowise.paymentservice.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.Properties;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    );

    static WireMockServer wireMockServer = new WireMockServer(8080);

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        registry.add("random.service.url", () -> "http://localhost:8089");
        registry.add("order.service.url", () -> "http://localhost:8089");
    }

    @BeforeAll
    static void createKafkaTopics() throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafka.getBootstrapServers());

        try (AdminClient adminClient = AdminClient.create(props)) {
            NewTopic topic = new NewTopic("CREATE_PAYMENT", 1, (short) 1);
            adminClient.createTopics(Collections.singleton(topic)).all().get();
        }
    }

    @BeforeAll
    static void startWiremock() {
        wireMockServer.start();
    }

    @AfterAll
    static void stopWiremock() {
        wireMockServer.stop();
    }
}

