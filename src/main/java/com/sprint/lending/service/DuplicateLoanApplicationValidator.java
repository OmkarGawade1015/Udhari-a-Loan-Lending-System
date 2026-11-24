package com.sprint.lending.service;

import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.exception.DuplicateLoanApplicationException;
import com.sprint.lending.repository.LoanRepository;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class DuplicateLoanApplicationValidator {

    private static final EnumSet<LoanStatus> ACTIVE_STATUSES = EnumSet.of(
            LoanStatus.APPLIED,
            LoanStatus.APPROVED
    );

    private final LoanRepository loanRepository;

    public DuplicateLoanApplicationValidator(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public void validate(BorrowerProfile borrower) {
        if (borrower == null || borrower.getId() == null) {
            throw new IllegalArgumentException("A saved borrower profile is required.");
        }
        if (loanRepository.existsByBorrower_IdAndStatusIn(borrower.getId(), ACTIVE_STATUSES)) {
            throw new DuplicateLoanApplicationException("You already have an active loan application or loan.");
        }
    }
}
