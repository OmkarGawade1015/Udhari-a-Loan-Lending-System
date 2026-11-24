package com.sprint.lending.service;

import com.sprint.lending.entity.Repayment;
import com.sprint.lending.entity.User;
import com.sprint.lending.entity.UserRole;
import com.sprint.lending.exception.AuthorizationException;
import com.sprint.lending.exception.LoanNotFoundException;
import com.sprint.lending.repository.LoanRepository;
import com.sprint.lending.repository.RepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BorrowerRepaymentService {

    private final LoanRepository loanRepository;
    private final RepaymentRepository repaymentRepository;

    public BorrowerRepaymentService(LoanRepository loanRepository, RepaymentRepository repaymentRepository) {
        this.loanRepository = loanRepository;
        this.repaymentRepository = repaymentRepository;
    }

    @Transactional(readOnly = true)
    public List<Repayment> getSchedule(User authenticatedBorrower, Long loanId) {
        if (authenticatedBorrower == null || authenticatedBorrower.getId() == null) {
            throw new AuthorizationException("An authenticated borrower is required.");
        }
        if (authenticatedBorrower.getRole() != UserRole.BORROWER) {
            throw new AuthorizationException("Only borrowers can view repayment schedules.");
        }

        loanRepository.findByIdAndBorrower_User_Id(loanId, authenticatedBorrower.getId())
                .orElseThrow(() -> new LoanNotFoundException("Loan was not found for this borrower."));

        return repaymentRepository.findAllByLoan_IdOrderByInstallmentNumberAsc(loanId);
    }
}
