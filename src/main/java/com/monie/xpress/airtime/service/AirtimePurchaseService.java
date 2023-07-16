package com.monie.xpress.airtime.service;

import com.monie.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.monie.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface AirtimePurchaseService {
    CompletableFuture<AirtimePurchaseResponse> buyAirtime(PurchaseAirtimeRequestDTO requestDTO) throws IOException;


}
