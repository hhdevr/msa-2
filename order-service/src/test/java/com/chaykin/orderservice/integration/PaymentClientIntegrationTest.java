package com.chaykin.orderservice.integration;

import com.chaykin.common.model.payment.CreatePaymentRequest;
import com.chaykin.common.model.payment.PaymentDto;
import com.chaykin.common.model.payment.PaymentMethod;
import com.chaykin.common.model.payment.PaymentStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import feign.FeignException;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static com.chaykin.common.model.payment.PaymentHeaders.KEY_HEADER;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@EnableWireMock(
        @ConfigureWireMock(
                name = "payment-service",
                filesUnderClasspath = "wiremock"
        )
)
class PaymentClientIntegrationTest {

    private static final String PAYMENTS_URL = "/api/v1/payments";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private PaymentClient paymentClient;

    @InjectWireMock("payment-service")
    private WireMockServer wireMockServer;

    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetRequests();
    }

    private CreatePaymentRequest buildRequest() {
        return new CreatePaymentRequest(
                UUID.randomUUID(),
                new BigDecimal("150.00"),
                "USD",
                PaymentMethod.CREDIT_CARD,
                PaymentStatus.PENDING,
                null,
                null
        );
    }

    @Nested
    @DisplayName("createPayment")
    class CreatePayment {

        @Test
        @DisplayName("should return PaymentDto when payment-service responds 201")
        void success() {
            // given
            CreatePaymentRequest request = buildRequest();

            // when
            PaymentDto result = paymentClient.createPayment(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.currency()).isEqualTo("USD");
            assertThat(result.status()).isEqualTo(PaymentStatus.PENDING);
            wireMockServer.verify(1, postRequestedFor(urlEqualTo(PAYMENTS_URL)));
        }

        @Test
        @DisplayName("should send idempotency key header and order-ref body")
        void sendsHeaderAndBody() throws JsonProcessingException {
            // given
            CreatePaymentRequest request = buildRequest();

            // when
            paymentClient.createPayment(request);

            // then
            wireMockServer.verify(postRequestedFor(urlEqualTo(PAYMENTS_URL))
                                          .withHeader(KEY_HEADER, matching("[0-9a-f-]{36}"))
                                          .withRequestBody(equalToJson(MAPPER.writeValueAsString(request))));
        }

        @Test
        @DisplayName("should retry 3 times on server error and throw FeignException")
        void retriesOnServerError() {
            // given
            wireMockServer.resetAll();
            wireMockServer.stubFor(post(urlEqualTo(PAYMENTS_URL))
                                           .willReturn(aResponse().withStatus(500)));
            CreatePaymentRequest request = buildRequest();

            // when & then
            assertThatThrownBy(() -> paymentClient.createPayment(request))
                    .isInstanceOf(FeignException.InternalServerError.class);
            wireMockServer.verify(3, postRequestedFor(urlEqualTo(PAYMENTS_URL)));
        }

        @Test
        @DisplayName("should succeed after one retry when first call fails with 500")
        void recoversAfterRetry() {
            // given
            wireMockServer.resetAll();
            wireMockServer.stubFor(post(urlEqualTo(PAYMENTS_URL))
                                           .inScenario("retry")
                                           .whenScenarioStateIs("Started")
                                           .willReturn(aResponse().withStatus(500))
                                           .willSetStateTo("second"));

            wireMockServer.stubFor(post(urlEqualTo(PAYMENTS_URL))
                                           .inScenario("retry")
                                           .whenScenarioStateIs("second")
                                           .willReturn(aResponse()
                                                               .withStatus(201)
                                                               .withHeader("Content-Type", "application/json")
                                                               .withBody("""
                                                                                 {
                                                                                   "guid":"11111111-1111-1111-1111-111111111111",
                                                                                   "orderRefId":"22222222-2222-2222-2222-222222222222",
                                                                                   "amount":150.00,
                                                                                   "currency":"USD",
                                                                                   "method":"CREDIT_CARD",
                                                                                   "status":"PENDING"
                                                                                 }
                                                                                 """)));
            CreatePaymentRequest request = buildRequest();

            // when
            PaymentDto result = paymentClient.createPayment(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo(PaymentStatus.PENDING);
            wireMockServer.verify(2, postRequestedFor(urlEqualTo(PAYMENTS_URL)));
        }

        @Test
        @DisplayName("should not retry on client error (404) and throw FeignException")
        void noRetryOnClientError() {
            // given
            wireMockServer.resetAll();
            wireMockServer.stubFor(post(urlEqualTo(PAYMENTS_URL))
                                           .willReturn(aResponse().withStatus(404)));
            CreatePaymentRequest request = buildRequest();

            // when & then
            assertThatThrownBy(() -> paymentClient.createPayment(request))
                    .isInstanceOf(FeignException.NotFound.class);
            wireMockServer.verify(1, postRequestedFor(urlEqualTo(PAYMENTS_URL)));
        }

        @Test
        @DisplayName("should not send GET requests to payment-service")
        void onlyPosts() {
            // given
            CreatePaymentRequest request = buildRequest();

            // when
            paymentClient.createPayment(request);

            // then
            wireMockServer.verify(0, getRequestedFor(urlEqualTo(PAYMENTS_URL)));
        }

        @Test
        @DisplayName("should fail with RetryableException when payment-service response is too slow")
        void timeout() {
            // given
            wireMockServer.resetAll();
            wireMockServer.stubFor(post(urlEqualTo(PAYMENTS_URL))
                                           .willReturn(aResponse()
                                                               .withStatus(201)
                                                               .withFixedDelay(2000)));
            CreatePaymentRequest request = buildRequest();

            // when & then
            assertThatThrownBy(() -> paymentClient.createPayment(request))
                    .isInstanceOf(RetryableException.class)
                    .hasMessageContaining("timed out");
        }

        @Test
        @DisplayName("should not retry on 409 Conflict (duplicate idempotency key)")
        void conflictOnDuplicate() {
            // given
            wireMockServer.resetAll();
            wireMockServer.stubFor(post(urlEqualTo(PAYMENTS_URL))
                                           .willReturn(aResponse().withStatus(409)));
            CreatePaymentRequest request = buildRequest();

            // when & then
            assertThatThrownBy(() -> paymentClient.createPayment(request))
                    .isInstanceOf(FeignException.Conflict.class);
            wireMockServer.verify(1, postRequestedFor(urlEqualTo(PAYMENTS_URL)));
        }

        @Test
        @DisplayName("should deserialize all PaymentDto fields including OffsetDateTime")
        void deserializesAllFields() {
            // given
            CreatePaymentRequest request = buildRequest();

            // when
            PaymentDto result = paymentClient.createPayment(request);

            // then
            assertThat(result.guid()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            assertThat(result.orderRefId()).isEqualTo(UUID.fromString("22222222-2222-2222-2222-222222222222"));
            assertThat(result.amount()).isEqualByComparingTo("150.00");
            assertThat(result.currency()).isEqualTo("USD");
            assertThat(result.method()).isEqualTo(PaymentMethod.CREDIT_CARD);
            assertThat(result.status()).isEqualTo(PaymentStatus.PENDING);
            assertThat(result.transactionId()).isEqualTo("TXN-001");
            assertThat(result.note()).isEqualTo("Initial payment");
            assertThat(result.createdAt()).isEqualTo(OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
            assertThat(result.updatedAt()).isEqualTo(OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
        }
    }
}
