package com.chaykin.paymentservice.controller;

import com.chaykin.common.exception.GlobalExceptionHandler;
import com.chaykin.common.exception.ServiceException;
import com.chaykin.common.model.payment.CreatePaymentRequest;
import com.chaykin.common.model.payment.PaymentDto;
import com.chaykin.common.model.payment.UpdatePaymentRequest;
import com.chaykin.paymentservice.converter.PaymentConverter;
import com.chaykin.paymentservice.converter.PaymentConverterImpl;
import com.chaykin.paymentservice.service.PaymentService;
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

import static com.chaykin.paymentservice.exception.ErrorMessage.PAYMENT_NOT_EXIST;
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
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        PaymentConverter converter = new PaymentConverterImpl();
        PaymentController controller = new PaymentController(paymentService, converter);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .setControllerAdvice(new GlobalExceptionHandler())
                                 .build();
    }

    @Nested
    @DisplayName("GET /payments")
    class FindAll {

        @Test
        @DisplayName("should return list of payments")
        void returnsList() throws Exception {
            // given
            List<PaymentDto> payments = Instancio.ofList(PaymentDto.class).size(2).create();
            when(paymentService.findAll()).thenReturn(payments);

            // when & then
            mockMvc.perform(get("/payments"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /payments/{guid}")
    class GetById {

        @Test
        @DisplayName("should return payment when found")
        void returnsPayment() throws Exception {
            // given
            PaymentDto dto = create(PaymentDto.class);
            when(paymentService.getById(dto.guid())).thenReturn(dto);

            // when & then
            mockMvc.perform(get("/payments/{guid}", dto.guid()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.guid").value(dto.guid().toString()));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void returns404() throws Exception {
            // given
            UUID guid = UUID.randomUUID();
            when(paymentService.getById(guid)).thenThrow(new ServiceException(PAYMENT_NOT_EXIST, guid));

            // when & then
            mockMvc.perform(get("/payments/{guid}", guid))
                   .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /payments")
    class CreatePayment {

        @Test
        @DisplayName("should create payment and return 201")
        void returnsCreated() throws Exception {
            // given
            CreatePaymentRequest request = create(CreatePaymentRequest.class);
            PaymentDto dto = create(PaymentDto.class);
            when(paymentService.create(any(PaymentDto.class))).thenReturn(dto);

            // when & then
            mockMvc.perform(post("/payments")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("PUT /payments/{guid}")
    class UpdatePayment {

        @Test
        @DisplayName("should update payment")
        void returnsUpdated() throws Exception {
            // given
            UpdatePaymentRequest request = create(UpdatePaymentRequest.class);
            PaymentDto dto = create(PaymentDto.class);
            when(paymentService.update(any(PaymentDto.class))).thenReturn(dto);

            // when & then
            mockMvc.perform(put("/payments/{guid}", request.guid())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /payments/{guid}")
    class DeletePayment {

        @Test
        @DisplayName("should return 204")
        void returnsNoContent() throws Exception {
            // given
            UUID guid = UUID.randomUUID();

            // when & then
            mockMvc.perform(delete("/payments/{guid}", guid))
                   .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 404 when not found")
        void returns404() throws Exception {
            // given
            UUID guid = UUID.randomUUID();
            doThrow(new ServiceException(PAYMENT_NOT_EXIST, guid))
                    .when(paymentService).delete(guid);

            // when & then
            mockMvc.perform(delete("/payments/{guid}", guid))
                   .andExpect(status().isNotFound());
        }
    }
}
