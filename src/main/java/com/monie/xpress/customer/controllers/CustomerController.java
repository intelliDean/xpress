package com.monie.xpress.customer.controllers;

import com.monie.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.monie.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.monie.xpress.customer.data.dtos.CustomerRegisterRequest;
import com.monie.xpress.customer.data.dtos.CustomerResponse;
import com.monie.xpress.customer.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
@Tag(name = "Customer Controller")
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("register")
    @Operation(summary = "New customer registers")
    public ResponseEntity<CustomerResponse> register(@RequestBody CustomerRegisterRequest request) {
        return new ResponseEntity<>(customerService.signUp(request), HttpStatus.CREATED);
    }

    @PostMapping("buy-airtime")
    @Operation(summary = "Buy airtime")
    public ResponseEntity<CompletableFuture<AirtimePurchaseResponse>> buyAirtime(
            @RequestBody PurchaseAirtimeRequestDTO requestDTO) throws IOException {
        return ResponseEntity.ok(
                customerService.buyAirtime(requestDTO)
        );
    }
}