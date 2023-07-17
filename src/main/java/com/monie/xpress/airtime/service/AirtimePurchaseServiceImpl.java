package com.monie.xpress.airtime.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monie.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.monie.xpress.airtime.data.dtos.Details;
import com.monie.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.monie.xpress.airtime.data.dtos.XpressAPIRequestDTO;
import com.monie.xpress.airtime.data.models.AirtimePurchase;
import com.monie.xpress.airtime.data.models.CATEGORY;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirtimePurchaseServiceImpl implements AirtimePurchaseService {

    private final AirtimePurchaseRepository airtimePurchaseRepository;
    private final TransactionService transactionService;
    private final WebClient.Builder webClient;
    private final UserService userService;

    @Override
    public AirtimePurchaseResponse buyAirtime(PurchaseAirtimeRequestDTO requestDTO) throws IOException {
        User user = userService.findUserById(requestDTO.getUserId());
        BigDecimal amount = requestDTO.getAmount();
        String uniqueCode = XpressUtils.generateToken(12);
        AirtimePurchase airtimePurchase = buildAirtimePurchase(requestDTO, user, amount, uniqueCode);
        AirtimePurchase savedAirtimePurchase = airtimePurchaseRepository.save(airtimePurchase);

        XpressAPIRequestDTO xpressAPIRequestDTO = buildXpressAPIRequestDTO(savedAirtimePurchase);

        saveTransaction(user, amount);

        return toBuyAirtime(xpressAPIRequestDTO);
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
                .uniqueCode(uniqueCode(phoneNumber))
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
                return CATEGORY.MTN.getValue();
            }
            case "08022222222" -> {
                return CATEGORY.AIRTEL.getValue();
            }case "08099999999" -> {
                return CATEGORY.ETISALAT.getValue();
            }case "08055555555" -> {
                return CATEGORY.GLO.getValue();
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


    public AirtimePurchaseResponse callToBuyAirtime(XpressAPIRequestDTO xpressAPIRequestDTO) throws JsonProcessingException {
        String jsonString = new ObjectMapper().writeValueAsString(xpressAPIRequestDTO);
        log.info("Stringed obj{}", jsonString);
        String PaymentHash = calculateHMAC512(jsonString, "4QFXNsr4tFv5Iki8QNOzo2ET5qExeUl4_CVASPRV");
        log.info("payment hash{}", PaymentHash);
        return webClient
                .baseUrl("https://billerstest.xpresspayments.com:9603/api/v1/airtime/fulfil")
                .defaultHeader(
                        "Authorization", "Bearer Afezg4BxVS9mbt4ECgEGP0qPWf97Uzvx_CVASPUB",
                        "PaymentHash", PaymentHash,
                        "channel", "api"
                )
                .build()
                .post()
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(Mono.just(xpressAPIRequestDTO), XpressAPIRequestDTO.class)
                .retrieve()
                .bodyToMono(AirtimePurchaseResponse.class)
                .block();
    }


    public static String calculateHMAC512(String data, String key) {
        String HMAC_SHA512 = "HmacSHA512";

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    HMAC_SHA512
            );
            Mac mac = Mac.getInstance(HMAC_SHA512);
            mac.init(secretKeySpec);
            return String.valueOf(
                    Hex.encode(
                            mac.doFinal(
                                    data.getBytes(StandardCharsets.UTF_8)
                            )
                    )
            );
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    @Async
//    public AirtimePurchaseResponse buyAirtime(XpressAPIRequestDTO xpressAPIRequestDTO) throws IOException {
//        String jsonString = new ObjectMapper().writeValueAsString(xpressAPIRequestDTO);
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        String PaymentHash = calculateHMAC512(jsonString, "4QFXNsr4tFv5Iki8QNOzo2ET5qExeUl4_CVASPRV");
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, jsonString);
//
//        Request request = new Request.Builder()
//                .url("https://billerstest.xpresspayments.com:9603/api/v1/airtime/fulfil")
//                .method("POST", body)
//                .addHeader("Authorization", "Bearer Afezg4BxVS9mbt4ECgEGP0qPWf97Uzvx_CVASPUB")
//                .addHeader("PaymentHash", PaymentHash)
//                .addHeader("channel", "api")
//                .build();
//        System.out.println("\n\n then reach here    \n\n");
//        Response response = client.newCall(request).execute();
//        /// mapre res = response.
//        System.out.println("\n\n then finally reach here    \n\n with the response" + response);
//
//        ResponseBody airtimePurchaseResponse = response.body();
//        return null;
// //   }

    public AirtimePurchaseResponse toBuyAirtime(XpressAPIRequestDTO xpressAPIRequestDTO) throws IOException {
        String jsonString = new ObjectMapper().writeValueAsString(xpressAPIRequestDTO);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String PaymentHash = calculateHMAC512(jsonString, "4QFXNsr4tFv5Iki8QNOzo2ET5qExeUl4_CVASPRV");
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonString);

        Request request = new Request.Builder()
                .url("https://billerstest.xpresspayments.com:9603/api/v1/airtime/fulfil")
                .method("POST", body)
                .addHeader("Authorization", "Bearer Afezg4BxVS9mbt4ECgEGP0qPWf97Uzvx_CVASPUB")
                .addHeader("PaymentHash", PaymentHash)
                .addHeader("channel", "api")
                .build();

       // OkHttpClient client = new OkHttpClient().newBuilder().build();
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

    // Other methods and code...

}