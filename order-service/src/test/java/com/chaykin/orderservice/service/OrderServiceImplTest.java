package com.chaykin.orderservice.service;

import com.chaykin.common.exception.ServiceException;
import com.chaykin.orderservice.converter.OrderConverter;
import com.chaykin.orderservice.converter.OrderConverterImpl;
import com.chaykin.orderservice.persistence.model.Order;
import com.chaykin.orderservice.persistence.repository.OrderRepository;
import com.chaykin.orderservice.service.model.OrderDto;
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
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        OrderConverter orderConverter = new OrderConverterImpl();
        orderService = new OrderServiceImpl(orderRepository, orderConverter);
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return list of OrderDto")
        void returnsListOfOrderDto() {
            // given
            List<Order> orders = Instancio.ofList(Order.class).size(3).create();
            orders.forEach(order -> {
                order.setItems(List.of());
                order.setActive(true);
            });
            when(orderRepository.findAllByActiveTrue()).thenReturn(orders);

            // when
            List<OrderDto> result = orderService.findAll();

            // then
            assertThat(result).hasSize(3);
            verify(orderRepository).findAllByActiveTrue();
        }

        @Test
        @DisplayName("should return empty list when repository is empty")
        void returnsEmptyList() {
            // given
            when(orderRepository.findAllByActiveTrue()).thenReturn(emptyList());

            // when
            List<OrderDto> result = orderService.findAll();

            // then
            assertThat(result).isEmpty();
            verify(orderRepository).findAllByActiveTrue();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return OrderDto when found")
        void returnsOrderDto() {
            // given
            Order order = create(Order.class);
            order.setItems(List.of());
            order.setActive(true);
            UUID guid = order.getGuid();
            when(orderRepository.findByGuid(guid)).thenReturn(Optional.of(order));

            // when
            Optional<OrderDto> result = orderService.findById(guid);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().guid()).isEqualTo(guid);
            assertThat(result.get().customerName()).isEqualTo(order.getCustomerName());
            verify(orderRepository).findByGuid(guid);
        }

        @Test
        @DisplayName("should return empty when not found")
        void returnsEmpty() {
            // given
            UUID guid = UUID.randomUUID();
            when(orderRepository.findByGuid(guid)).thenReturn(Optional.empty());

            // when
            Optional<OrderDto> result = orderService.findById(guid);

            // then
            assertThat(result).isEmpty();
            verify(orderRepository).findByGuid(guid);
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
            when(orderRepository.findByGuid(guid)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.getById(guid))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Order id=" + guid + " does not exist");
            verify(orderRepository).findByGuid(guid);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should save and return new OrderDto")
        void savesAndReturns() {
            // given
            OrderDto dto = create(OrderDto.class);
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            OrderDto result = orderService.create(dto);

            // then
            assertThat(result).isNotNull();
            verify(orderRepository).save(any(Order.class));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update existing order")
        void updatesExisting() {
            // given
            OrderDto dto = create(OrderDto.class);
            Order existing = create(Order.class);
            existing.setGuid(dto.guid());
            existing.setActive(true);
            when(orderRepository.findByGuid(dto.guid())).thenReturn(Optional.of(existing));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            OrderDto result = orderService.update(dto);

            // then
            assertThat(result).isNotNull();
            verify(orderRepository).findByGuid(dto.guid());
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("should throw ServiceException when not exists")
        void throwsWhenNotExists() {
            // given
            OrderDto dto = create(OrderDto.class);
            when(orderRepository.findByGuid(dto.guid())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.update(dto))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Order id=" + dto.guid() + " does not exist");
            verify(orderRepository).findByGuid(dto.guid());
            verify(orderRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should soft-delete existing order")
        void softDeletes() {
            // given
            UUID guid = UUID.randomUUID();
            Order order = create(Order.class);
            order.setGuid(guid);
            order.setActive(true);
            when(orderRepository.findByGuid(guid)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            orderService.delete(guid);

            // then
            assertThat(order.isActive()).isFalse();
            verify(orderRepository).findByGuid(guid);
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("should throw ServiceException when not exists")
        void throwsWhenNotExists() {
            // given
            UUID guid = UUID.randomUUID();
            when(orderRepository.findByGuid(guid)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.delete(guid))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Order id=" + guid + " does not exist");
            verify(orderRepository).findByGuid(guid);
            verify(orderRepository, never()).save(any());
        }
    }
}
