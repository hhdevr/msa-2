package com.chaykin.paymentservice.controller.docs;

import com.chaykin.common.model.payment.CreatePaymentRequest;
import com.chaykin.common.model.payment.PaymentDto;
import com.chaykin.common.model.payment.UpdatePaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Payments", description = "Payment management API")
public interface PaymentApi {

    @Operation(summary = "Get all payments")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<List<PaymentDto>> findAll();

    @Operation(summary = "Get payment by GUID")
    @ApiResponse(responseCode = "200", description = "Payment found")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<PaymentDto> getById(UUID guid);

    @Operation(summary = "Create a new payment")
    @ApiResponse(responseCode = "201", description = "Payment created")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<PaymentDto> create(CreatePaymentRequest request);

    @Operation(summary = "Update an existing payment")
    @ApiResponse(responseCode = "200", description = "Payment updated")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<PaymentDto> update(UUID guid, UpdatePaymentRequest request);

    @Operation(summary = "Delete a payment (soft delete)")
    @ApiResponse(responseCode = "204", description = "Payment deleted")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<Void> delete(UUID guid);
}
