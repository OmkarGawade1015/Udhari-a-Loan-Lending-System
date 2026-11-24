package com.sprint.lending.service;

import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.entity.User;
import com.sprint.lending.entity.UserRole;
import com.sprint.lending.exception.AuthorizationException;
import com.sprint.lending.exception.BorrowerProfileNotFoundException;
import com.sprint.lending.repository.BorrowerProfileRepository;
import com.sprint.lending.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LoanApplicationService {

    private final BorrowerProfileRepository borrowerProfileRepository;
    private final LoanRepository loanRepository;
    private final EmiCalculator emiCalculator;
    private final LoanApplicationValidator loanApplicationValidator;
    private final AffordabilityValidator affordabilityValidator;
    private final DuplicateLoanApplicationValidator duplicateLoanApplicationValidator;
    private final BigDecimal annualInterestRate;

    public LoanApplicationService(
            BorrowerProfileRepository borrowerProfileRepository,
            LoanRepository loanRepository,
            EmiCalculator emiCalculator,
            LoanApplicationValidator loanApplicationValidator,
            AffordabilityValidator affordabilityValidator,
            DuplicateLoanApplicationValidator duplicateLoanApplicationValidator,
            @Value("${app.loan.annual-interest-rate}") BigDecimal annualInterestRate
    ) {
        this.borrowerProfileRepository = borrowerProfileRepository;
        this.loanRepository = loanRepository;
        this.emiCalculator = emiCalculator;
        this.loanApplicationValidator = loanApplicationValidator;
        this.affordabilityValidator = affordabilityValidator;
        this.duplicateLoanApplicationValidator = duplicateLoanApplicationValidator;
        this.annualInterestRate = annualInterestRate;
    }

    @Transactional
    public Loan apply(User authenticatedUser, BigDecimal principal, Integer tenureMonths) {
        if (authenticatedUser == null || authenticatedUser.getId() == null) {
            throw new AuthorizationException("An authenticated borrower is required.");
        }
        if (authenticatedUser.getRole() != UserRole.BORROWER) {
            throw new AuthorizationException("Only borrowers can apply for a loan.");
        }

        BorrowerProfile borrower = borrowerProfileRepository.findByUser_Id(authenticatedUser.getId())
                .orElseThrow(() -> new BorrowerProfileNotFoundException("Borrower profile was not found."));

        loanApplicationValidator.validate(principal, tenureMonths);
        duplicateLoanApplicationValidator.validate(borrower);

        BigDecimal emi = emiCalculator.calculate(principal, annualInterestRate, tenureMonths);
        affordabilityValidator.validate(emi, borrower.getMonthlyIncome());

        Loan application = new Loan(
                principal,
                annualInterestRate,
                tenureMonths,
                emi,
                LoanStatus.APPLIED,
                borrower
        );
        return loanRepository.save(application);
    }
}
