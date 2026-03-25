package com.innowise.paymentservice.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.innowise.paymentservice.client.OrderClient;
import com.innowise.paymentservice.client.RandomClient;
import com.innowise.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.model.dto.PaymentDto;
import com.innowise.paymentservice.model.entity.Payment;
import com.innowise.paymentservice.repository.PaymentRepository;
import com.innowise.paymentservice.service.impl.AuthenticationServiceImpl;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PaymentIntegrationTest {

    @MockitoBean
    private RandomClient randomClient;
    @MockitoBean
    private OrderClient orderClient;
    @Autowired
    private PaymentRepository paymentRepository;
    @MockitoBean
    private PaymentMapper paymentMapper;
    @MockitoBean
    private AuthenticationServiceImpl authenticationServiceImpl;
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    );

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        registry.add("random.service.url", () -> "http://localhost:8089");
        registry.add("order.service.url", () -> "http://localhost:8089");
        registry.add("mongodb.url", mongoDBContainer::getReplicaSetUrl);

    }

    @BeforeEach
    void setup() {
        Mockito.when(authenticationServiceImpl.adminRole(Mockito.any(Authentication.class))).thenReturn(true);
    }

    @BeforeAll
    static void setupKafkaTopic() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (AdminClient adminClient = AdminClient.create(configs)) {
            NewTopic topic = new NewTopic("CREATE_PAYMENT", 1, (short) 1);
            adminClient.createTopics(Collections.singleton(topic)).all().get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        Payment payment = new Payment();
        payment.setId("1");
        payment.setUserId(1L);
        payment.setOrderId(100L);
        payment.setStatus(Payment.Status.FAILED);
        payment.setPaymentAmount(new BigDecimal("50"));

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setUserId(payment.getUserId());
        paymentDto.setOrderId(payment.getOrderId());
        paymentDto.setStatus(payment.getStatus());
        paymentDto.setPaymentAmount(payment.getPaymentAmount());


        when(paymentMapper.toPaymentDtoList(any())).thenReturn(List.of(paymentDto));
    }

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setupStubs() {
        wireMockServer.stubFor(get(urlEqualTo("/orders/users/1/orders"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {
                                        "id": 100,
                                        "totalPrice": 50.5,
                                        "deleted": false,
                                        "user": {"id": 1}
                                    }
                                ]
                                """)
                        .withStatus(200)));
    }

    @Test
    void testCreatePayment() throws Exception {
        mockMvc.perform(post("/payments/").with(user("1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].status").exists())
                .andExpect(jsonPath("$[0].amount").doesNotExist());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testFindByUserId() throws Exception {
        Payment payment = new Payment();
        payment.setUserId(1L);
        payment.setOrderId(100L);
        payment.setStatus(Payment.Status.FAILED);
        payment.setPaymentAmount(new BigDecimal("50"));

        PaymentDto dto = new PaymentDto();
        dto.setUserId(1L);
        dto.setOrderId(100L);
        dto.setStatus(Payment.Status.FAILED);
        dto.setPaymentAmount(new BigDecimal("50"));

        Mockito.when(authenticationServiceImpl.isSelf(Mockito.eq(1L), Mockito.any(Authentication.class)))
                .thenReturn(true);
        Mockito.when(paymentMapper.toPaymentDtoList(List.of(payment))).thenReturn(List.of(dto));
        Mockito.when(authenticationServiceImpl.adminRole(Mockito.any())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/payments/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].orderId").value(100))
                .andExpect(jsonPath("$[0].status").value("FAILED"))
                .andExpect(jsonPath("$[0].paymentAmount").value(50));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFindByOrderId() throws Exception {

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/payments/orders/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].orderId").value(100))
                .andExpect(jsonPath("$[0].status").value("FAILED"))
                .andExpect(jsonPath("$[0].paymentAmount").value(50.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFindByStatus() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/payments/status/?status=FAILED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].orderId").value(100))
                .andExpect(jsonPath("$[0].status").value("FAILED"))
                .andExpect(jsonPath("$[0].paymentAmount").value(50));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetTotalSumForUserById() throws Exception {
        Payment payment = new Payment();
        payment.setUserId(1L);
        payment.setOrderId(100L);
        payment.setStatus(Payment.Status.FAILED);
        payment.setPaymentAmount(new BigDecimal("150.0"));
        payment.setTimestamp(LocalDateTime.of(2026, 3, 15, 12, 0));
        paymentRepository.save(payment);
        LocalDateTime from = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 3, 31, 23, 59);

        Mockito.when(authenticationServiceImpl.isSelf(Mockito.eq(payment.getUserId()), Mockito.any(Authentication.class)))
                .thenReturn(true);
        Mockito.when(authenticationServiceImpl.adminRole(Mockito.any(Authentication.class)))
                .thenReturn(false);


        mockMvc.perform(MockMvcRequestBuilders.get("/payments/{userId}/", payment.getUserId())
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(payment.getUserId()))
                .andExpect(jsonPath("$.total").value(150.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetTotalSumForAllUsers() throws Exception {
        LocalDateTime from = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 3, 31, 23, 59);

        Mockito.when(authenticationServiceImpl.adminRole(Mockito.any(Authentication.class)))
                .thenReturn(true);


        mockMvc.perform(MockMvcRequestBuilders.get("/payments/")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(150.0));
    }
}
