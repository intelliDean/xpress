package com.monie.xpress.customer.services;

import com.monie.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.monie.xpress.airtime.data.dtos.AirtimeResponse;
import com.monie.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.monie.xpress.customer.data.dtos.CustomerRegisterRequest;
import com.monie.xpress.customer.data.dtos.CustomerRegistrationResponse;
import com.monie.xpress.customer.data.dtos.CustomerResponse;

import java.io.IOException;

public interface CustomerService {

    CustomerResponse signUp(CustomerRegisterRequest request);

    CustomerRegistrationResponse verifyCustomerMail(String email, String token);

    AirtimePurchaseResponse buyAirtime(PurchaseAirtimeRequestDTO requestDTO) throws IOException;


}
