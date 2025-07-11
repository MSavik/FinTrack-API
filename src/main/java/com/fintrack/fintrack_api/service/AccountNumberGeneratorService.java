package com.fintrack.fintrack_api.service;

import com.fintrack.fintrack_api.model.Account;
import com.fintrack.fintrack_api.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Year;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.MANDATORY)
@RequiredArgsConstructor
public class AccountNumberGeneratorService {

    private final AccountRepository accountRepository;

    private static final String INSTITUTION_CODE = "FTS";

    public String generateAccountNumber(Account.AccountType type) {
        String typeCode = switch(type) {
            case CHECKING -> "C";
            case SAVINGS -> "S";
            case CREDIT_CARD -> "D";
        };

        String year = String.valueOf(Year.now().getValue());
        Optional<Long> lastSeq2 = accountRepository.findMaxSequenceForYear(year);
        Optional<Long> lastSeq3 = accountRepository.findMaxSequenceForYear("2025");
        Long lastSeq = accountRepository.findMaxSequenceForYear(String.valueOf(Year.now().getValue()))
                .orElse(0L);

        String sequencePart = String.format("%06d", lastSeq + 1);

        String base = INSTITUTION_CODE +
                typeCode +
                Year.now().getValue() +
                sequencePart;

        return base + calculateCheckDigits(base);
    }

    private String calculateCheckDigits(String base) {
        // Convert letters to numbers (A=10, B=11,... Z=35)
        StringBuilder numericBase = new StringBuilder();
        for (char c : base.toCharArray()) {
            if (Character.isLetter(c)) {
                numericBase.append(10 + Character.toUpperCase(c) - 'A');
            } else {
                numericBase.append(c);
            }
        }
        numericBase.append("00"); // Prepare for MOD97 calculation

        // Handle large numbers with BigInteger
        try {
            BigInteger bigInt = new BigInteger(numericBase.toString());
            int checksum = 98 - bigInt.mod(BigInteger.valueOf(97)).intValue();
            return String.format("%02d", checksum);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Failed to generate check digits for: " + base, e);
        }
    }
}
