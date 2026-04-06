package com.chaykin.orderservice.controller.docs;

import com.chaykin.common.model.order.CreateOrderRequest;
import com.chaykin.common.model.order.OrderDto;
import com.chaykin.common.model.order.UpdateOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Orders", description = "Order management API")
public interface OrderApi {

    @Operation(summary = "Get all orders")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<List<OrderDto>> findAll();

    @Operation(summary = "Get order by GUID")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<OrderDto> getById(UUID guid);

    @Operation(summary = "Create a new order")
    @ApiResponse(responseCode = "201", description = "Order created")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<OrderDto> create(CreateOrderRequest request);

    @Operation(summary = "Update an existing order")
    @ApiResponse(responseCode = "200", description = "Order updated")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<OrderDto> update(UpdateOrderRequest request);

    @Operation(summary = "Delete an order (soft delete)")
    @ApiResponse(responseCode = "204", description = "Order deleted")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<Void> delete(UUID guid);
}
