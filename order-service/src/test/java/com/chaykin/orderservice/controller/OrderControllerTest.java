package com.chaykin.orderservice.controller;

import com.chaykin.common.exception.GlobalExceptionHandler;
import com.chaykin.common.exception.ServiceException;
import com.chaykin.common.model.order.CreateOrderRequest;
import com.chaykin.common.model.order.OrderDto;
import com.chaykin.common.model.order.UpdateOrderRequest;
import com.chaykin.orderservice.converter.OrderConverter;
import com.chaykin.orderservice.converter.OrderConverterImpl;
import com.chaykin.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static com.chaykin.orderservice.exception.ErrorMessage.ORDER_NOT_EXIST;
import static org.instancio.Instancio.create;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        OrderConverter converter = new OrderConverterImpl();
        OrderController controller = new OrderController(orderService, converter);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .setControllerAdvice(new GlobalExceptionHandler())
                                 .build();
    }

    @Nested
    @DisplayName("GET /orders")
    class FindAll {

        @Test
        @DisplayName("should return list of orders")
        void returnsList() throws Exception {
            // given
            List<OrderDto> orders = Instancio.ofList(OrderDto.class).size(2).create();
            when(orderService.findAll()).thenReturn(orders);

            // when & then
            mockMvc.perform(get("/orders"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /orders/{guid}")
    class GetById {

        @Test
        @DisplayName("should return order when found")
        void returnsOrder() throws Exception {
            // given
            OrderDto dto = create(OrderDto.class);
            when(orderService.getById(dto.guid())).thenReturn(dto);

            // when & then
            mockMvc.perform(get("/orders/{guid}", dto.guid()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.guid").value(dto.guid().toString()));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void returns404() throws Exception {
            // given
            UUID guid = UUID.randomUUID();
            when(orderService.getById(guid)).thenThrow(new ServiceException(ORDER_NOT_EXIST, guid));

            // when & then
            mockMvc.perform(get("/orders/{guid}", guid))
                   .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /orders")
    class CreateOrder {

        @Test
        @DisplayName("should create order and return 201")
        void returnsCreated() throws Exception {
            // given
            CreateOrderRequest request = create(CreateOrderRequest.class);
            OrderDto dto = create(OrderDto.class);
            when(orderService.create(any(OrderDto.class))).thenReturn(dto);

            // when & then
            mockMvc.perform(post("/orders")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("PUT /orders/{guid}")
    class UpdateOrder {

        @Test
        @DisplayName("should update order")
        void returnsUpdated() throws Exception {
            // given
            UpdateOrderRequest request = create(UpdateOrderRequest.class);
            OrderDto dto = create(OrderDto.class);
            when(orderService.update(any(OrderDto.class))).thenReturn(dto);

            // when & then
            mockMvc.perform(put("/orders/{guid}", request.guid())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /orders/{guid}")
    class DeleteOrder {

        @Test
        @DisplayName("should return 204")
        void returnsNoContent() throws Exception {
            // given
            UUID guid = UUID.randomUUID();

            // when & then
            mockMvc.perform(delete("/orders/{guid}", guid))
                   .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 404 when not found")
        void returns404() throws Exception {
            // given
            UUID guid = UUID.randomUUID();
            doThrow(new ServiceException(ORDER_NOT_EXIST, guid))
                    .when(orderService).delete(guid);

            // when & then
            mockMvc.perform(delete("/orders/{guid}", guid))
                   .andExpect(status().isNotFound());
        }
    }
}
