package com.chaykin.deliveryservice.service;

import com.chaykin.common.exception.ServiceException;
import com.chaykin.deliveryservice.converter.DeliveryConverter;
import com.chaykin.deliveryservice.converter.DeliveryConverterImpl;
import com.chaykin.deliveryservice.persistence.model.Delivery;
import com.chaykin.deliveryservice.persistence.repository.DeliveryRepository;
import com.chaykin.deliveryservice.service.model.DeliveryDto;
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
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        DeliveryConverter deliveryConverter = new DeliveryConverterImpl();
        deliveryService = new DeliveryServiceImpl(deliveryRepository, deliveryConverter);
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return list of DeliveryDto")
        void returnsList() {
            // given
            List<Delivery> deliveries = Instancio.ofList(Delivery.class).size(3).create();
            deliveries.forEach(d -> d.setActive(true));
            when(deliveryRepository.findAllByActiveTrue()).thenReturn(deliveries);

            // when
            List<DeliveryDto> result = deliveryService.findAll();

            // then
            assertThat(result).hasSize(3);
            verify(deliveryRepository).findAllByActiveTrue();
        }

        @Test
        @DisplayName("should return empty list when repository is empty")
        void returnsEmptyList() {
            // given
            when(deliveryRepository.findAllByActiveTrue()).thenReturn(emptyList());

            // when
            List<DeliveryDto> result = deliveryService.findAll();

            // then
            assertThat(result).isEmpty();
            verify(deliveryRepository).findAllByActiveTrue();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return DeliveryDto when found")
        void returnsDeliveryDto() {
            // given
            Delivery delivery = create(Delivery.class);
            delivery.setActive(true);
            UUID guid = delivery.getGuid();
            when(deliveryRepository.findByGuid(guid)).thenReturn(Optional.of(delivery));

            // when
            Optional<DeliveryDto> result = deliveryService.findById(guid);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().guid()).isEqualTo(guid);
            verify(deliveryRepository).findByGuid(guid);
        }

        @Test
        @DisplayName("should return empty when not found")
        void returnsEmpty() {
            // given
            UUID guid = UUID.randomUUID();
            when(deliveryRepository.findByGuid(guid)).thenReturn(Optional.empty());

            // when
            Optional<DeliveryDto> result = deliveryService.findById(guid);

            // then
            assertThat(result).isEmpty();
            verify(deliveryRepository).findByGuid(guid);
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
            when(deliveryRepository.findByGuid(guid)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> deliveryService.getById(guid))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Delivery id=" + guid + " does not exist");
            verify(deliveryRepository).findByGuid(guid);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should save and return new DeliveryDto")
        void savesAndReturns() {
            // given
            DeliveryDto dto = create(DeliveryDto.class);
            when(deliveryRepository.save(any(Delivery.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            DeliveryDto result = deliveryService.create(dto);

            // then
            assertThat(result).isNotNull();
            verify(deliveryRepository).save(any(Delivery.class));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update existing delivery")
        void updatesExisting() {
            // given
            DeliveryDto dto = create(DeliveryDto.class);
            Delivery existing = create(Delivery.class);
            existing.setGuid(dto.guid());
            existing.setActive(true);
            when(deliveryRepository.findByGuid(dto.guid())).thenReturn(Optional.of(existing));
            when(deliveryRepository.save(any(Delivery.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            DeliveryDto result = deliveryService.update(dto);

            // then
            assertThat(result).isNotNull();
            verify(deliveryRepository).findByGuid(dto.guid());
            verify(deliveryRepository).save(any(Delivery.class));
        }

        @Test
        @DisplayName("should throw ServiceException when not exists")
        void throwsWhenNotExists() {
            // given
            DeliveryDto dto = create(DeliveryDto.class);
            when(deliveryRepository.findByGuid(dto.guid())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> deliveryService.update(dto))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Delivery id=" + dto.guid() + " does not exist");
            verify(deliveryRepository).findByGuid(dto.guid());
            verify(deliveryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should soft-delete existing delivery")
        void softDeletes() {
            // given
            UUID guid = UUID.randomUUID();
            Delivery delivery = create(Delivery.class);
            delivery.setGuid(guid);
            delivery.setActive(true);
            when(deliveryRepository.findByGuid(guid)).thenReturn(Optional.of(delivery));
            when(deliveryRepository.save(any(Delivery.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            deliveryService.delete(guid);

            // then
            assertThat(delivery.isActive()).isFalse();
            verify(deliveryRepository).findByGuid(guid);
            verify(deliveryRepository).save(delivery);
        }

        @Test
        @DisplayName("should throw ServiceException when not exists")
        void throwsWhenNotExists() {
            // given
            UUID guid = UUID.randomUUID();
            when(deliveryRepository.findByGuid(guid)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> deliveryService.delete(guid))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Delivery id=" + guid + " does not exist");
            verify(deliveryRepository).findByGuid(guid);
            verify(deliveryRepository, never()).save(any());
        }
    }
}
