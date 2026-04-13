package com.chaykin.deliveryservice.controller;

import com.chaykin.common.exception.GlobalExceptionHandler;
import com.chaykin.common.exception.ServiceException;
import com.chaykin.common.model.delivery.CreateDeliveryRequest;
import com.chaykin.common.model.delivery.DeliveryDto;
import com.chaykin.common.model.delivery.UpdateDeliveryRequest;
import com.chaykin.deliveryservice.converter.DeliveryConverter;
import com.chaykin.deliveryservice.converter.DeliveryConverterImpl;
import com.chaykin.deliveryservice.service.DeliveryService;
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

import static com.chaykin.deliveryservice.exception.ErrorMessage.DELIVERY_NOT_EXIST;
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
class DeliveryControllerTest {

    @Mock
    private DeliveryService deliveryService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        DeliveryConverter converter = new DeliveryConverterImpl();
        DeliveryController controller = new DeliveryController(deliveryService, converter);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .setControllerAdvice(new GlobalExceptionHandler())
                                 .build();
    }

    @Nested
    @DisplayName("GET /deliveries")
    class FindAll {

        @Test
        @DisplayName("should return list of deliveries")
        void returnsList() throws Exception {
            // given
            List<DeliveryDto> deliveries = Instancio.ofList(DeliveryDto.class).size(2).create();
            when(deliveryService.findAll()).thenReturn(deliveries);

            // when & then
            mockMvc.perform(get("/deliveries"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /deliveries/{guid}")
    class GetById {

        @Test
        @DisplayName("should return delivery when found")
        void returnsDelivery() throws Exception {
            // given
            DeliveryDto dto = create(DeliveryDto.class);
            when(deliveryService.getById(dto.guid())).thenReturn(dto);

            // when & then
            mockMvc.perform(get("/deliveries/{guid}", dto.guid()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.guid").value(dto.guid().toString()));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void returns404() throws Exception {
            // given
            UUID guid = UUID.randomUUID();
            when(deliveryService.getById(guid)).thenThrow(new ServiceException(DELIVERY_NOT_EXIST, guid));

            // when & then
            mockMvc.perform(get("/deliveries/{guid}", guid))
                   .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /deliveries")
    class CreateDelivery {

        @Test
        @DisplayName("should create delivery and return 201")
        void returnsCreated() throws Exception {
            // given
            CreateDeliveryRequest request = create(CreateDeliveryRequest.class);
            DeliveryDto dto = create(DeliveryDto.class);
            when(deliveryService.create(any(DeliveryDto.class))).thenReturn(dto);

            // when & then
            mockMvc.perform(post("/deliveries")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("PUT /deliveries/{guid}")
    class UpdateDelivery {

        @Test
        @DisplayName("should update delivery")
        void returnsUpdated() throws Exception {
            // given
            UpdateDeliveryRequest request = create(UpdateDeliveryRequest.class);
            DeliveryDto dto = create(DeliveryDto.class);
            when(deliveryService.update(any(DeliveryDto.class))).thenReturn(dto);

            // when & then
            mockMvc.perform(put("/deliveries/{guid}", request.guid())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /deliveries/{guid}")
    class DeleteDelivery {

        @Test
        @DisplayName("should return 204")
        void returnsNoContent() throws Exception {
            // given
            UUID guid = UUID.randomUUID();

            // when & then
            mockMvc.perform(delete("/deliveries/{guid}", guid))
                   .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 404 when not found")
        void returns404() throws Exception {
            // given
            UUID guid = UUID.randomUUID();
            doThrow(new ServiceException(DELIVERY_NOT_EXIST, guid))
                    .when(deliveryService).delete(guid);

            // when & then
            mockMvc.perform(delete("/deliveries/{guid}", guid))
                   .andExpect(status().isNotFound());
        }
    }
}
