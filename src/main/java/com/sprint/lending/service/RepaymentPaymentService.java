package com.sprint.lending.service;

import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.entity.Repayment;
import com.sprint.lending.entity.RepaymentStatus;
import com.sprint.lending.entity.User;
import com.sprint.lending.entity.UserRole;
import com.sprint.lending.exception.AuthorizationException;
import com.sprint.lending.exception.InvalidLoanStateException;
import com.sprint.lending.exception.RepaymentNotFoundException;
import com.sprint.lending.repository.LoanRepository;
import com.sprint.lending.repository.RepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RepaymentPaymentService {

    private final RepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;

    public RepaymentPaymentService(RepaymentRepository repaymentRepository, LoanRepository loanRepository) {
        this.repaymentRepository = repaymentRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public Repayment markPaid(User authenticatedBorrower, Long repaymentId) {
        if (authenticatedBorrower == null || authenticatedBorrower.getId() == null) {
            throw new AuthorizationException("An authenticated borrower is required.");
        }
        if (authenticatedBorrower.getRole() != UserRole.BORROWER) {
            throw new AuthorizationException("Only borrowers can mark instalments paid.");
        }

        Repayment repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new RepaymentNotFoundException("Repayment was not found."));
        Loan loan = repayment.getLoan();
        if (!loan.getBorrower().getUser().getId().equals(authenticatedBorrower.getId())) {
            throw new AuthorizationException("This repayment does not belong to the authenticated borrower.");
        }
        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new InvalidLoanStateException("Payments can only be recorded for an APPROVED loan.");
        }
        if (!repayment.getStatus().canTransitionTo(RepaymentStatus.PAID)) {
            throw new InvalidLoanStateException("Only a PENDING instalment can be marked paid.");
        }

        repayment.setStatus(RepaymentStatus.PAID);
        Repayment paidRepayment = repaymentRepository.save(repayment);

        List<Repayment> schedule = repaymentRepository
                .findAllByLoan_IdOrderByInstallmentNumberAsc(loan.getId());
        boolean allPaid = schedule.stream().allMatch(item -> item.getStatus() == RepaymentStatus.PAID);
        if (allPaid) {
            if (!loan.getStatus().canTransitionTo(LoanStatus.CLOSED)) {
                throw new InvalidLoanStateException("All instalments are paid, but the loan cannot be closed from its current state.");
            }
            loan.setStatus(LoanStatus.CLOSED);
            loanRepository.save(loan);
        }

        return paidRepayment;
    }
}
