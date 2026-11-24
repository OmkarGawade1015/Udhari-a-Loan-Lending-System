package com.sprint.lending.controller;

import com.sprint.lending.dto.LoanApplicationRequest;
import com.sprint.lending.dto.LoanResponse;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.User;
import com.sprint.lending.service.BorrowerLoanService;
import com.sprint.lending.service.LoanApplicationService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Borrower loans")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final LoanApplicationService loanApplicationService;
    private final BorrowerLoanService borrowerLoanService;

    public LoanController(
            LoanApplicationService loanApplicationService,
            BorrowerLoanService borrowerLoanService
    ) {
        this.loanApplicationService = loanApplicationService;
        this.borrowerLoanService = borrowerLoanService;
    }

    @PostMapping
    @Operation(summary = "Apply for a loan", description = "Validate affordability and create an APPLIED loan for the authenticated borrower.")
    public ResponseEntity<LoanResponse> apply(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody LoanApplicationRequest request
    ) {
        Loan loan = loanApplicationService.apply(
                authenticatedUser,
                request.principal(),
                request.tenureMonths()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(loan));
    }

    @GetMapping
    @Operation(summary = "List my loans", description = "Return only the authenticated borrower's loans.")
    public List<LoanResponse> list(@AuthenticationPrincipal User authenticatedUser) {
        return borrowerLoanService.listLoans(authenticatedUser).stream()
                .map(LoanController::toResponse)
                .toList();
    }

    private static LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getPrincipal(),
                loan.getAnnualInterestRate(),
                loan.getTenureMonths(),
                loan.getEmi(),
                loan.getStatus(),
                loan.getReviewRemark()
        );
    }
}
