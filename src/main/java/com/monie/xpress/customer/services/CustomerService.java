package com.monie.xpress.customer.services;

import com.monie.xpress.customer.data.dtos.CustomerRegisterRequest;
import com.monie.xpress.customer.data.dtos.CustomerRegistrationResponse;
import com.monie.xpress.customer.data.dtos.CustomerResponse;

public interface CustomerService {

    CustomerResponse signUp(CustomerRegisterRequest request);
    CustomerRegistrationResponse verifyCustomerMail(String email, String token);


}
