package com.monie.xpress.airtime.data.repository;

import com.monie.xpress.airtime.data.models.AirtimePurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirtimePurchaseRepository extends JpaRepository<AirtimePurchase, String> {
}
