package com.sprint.lending.service;

import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.entity.Repayment;
import com.sprint.lending.entity.RepaymentStatus;
import com.sprint.lending.entity.User;
import com.sprint.lending.entity.UserRole;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RepaymentScheduleGeneratorTest {

    private final RepaymentScheduleGenerator generator = new RepaymentScheduleGenerator();

    @Test
    void generateCreatesOnePendingInstalmentPerMonthAndAdjustsFinalAmount() {
        User borrowerUser = new User("borrower@example.test", "hash", UserRole.BORROWER);
        BorrowerProfile profile = new BorrowerProfile(
                "Test Borrower", new BigDecimal("50000.00"), "SALARIED", borrowerUser
        );
        Loan loan = new Loan(
                new BigDecimal("1000.00"),
                BigDecimal.ZERO,
                3,
                new BigDecimal("333.33"),
                LoanStatus.APPROVED,
                profile
        );
        LocalDate approvalDate = LocalDate.of(2026, 1, 31);

        List<Repayment> schedule = generator.generate(loan, approvalDate);

        assertEquals(3, schedule.size());
        assertEquals(1, schedule.get(0).getInstallmentNumber());
        assertEquals(2, schedule.get(1).getInstallmentNumber());
        assertEquals(3, schedule.get(2).getInstallmentNumber());
        assertEquals(LocalDate.of(2026, 2, 28), schedule.get(0).getDueDate());
        assertEquals(LocalDate.of(2026, 3, 31), schedule.get(1).getDueDate());
        assertEquals(LocalDate.of(2026, 4, 30), schedule.get(2).getDueDate());
        assertEquals(new BigDecimal("333.33"), schedule.get(0).getAmount());
        assertEquals(new BigDecimal("333.33"), schedule.get(1).getAmount());
        assertEquals(new BigDecimal("333.34"), schedule.get(2).getAmount());
        assertEquals(RepaymentStatus.PENDING, schedule.get(0).getStatus());
        assertEquals(RepaymentStatus.PENDING, schedule.get(1).getStatus());
        assertEquals(RepaymentStatus.PENDING, schedule.get(2).getStatus());
    }

    @Test
    void generateRejectsInvalidLoanTerms() {
        User borrowerUser = new User("borrower@example.test", "hash", UserRole.BORROWER);
        BorrowerProfile profile = new BorrowerProfile(
                "Test Borrower", new BigDecimal("50000.00"), "SALARIED", borrowerUser
        );
        Loan loan = new Loan(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                3,
                new BigDecimal("333.33"),
                LoanStatus.APPROVED,
                profile
        );

        assertThrows(IllegalArgumentException.class,
                () -> generator.generate(loan, LocalDate.of(2026, 1, 1)));
    }
}
