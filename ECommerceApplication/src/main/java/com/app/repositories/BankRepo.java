package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.entites.Bank;

public interface BankRepo extends JpaRepository<Bank, Long> {
    Bank findByName(String name);
}
