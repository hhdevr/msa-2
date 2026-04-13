package com.chaykin.deliveryservice.converter;

import com.chaykin.common.model.delivery.AddressDto;
import com.chaykin.common.model.delivery.CreateDeliveryRequest;
import com.chaykin.common.model.delivery.DeliveryDto;
import com.chaykin.common.model.delivery.UpdateDeliveryRequest;
import com.chaykin.deliveryservice.persistence.model.Address;
import com.chaykin.deliveryservice.persistence.model.Delivery;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;

class DeliveryConverterImplTest {

    private final DeliveryConverter converter = new DeliveryConverterImpl();

    @Nested
    @DisplayName("Delivery <-> DeliveryDto")
    class DeliveryMapping {

        @Test
        @DisplayName("should convert Delivery to DeliveryDto")
        void toDto() {
            // given
            Delivery delivery = create(Delivery.class);

            // when
            DeliveryDto dto = converter.convert(delivery);

            // then
            assertThat(dto.guid()).isEqualTo(delivery.getGuid());
            assertThat(dto.orderRefId()).isEqualTo(delivery.getOrderRefId());
            assertThat(dto.recipientName()).isEqualTo(delivery.getRecipientName());
            assertThat(dto.status()).isEqualTo(delivery.getStatus());
        }

        @Test
        @DisplayName("should convert DeliveryDto to Delivery")
        void toEntity() {
            // given
            DeliveryDto dto = create(DeliveryDto.class);

            // when
            Delivery delivery = converter.convert(dto);

            // then
            assertThat(delivery.getGuid()).isEqualTo(dto.guid());
            assertThat(delivery.getOrderRefId()).isEqualTo(dto.orderRefId());
            assertThat(delivery.getRecipientName()).isEqualTo(dto.recipientName());
        }

        @Test
        @DisplayName("should return null for null Delivery")
        void nullDelivery() {
            assertThat(converter.convert((Delivery) null)).isNull();
        }

        @Test
        @DisplayName("should return null for null DeliveryDto")
        void nullDto() {
            assertThat(converter.convert((DeliveryDto) null)).isNull();
        }

        @Test
        @DisplayName("should convert list of Deliveries")
        void list() {
            // given
            List<Delivery> deliveries = Instancio.ofList(Delivery.class).size(3).create();

            // when
            List<DeliveryDto> dtos = converter.convert(deliveries);

            // then
            assertThat(dtos).hasSize(3);
        }
    }

    @Nested
    @DisplayName("Address <-> AddressDto")
    class AddressMapping {

        @Test
        @DisplayName("should convert Address to AddressDto")
        void toDto() {
            // given
            Address address = create(Address.class);

            // when
            AddressDto dto = converter.convert(address);

            // then
            assertThat(dto.country()).isEqualTo(address.getCountry());
            assertThat(dto.city()).isEqualTo(address.getCity());
            assertThat(dto.street()).isEqualTo(address.getStreet());
        }
    }

    @Nested
    @DisplayName("Request -> DeliveryDto")
    class RequestMapping {

        @Test
        @DisplayName("should convert CreateDeliveryRequest with null guid and timestamps")
        void createRequest() {
            // given
            CreateDeliveryRequest request = create(CreateDeliveryRequest.class);

            // when
            DeliveryDto dto = converter.convert(request);

            // then
            assertThat(dto.guid()).isNull();
            assertThat(dto.orderRefId()).isEqualTo(request.orderRefId());
            assertThat(dto.recipientName()).isEqualTo(request.recipientName());
            assertThat(dto.createdAt()).isNull();
            assertThat(dto.updatedAt()).isNull();
        }

        @Test
        @DisplayName("should convert UpdateDeliveryRequest with guid, null timestamps")
        void updateRequest() {
            // given
            UpdateDeliveryRequest request = create(UpdateDeliveryRequest.class);

            // when
            DeliveryDto dto = converter.convert(request);

            // then
            assertThat(dto.guid()).isEqualTo(request.guid());
            assertThat(dto.orderRefId()).isEqualTo(request.orderRefId());
            assertThat(dto.createdAt()).isNull();
            assertThat(dto.updatedAt()).isNull();
        }
    }
}
