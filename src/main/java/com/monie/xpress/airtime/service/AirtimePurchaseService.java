package com.monie.xpress.airtime.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.monie.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.monie.xpress.airtime.data.dtos.AirtimeResponse;
import com.monie.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;

import java.io.IOException;

public interface AirtimePurchaseService {
  AirtimePurchaseResponse buyAirtime(PurchaseAirtimeRequestDTO requestDTO) throws IOException;
}
