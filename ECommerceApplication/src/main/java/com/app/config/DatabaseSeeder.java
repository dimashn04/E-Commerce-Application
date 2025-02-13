package com.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.app.entites.Bank;
import com.app.repositories.BankRepo;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final BankRepo bankRepository;

    public DatabaseSeeder(BankRepo bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public void run(String... args) {
        if (bankRepository.count() == 0) {
            List<Bank> banks = List.of(
                new Bank(null, "BCA", "1234567890"),
                new Bank(null, "Mandiri", "0987654321"),
                new Bank(null, "BRI", "5678901234"),
                new Bank(null, "BNI", "4321098765")
            );
            bankRepository.saveAll(banks);
        }
    }
}
