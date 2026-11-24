package com.sprint.lending.repository;

import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByBorrower_User_Id(Long userId);

    Optional<Loan> findByIdAndBorrower_User_Id(Long loanId, Long userId);

    boolean existsByBorrower_IdAndStatusIn(Long borrowerId, Collection<LoanStatus> statuses);
}
