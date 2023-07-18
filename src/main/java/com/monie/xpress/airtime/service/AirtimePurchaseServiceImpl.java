package com.monie.xpress.airtime.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monie.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.monie.xpress.airtime.data.dtos.Details;
import com.monie.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.monie.xpress.airtime.data.dtos.XpressAPIRequestDTO;
import com.monie.xpress.airtime.data.models.AirtimePurchase;
import com.monie.xpress.airtime.data.models.NETWORK_PROVIDER;
import com.monie.xpress.airtime.data.models.Status;
import com.monie.xpress.airtime.data.repository.AirtimePurchaseRepository;
import com.monie.xpress.auth_config.user.data.models.User;
import com.monie.xpress.auth_config.user.services.UserService;
import com.monie.xpress.transaction.data.model.Transaction;
import com.monie.xpress.transaction.data.model.TransactionType;
import com.monie.xpress.transaction.services.TransactionService;
import com.monie.xpress.xceptions.XpressException;
import com.monie.xpress.xpress_utils.XpressUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirtimePurchaseServiceImpl implements AirtimePurchaseService {
    @Value("${private_key}")
    private String privateKey;
    @Value("${public_key}")
    private String publicKey;
    @Value("${base_url}")
    private String url;

    private final AirtimePurchaseRepository airtimePurchaseRepository;
    private final TransactionService transactionService;
    private final UserService userService;

    @Override
    public AirtimePurchaseResponse buyAirtime(PurchaseAirtimeRequestDTO requestDTO) throws IOException {
        User user = userService.findUserById(requestDTO.getUserId());
        BigDecimal amount = requestDTO.getAmount();
        String uniqueCode = uniqueCode(requestDTO.getPhoneNumber());
        AirtimePurchase airtimePurchase = buildAirtimePurchase(requestDTO, user, amount, uniqueCode);
        AirtimePurchase savedAirtimePurchase = airtimePurchaseRepository.save(airtimePurchase);

        XpressAPIRequestDTO xpressAPIRequestDTO = buildXpressAPIRequestDTO(savedAirtimePurchase);

        saveTransaction(user, amount);

        return callToXpressAPI(xpressAPIRequestDTO);
    }

    private static AirtimePurchase buildAirtimePurchase(
            PurchaseAirtimeRequestDTO requestDTO,
            User user, BigDecimal amount, String uniqueCode) {
        return AirtimePurchase.builder()
                .phoneNumber(requestDTO.getPhoneNumber())
                .amount(amount)
                .uniqueCode(uniqueCode)
                .user(user)
                .transactionTime(LocalDateTime.now())
                .status(Status.PENDING)
                .build();
    }

    private static XpressAPIRequestDTO buildXpressAPIRequestDTO(
            AirtimePurchase savedAirtimePurchase) {
        String phoneNumber = savedAirtimePurchase.getPhoneNumber();
        return XpressAPIRequestDTO.builder()
                .requestId(savedAirtimePurchase.getId())
                .uniqueCode(savedAirtimePurchase.getUniqueCode())
                .details(
                        Details.builder()
                                .amount(savedAirtimePurchase.getAmount())
                                .phoneNumber(phoneNumber)
                                .build()
                ).build();
    }

    private static String uniqueCode(String phoneNumber) {
        switch (phoneNumber) {
            case "08033333333" -> {
                return NETWORK_PROVIDER.MTN.getValue();
            }
            case "08022222222" -> {
                return NETWORK_PROVIDER.AIRTEL.getValue();
            }
            case "09099999999" -> {
                return NETWORK_PROVIDER.ETISALAT.getValue();
            }
            case "08055555555" -> {
                return NETWORK_PROVIDER.GLO.getValue();
            }
        }
        throw new XpressException("Invalid number");
    }

    private void saveTransaction(User user, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .user(user)
                .transactionType(TransactionType.BUY_AIRTIME)
                .amount(amount)
                .transactionTime(LocalDateTime.now())
                .build();
        transactionService.saveTransaction(transaction);
    }

    private AirtimePurchaseResponse callToXpressAPI(XpressAPIRequestDTO xpressAPIRequestDTO) throws IOException {
        String jsonString = new ObjectMapper().writeValueAsString(xpressAPIRequestDTO);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String PaymentHash = XpressUtils.calculateHMAC512(jsonString, privateKey);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonString);

        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Authorization", publicKey)
                .addHeader("PaymentHash", PaymentHash)
                .addHeader("channel", "api")
                .build();

        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();

        ObjectMapper objectMapper = new ObjectMapper();
        assert responseBody != null;
        AirtimePurchaseResponse airtimePurchaseResponse = objectMapper.readValue(
                responseBody.string(),
                AirtimePurchaseResponse.class
        );
        response.close();
        return airtimePurchaseResponse;
    }
}