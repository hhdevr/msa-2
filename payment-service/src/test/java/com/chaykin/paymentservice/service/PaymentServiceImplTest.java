package com.chaykin.paymentservice.service;

import com.chaykin.common.exception.ServiceException;
import com.chaykin.paymentservice.converter.PaymentConverter;
import com.chaykin.paymentservice.converter.PaymentConverterImpl;
import com.chaykin.paymentservice.persistence.model.Payment;
import com.chaykin.paymentservice.persistence.repository.PaymentRepository;
import com.chaykin.paymentservice.service.model.PaymentDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Instancio.create;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        PaymentConverter paymentConverter = new PaymentConverterImpl();
        paymentService = new PaymentServiceImpl(paymentRepository, paymentConverter);
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return list of PaymentDto")
        void returnsList() {
            // given
            List<Payment> payments = Instancio.ofList(Payment.class).size(3).create();
            payments.forEach(p -> p.setActive(true));
            when(paymentRepository.findAllByActiveTrue()).thenReturn(payments);

            // when
            List<PaymentDto> result = paymentService.findAll();

            // then
            assertThat(result).hasSize(3);
            verify(paymentRepository).findAllByActiveTrue();
        }

        @Test
        @DisplayName("should return empty list when repository is empty")
        void returnsEmptyList() {
            // given
            when(paymentRepository.findAllByActiveTrue()).thenReturn(emptyList());

            // when
            List<PaymentDto> result = paymentService.findAll();

            // then
            assertThat(result).isEmpty();
            verify(paymentRepository).findAllByActiveTrue();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return PaymentDto when found")
        void returnsPaymentDto() {
            // given
            Payment payment = create(Payment.class);
            payment.setActive(true);
            UUID guid = payment.getGuid();
            when(paymentRepository.findByGuid(guid)).thenReturn(Optional.of(payment));

            // when
            Optional<PaymentDto> result = paymentService.findById(guid);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().guid()).isEqualTo(guid);
            verify(paymentRepository).findByGuid(guid);
        }

        @Test
        @DisplayName("should return empty when not found")
        void returnsEmpty() {
            // given
            UUID guid = UUID.randomUUID();
            when(paymentRepository.findByGuid(guid)).thenReturn(Optional.empty());

            // when
            Optional<PaymentDto> result = paymentService.findById(guid);

            // then
            assertThat(result).isEmpty();
            verify(paymentRepository).findByGuid(guid);
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("should throw ServiceException when not found")
        void throwsException() {
            // given
            UUID guid = UUID.randomUUID();
            when(paymentRepository.findByGuid(guid)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.getById(guid))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Payment id=" + guid + " does not exist");
            verify(paymentRepository).findByGuid(guid);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should save and return new PaymentDto")
        void savesAndReturns() {
            // given
            PaymentDto dto = create(PaymentDto.class);
            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            PaymentDto result = paymentService.create(dto);

            // then
            assertThat(result).isNotNull();
            verify(paymentRepository).save(any(Payment.class));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update existing payment")
        void updatesExisting() {
            // given
            PaymentDto dto = create(PaymentDto.class);
            Payment existing = create(Payment.class);
            existing.setGuid(dto.guid());
            existing.setActive(true);
            when(paymentRepository.findByGuid(dto.guid())).thenReturn(Optional.of(existing));
            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            PaymentDto result = paymentService.update(dto);

            // then
            assertThat(result).isNotNull();
            verify(paymentRepository).findByGuid(dto.guid());
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("should throw ServiceException when not exists")
        void throwsWhenNotExists() {
            // given
            PaymentDto dto = create(PaymentDto.class);
            when(paymentRepository.findByGuid(dto.guid())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.update(dto))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Payment id=" + dto.guid() + " does not exist");
            verify(paymentRepository).findByGuid(dto.guid());
            verify(paymentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should soft-delete existing payment")
        void softDeletes() {
            // given
            UUID guid = UUID.randomUUID();
            Payment payment = create(Payment.class);
            payment.setGuid(guid);
            payment.setActive(true);
            when(paymentRepository.findByGuid(guid)).thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            paymentService.delete(guid);

            // then
            assertThat(payment.isActive()).isFalse();
            verify(paymentRepository).findByGuid(guid);
            verify(paymentRepository).save(payment);
        }

        @Test
        @DisplayName("should throw ServiceException when not exists")
        void throwsWhenNotExists() {
            // given
            UUID guid = UUID.randomUUID();
            when(paymentRepository.findByGuid(guid)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.delete(guid))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Payment id=" + guid + " does not exist");
            verify(paymentRepository).findByGuid(guid);
            verify(paymentRepository, never()).save(any());
        }
    }
}
