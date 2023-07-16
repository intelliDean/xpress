package com.monie.xpress.customer.controllers;

import com.monie.xpress.customer.data.dtos.CustomerRegisterRequest;
import com.monie.xpress.customer.data.dtos.CustomerRegistrationResponse;
import com.monie.xpress.customer.data.dtos.CustomerResponse;
import com.monie.xpress.customer.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    @PostMapping("verify")
//    @Operation(summary = "Verify User email address")
//    public ResponseEntity<CustomerRegistrationResponse> register(
//            @RequestParam String token, @RequestParam String email
//    ) {
//        return ResponseEntity.ok(
//                customerService.verifyCustomerMail(token, email)
//        );
//    }
}
