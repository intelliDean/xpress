package com.monie.xpress.airtime.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monie.xpress.auth_config.user.data.models.User;
import com.monie.xpress.auth_config.user.services.UserService;
import com.monie.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.monie.xpress.airtime.data.dtos.Details;
import com.monie.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.monie.xpress.airtime.data.dtos.XpressAPIRequestDTO;
import com.monie.xpress.airtime.data.models.AirtimePurchase;
import com.monie.xpress.airtime.data.models.Status;
import com.monie.xpress.airtime.data.repository.AirtimePurchaseRepository;
import com.monie.xpress.xpress_utils.XpressUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AirtimePurchaseServiceImpl implements AirtimePurchaseService {

    private final AirtimePurchaseRepository airtimePurchaseRepository;
    private final WebClient.Builder webClient;
    private final UserService userService;

    @Override
    public CompletableFuture<AirtimePurchaseResponse> buyAirtime(PurchaseAirtimeRequestDTO requestDTO) throws IOException {
        User user = userService.findUserById(requestDTO.getUserId());
        String uniqueCode = XpressUtils.generateToken(12);
        AirtimePurchase airtimePurchase = AirtimePurchase.builder()
                .phoneNumber(requestDTO.getPhoneNumber())
                .amount(requestDTO.getAmount())
                .uniqueCode(uniqueCode)
                .user(user)
                .transactionTime(LocalDateTime.now())
                .status(Status.PENDING)
                .build();
        AirtimePurchase savedAirtimePurchase = airtimePurchaseRepository.save(airtimePurchase);

        XpressAPIRequestDTO xpressAPIRequestDTO = XpressAPIRequestDTO.builder()
                .requestId(savedAirtimePurchase.getId())
                .uniqueCode(savedAirtimePurchase.getUniqueCode())
                .details(
                        Details.builder()
                                .amount(savedAirtimePurchase.getAmount())
                                .phoneNumber(savedAirtimePurchase.getPhoneNumber())
                                .build()
                ).build();
        return callToBuyAirtime (xpressAPIRequestDTO);
    }

    @Async
    public CompletableFuture<AirtimePurchaseResponse> callToBuyAirtime(XpressAPIRequestDTO xpressAPIRequestDTO) throws JsonProcessingException {
         String jsonString = new ObjectMapper().writeValueAsString(xpressAPIRequestDTO);
        String PaymentHash = calculateHMAC512(jsonString, "VN0tjjVLn1jTO88VRfCqZ9hA0QiAxHUI_CVASPRV");
        return CompletableFuture.completedFuture(webClient
                .baseUrl("https://billerstest.xpresspayments.com:9603/api/v1/airtime/fulfil")
                .defaultHeader(
                        "Authorization", "Bearer q7FcIWJvQtSt2lAtxEvpPPZZhdDn9qIV_CVASPUB",
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
                .block());
//
////       return CompletableFuture.completedFuture(webClient
////               .baseUrl("https://billerstest.xpresspayments.com:9603/api/v1/airtime/fulfil")
////               .defaultHeader("Authorization:", "Bearer q7FcIWJvQtSt2lAtxEvpPPZZhdDn9qIV_CVASPUB")
////               .build()
////               .post()
////               .bodyValue(xpressAPIRequestDTO)
////               .retrieve()
////               .bodyToMono(AirtimePurchaseResponse.class)
////               .block());
//
   }

//    @Async
//    public CompletableFuture<AirtimePurchaseResponse> buyAirtime(XpressAPIRequestDTO xpressAPIRequestDTO) throws IOException {
//        String jsonString = new ObjectMapper().writeValueAsString(xpressAPIRequestDTO);
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        String PaymentHash = calculateHMAC512(jsonString, "VN0tjjVLn1jTO88VRfCqZ9hA0QiAxHUI_CVASPRV");
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, jsonString);
//        Request request = new Request.Builder()
//                .url("https://billerstest.xpresspayments.com:9603/api/v1/airtime/fulfil")
//                .method("POST", body)
//                .addHeader("Authorization", "Bearer q7FcIWJvQtSt2lAtxEvpPPZZhdDn9qIV_CVASPUB")
//                .addHeader("PaymentHash", PaymentHash)
//                .addHeader("channel", "api")
//                .build();
//        Response response = client.newCall(request).execute();
//        return CompletableFuture.completedFuture(
//                new ModelMapper().map(response.body(),
//                        AirtimePurchaseResponse.class)
//        );
//    }

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

//    public static String hashString(String input, String key) {
//
//        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
//
//        try {
//            MessageDigest digest = MessageDigest.getInstance("HmacSHA512");
//            byte[] hashedBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
//            // Convert the byte array to a hexadecimal string representation
//
//            Mac mac = null;
//
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : hashedBytes) {
//
//                mac = Mac.getInstance("HmacSHA512");
//
//                mac.     init(secretKeySpec);
//
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) {
//                    hexString.append('0');
//                }
//                hexString.append(hex);
//            }
//            return hexString.toString();
//        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e.getMessage());
//        }
//    }


}
