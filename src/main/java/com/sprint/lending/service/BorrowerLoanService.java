package com.sprint.lending.service;

import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.User;
import com.sprint.lending.entity.UserRole;
import com.sprint.lending.exception.AuthorizationException;
import com.sprint.lending.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BorrowerLoanService {

    private final LoanRepository loanRepository;

    public BorrowerLoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Transactional(readOnly = true)
    public List<Loan> listLoans(User authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.getId() == null) {
            throw new AuthorizationException("An authenticated borrower is required.");
        }
        if (authenticatedUser.getRole() != UserRole.BORROWER) {
            throw new AuthorizationException("Only borrowers can view borrower loan lists.");
        }
        return loanRepository.findAllByBorrower_User_Id(authenticatedUser.getId());
    }
}
