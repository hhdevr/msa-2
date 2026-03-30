package com.chaykin.paymentservice.converter;

import com.chaykin.paymentservice.controller.model.CreatePaymentRequest;
import com.chaykin.paymentservice.controller.model.UpdatePaymentRequest;
import com.chaykin.paymentservice.persistence.model.Payment;
import com.chaykin.paymentservice.service.model.PaymentDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;

class PaymentConverterImplTest {

    private final PaymentConverter converter = new PaymentConverterImpl();

    @Nested
    @DisplayName("Payment <-> PaymentDto")
    class PaymentMapping {

        @Test
        @DisplayName("should convert Payment to PaymentDto")
        void toDto() {
            // given
            Payment payment = create(Payment.class);

            // when
            PaymentDto dto = converter.convert(payment);

            // then
            assertThat(dto.guid()).isEqualTo(payment.getGuid());
            assertThat(dto.orderRefId()).isEqualTo(payment.getOrderRefId());
            assertThat(dto.amount()).isEqualTo(payment.getAmount());
            assertThat(dto.currency()).isEqualTo(payment.getCurrency());
            assertThat(dto.method()).isEqualTo(payment.getMethod());
            assertThat(dto.status()).isEqualTo(payment.getStatus());
        }

        @Test
        @DisplayName("should convert PaymentDto to Payment")
        void toEntity() {
            // given
            PaymentDto dto = create(PaymentDto.class);

            // when
            Payment payment = converter.convert(dto);

            // then
            assertThat(payment.getGuid()).isEqualTo(dto.guid());
            assertThat(payment.getOrderRefId()).isEqualTo(dto.orderRefId());
            assertThat(payment.getAmount()).isEqualTo(dto.amount());
            assertThat(payment.getMethod()).isEqualTo(dto.method());
        }

        @Test
        @DisplayName("should return null for null Payment")
        void nullPayment() {
            assertThat(converter.convert((Payment) null)).isNull();
        }

        @Test
        @DisplayName("should return null for null PaymentDto")
        void nullDto() {
            assertThat(converter.convert((PaymentDto) null)).isNull();
        }

        @Test
        @DisplayName("should convert list of Payments")
        void list() {
            // given
            List<Payment> payments = Instancio.ofList(Payment.class).size(3).create();

            // when
            List<PaymentDto> dtos = converter.convert(payments);

            // then
            assertThat(dtos).hasSize(3);
        }
    }

    @Nested
    @DisplayName("Request -> PaymentDto")
    class RequestMapping {

        @Test
        @DisplayName("should convert CreatePaymentRequest with null guid and timestamps")
        void createRequest() {
            // given
            CreatePaymentRequest request = create(CreatePaymentRequest.class);

            // when
            PaymentDto dto = converter.convert(request);

            // then
            assertThat(dto.guid()).isNull();
            assertThat(dto.orderRefId()).isEqualTo(request.orderRefId());
            assertThat(dto.amount()).isEqualTo(request.amount());
            assertThat(dto.createdAt()).isNull();
            assertThat(dto.updatedAt()).isNull();
        }

        @Test
        @DisplayName("should convert UpdatePaymentRequest with guid, null timestamps")
        void updateRequest() {
            // given
            UpdatePaymentRequest request = create(UpdatePaymentRequest.class);

            // when
            PaymentDto dto = converter.convert(request);

            // then
            assertThat(dto.guid()).isEqualTo(request.guid());
            assertThat(dto.orderRefId()).isEqualTo(request.orderRefId());
            assertThat(dto.createdAt()).isNull();
            assertThat(dto.updatedAt()).isNull();
        }
    }
}
