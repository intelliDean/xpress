package com.monie.xpress.customer.data.repositories;

import com.monie.xpress.customer.data.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUser_EmailAddress(String emailAddress);
}
