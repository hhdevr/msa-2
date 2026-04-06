package com.chaykin.orderservice.converter;

import com.chaykin.orderservice.controller.model.CreateOrderItemRequest;
import com.chaykin.orderservice.controller.model.CreateOrderRequest;
import com.chaykin.orderservice.controller.model.UpdateOrderRequest;
import com.chaykin.orderservice.persistence.model.Order;
import com.chaykin.orderservice.persistence.model.OrderItem;
import com.chaykin.orderservice.service.model.OrderDto;
import com.chaykin.orderservice.service.model.OrderItemDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;

class OrderConverterImplTest {

    private final OrderConverter converter = new OrderConverterImpl();

    @Nested
    @DisplayName("Order <-> OrderDto")
    class OrderMapping {

        @Test
        @DisplayName("should convert Order to OrderDto")
        void toDto() {
            // given
            Order order = create(Order.class);
            order.setItems(List.of());

            // when
            OrderDto dto = converter.convert(order);

            // then
            assertThat(dto.guid()).isEqualTo(order.getGuid());
            assertThat(dto.customerName()).isEqualTo(order.getCustomerName());
            assertThat(dto.customerEmail()).isEqualTo(order.getCustomerEmail());
            assertThat(dto.status()).isEqualTo(order.getStatus());
            assertThat(dto.totalAmount()).isEqualTo(order.getTotalAmount());
            assertThat(dto.currency()).isEqualTo(order.getCurrency());
        }

        @Test
        @DisplayName("should convert OrderDto to Order")
        void toEntity() {
            // given
            OrderDto dto = create(OrderDto.class);

            // when
            Order order = converter.convert(dto);

            // then
            assertThat(order.getGuid()).isEqualTo(dto.guid());
            assertThat(order.getCustomerName()).isEqualTo(dto.customerName());
            assertThat(order.getStatus()).isEqualTo(dto.status());
            assertThat(order.getTotalAmount()).isEqualTo(dto.totalAmount());
        }

        @Test
        @DisplayName("should return null for null Order")
        void nullOrder() {
            assertThat(converter.convert((Order) null)).isNull();
        }

        @Test
        @DisplayName("should return null for null OrderDto")
        void nullDto() {
            assertThat(converter.convert((OrderDto) null)).isNull();
        }

        @Test
        @DisplayName("should convert list of Orders")
        void list() {
            // given
            List<Order> orders = Instancio.ofList(Order.class).size(3).create();
            orders.forEach(o -> o.setItems(List.of()));

            // when
            List<OrderDto> dtos = converter.convert(orders);

            // then
            assertThat(dtos).hasSize(3);
        }
    }

    @Nested
    @DisplayName("OrderItem <-> OrderItemDto")
    class OrderItemMapping {

        @Test
        @DisplayName("should convert OrderItem to OrderItemDto")
        void toDto() {
            // given
            OrderItem item = create(OrderItem.class);

            // when
            OrderItemDto dto = converter.convert(item);

            // then
            assertThat(dto.guid()).isEqualTo(item.getGuid());
            assertThat(dto.productName()).isEqualTo(item.getProductName());
            assertThat(dto.quantity()).isEqualTo(item.getQuantity());
            assertThat(dto.price()).isEqualTo(item.getPrice());
        }
    }

    @Nested
    @DisplayName("Request -> OrderDto")
    class RequestMapping {

        @Test
        @DisplayName("should convert CreateOrderRequest with null guid and timestamps")
        void createRequest() {
            // given
            CreateOrderRequest request = create(CreateOrderRequest.class);

            // when
            OrderDto dto = converter.convert(request);

            // then
            assertThat(dto.guid()).isNull();
            assertThat(dto.customerName()).isEqualTo(request.customerName());
            assertThat(dto.status()).isEqualTo(request.status());
            assertThat(dto.createdAt()).isNull();
            assertThat(dto.updatedAt()).isNull();
        }

        @Test
        @DisplayName("should convert UpdateOrderRequest with guid, null timestamps")
        void updateRequest() {
            // given
            UpdateOrderRequest request = create(UpdateOrderRequest.class);

            // when
            OrderDto dto = converter.convert(request);

            // then
            assertThat(dto.guid()).isEqualTo(request.guid());
            assertThat(dto.customerName()).isEqualTo(request.customerName());
            assertThat(dto.createdAt()).isNull();
            assertThat(dto.updatedAt()).isNull();
        }

        @Test
        @DisplayName("should convert CreateOrderItemRequest with null guid")
        void createItemRequest() {
            // given
            CreateOrderItemRequest request = create(CreateOrderItemRequest.class);

            // when
            OrderItemDto dto = converter.convert(request);

            // then
            assertThat(dto.guid()).isNull();
            assertThat(dto.productName()).isEqualTo(request.productName());
            assertThat(dto.quantity()).isEqualTo(request.quantity());
            assertThat(dto.price()).isEqualTo(request.price());
        }
    }
}
