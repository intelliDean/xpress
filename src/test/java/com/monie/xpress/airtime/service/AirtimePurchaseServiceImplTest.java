package com.monie.xpress.airtime.service;

import com.monie.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.monie.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.monie.xpress.airtime.data.models.AirtimePurchase;
import com.monie.xpress.airtime.data.models.Status;
import com.monie.xpress.airtime.data.repository.AirtimePurchaseRepository;
import com.monie.xpress.auth_config.user.data.enums.Role;
import com.monie.xpress.auth_config.user.data.models.User;
import com.monie.xpress.auth_config.user.services.UserService;
import com.monie.xpress.transaction.data.model.Transaction;
import com.monie.xpress.transaction.data.model.TransactionType;
import com.monie.xpress.transaction.services.TransactionService;
import com.monie.xpress.transaction.services.TransactionServiceImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AirtimePurchaseServiceImplTest {
    private final AirtimePurchaseRepository airtimePurchaseRepository =
            mock(AirtimePurchaseRepository.class);
    private final TransactionService transactionService =
            mock(TransactionServiceImpl.class);
    private final UserService userService =
            mock(UserService.class);

    private final AirtimePurchaseService airtimePurchaseService
            = new AirtimePurchaseServiceImpl(airtimePurchaseRepository, transactionService, userService);

    @Test
    void buyAirtime() throws IOException {
        User user = User.builder()
                .id(1L)
                .roles(Collections.singleton(Role.CUSTOMER))
                .isEnabled(true)
                .password("Password")
                .emailAddress("email@gmail.com")
                .fullName("Full Name")
                .build();

        AirtimePurchase airtimePurchase = AirtimePurchase.builder()
                .id("String")
                .phoneNumber("08033333333")
                .amount(BigDecimal.valueOf(2300))
                .uniqueCode("MTN_24207")
                .user(user)
                .transactionTime(LocalDateTime.now())
                .status(Status.PENDING)
                .build();
        AirtimePurchase savedAirtimePurchase = AirtimePurchase.builder()
                .id(airtimePurchase.getId())
                .phoneNumber(airtimePurchase.getPhoneNumber())
                .amount(airtimePurchase.getAmount())
                .uniqueCode(airtimePurchase.getUniqueCode())
                .user(airtimePurchase.getUser())
                .transactionTime(airtimePurchase.getTransactionTime())
                .status(airtimePurchase.getStatus())
                .build();

        Transaction transaction = Transaction.builder()
                .user(mock(User.class))
                .transactionType(TransactionType.BUY_AIRTIME)
                .amount(BigDecimal.valueOf(2300))
                .transactionTime(airtimePurchase.getTransactionTime())
                .build();
        ;
        when(userService.findUserById(1L)).thenReturn(user);
        when(airtimePurchaseRepository.save(airtimePurchase))
                .thenReturn(savedAirtimePurchase);
        doNothing().when(transactionService).saveTransaction(transaction);

        AirtimePurchaseResponse response = airtimePurchaseService.buyAirtime(
                PurchaseAirtimeRequestDTO.builder()
                        .userId(1L)
                        .phoneNumber("08095729090")
                        .amount(BigDecimal.valueOf(1200))
                        .build()
        );
        assertThat(response).isNotNull().isInstanceOf(AirtimePurchaseResponse.class);

    }
}