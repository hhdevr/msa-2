package com.chaykin.deliveryservice.controller.docs;

import com.chaykin.common.model.delivery.CreateDeliveryRequest;
import com.chaykin.common.model.delivery.DeliveryDto;
import com.chaykin.common.model.delivery.UpdateDeliveryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Deliveries", description = "Delivery management API")
public interface DeliveryApi {

    @Operation(summary = "Get all deliveries")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<List<DeliveryDto>> findAll();

    @Operation(summary = "Get delivery by GUID")
    @ApiResponse(responseCode = "200", description = "Delivery found")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Delivery not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<DeliveryDto> getById(UUID guid);

    @Operation(summary = "Create a new delivery")
    @ApiResponse(responseCode = "201", description = "Delivery created")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<DeliveryDto> create(CreateDeliveryRequest request);

    @Operation(summary = "Update an existing delivery")
    @ApiResponse(responseCode = "200", description = "Delivery updated")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Delivery not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<DeliveryDto> update(UUID guid, UpdateDeliveryRequest request);

    @Operation(summary = "Delete a delivery (soft delete)")
    @ApiResponse(responseCode = "204", description = "Delivery deleted")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Delivery not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<Void> delete(UUID guid);
}
