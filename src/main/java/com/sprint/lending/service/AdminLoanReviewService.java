package com.sprint.lending.service;

import com.sprint.lending.dto.AdminLoanReviewResponse;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.entity.Repayment;
import com.sprint.lending.entity.User;
import com.sprint.lending.entity.UserRole;
import com.sprint.lending.exception.AuthorizationException;
import com.sprint.lending.exception.InvalidLoanStateException;
import com.sprint.lending.exception.LoanNotFoundException;
import com.sprint.lending.repository.LoanRepository;
import com.sprint.lending.repository.RepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class AdminLoanReviewService {

    private final LoanRepository loanRepository;
    private final RepaymentRepository repaymentRepository;
    private final RepaymentScheduleGenerator repaymentScheduleGenerator;

    public AdminLoanReviewService(
            LoanRepository loanRepository,
            RepaymentRepository repaymentRepository,
            RepaymentScheduleGenerator repaymentScheduleGenerator
    ) {
        this.loanRepository = loanRepository;
        this.repaymentRepository = repaymentRepository;
        this.repaymentScheduleGenerator = repaymentScheduleGenerator;
    }

    @Transactional
    public Loan approve(User authenticatedAdmin, Long loanId, String remark) {
        requireAdmin(authenticatedAdmin);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan was not found."));
        if (!loan.getStatus().canTransitionTo(LoanStatus.APPROVED)) {
            throw new InvalidLoanStateException("Only an APPLIED loan can be approved.");
        }
        if (remark == null || remark.isBlank()) {
            throw new IllegalArgumentException("An approval remark is required.");
        }
        if (!repaymentRepository.findAllByLoan_IdOrderByInstallmentNumberAsc(loanId).isEmpty()) {
            throw new InvalidLoanStateException("A repayment schedule already exists for this loan.");
        }

        loan.setStatus(LoanStatus.APPROVED);
        loan.setReviewRemark(normalizeRemark(remark));
        Loan approvedLoan = loanRepository.save(loan);

        List<Repayment> schedule = repaymentScheduleGenerator.generate(approvedLoan, LocalDate.now());
        repaymentRepository.saveAll(schedule);
        return approvedLoan;
    }

    @Transactional
    public Loan reject(User authenticatedAdmin, Long loanId, String remark) {
        requireAdmin(authenticatedAdmin);
        if (remark == null || remark.isBlank()) {
            throw new IllegalArgumentException("A rejection remark is required.");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan was not found."));
        if (!loan.getStatus().canTransitionTo(LoanStatus.REJECTED)) {
            throw new InvalidLoanStateException("Only an APPLIED loan can be rejected.");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setReviewRemark(remark.trim());
        return loanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    public List<AdminLoanReviewResponse> listLoans(User authenticatedAdmin, String statusFilter) {
        requireAdmin(authenticatedAdmin);

        LoanStatus requestedStatus = null;
        if (statusFilter != null && !statusFilter.isBlank()) {
            try {
                requestedStatus = LoanStatus.valueOf(statusFilter.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Unknown loan status: " + statusFilter);
            }
        }

        LoanStatus filter = requestedStatus;
        return loanRepository.findAll().stream()
                .filter(loan -> filter == null || loan.getStatus() == filter)
                .map(AdminLoanReviewService::toReviewResponse)
                .toList();
    }

    private void requireAdmin(User user) {
        if (user == null || user.getId() == null || user.getRole() != UserRole.ADMIN) {
            throw new AuthorizationException("Administrator access is required.");
        }
    }

    private String normalizeRemark(String remark) {
        if (remark == null || remark.isBlank()) {
            return null;
        }
        return remark.trim();
    }

    private static AdminLoanReviewResponse toReviewResponse(Loan loan) {
        return new AdminLoanReviewResponse(
                loan.getId(),
                loan.getBorrower().getFullName(),
                loan.getBorrower().getUser().getEmail(),
                loan.getPrincipal(),
                loan.getTenureMonths(),
                loan.getEmi(),
                loan.getStatus(),
                loan.getReviewRemark()
        );
    }
}
