package com.sprint.lending.repository;

import com.sprint.lending.entity.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

    List<Repayment> findAllByLoan_IdOrderByInstallmentNumberAsc(Long loanId);

    Optional<Repayment> findByIdAndLoan_Id(Long repaymentId, Long loanId);
}
