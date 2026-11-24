package com.sprint.lending.controller;

import com.sprint.lending.dto.PaymentRequest;
import com.sprint.lending.dto.PaymentResponse;
import com.sprint.lending.dto.RepaymentResponse;
import com.sprint.lending.entity.Repayment;
import com.sprint.lending.entity.User;
import com.sprint.lending.service.BorrowerRepaymentService;
import com.sprint.lending.service.RepaymentPaymentService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Repayments")
@SecurityRequirement(name = "bearerAuth")
public class RepaymentController {

    private final BorrowerRepaymentService borrowerRepaymentService;
    private final RepaymentPaymentService repaymentPaymentService;

    public RepaymentController(
            BorrowerRepaymentService borrowerRepaymentService,
            RepaymentPaymentService repaymentPaymentService
    ) {
        this.borrowerRepaymentService = borrowerRepaymentService;
        this.repaymentPaymentService = repaymentPaymentService;
    }

    @GetMapping("/api/loans/{id}/repayments")
    @Operation(summary = "View a repayment schedule", description = "Return a schedule only when the loan belongs to the authenticated borrower.")
    public List<RepaymentResponse> getSchedule(
            @AuthenticationPrincipal User authenticatedBorrower,
            @PathVariable Long id
    ) {
        return borrowerRepaymentService.getSchedule(authenticatedBorrower, id).stream()
                .map(RepaymentController::toResponse)
                .toList();
    }

    @PutMapping("/api/repayments/{id}/pay")
    @Operation(summary = "Mark an instalment paid", description = "Update demo repayment tracking for one pending instalment.")
    public PaymentResponse markPaid(
            @AuthenticationPrincipal User authenticatedBorrower,
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequest request
    ) {
        Repayment repayment = repaymentPaymentService.markPaid(authenticatedBorrower, id);
        return new PaymentResponse(
                repayment.getId(),
                repayment.getStatus(),
                repayment.getLoan().getStatus()
        );
    }

    private static RepaymentResponse toResponse(Repayment repayment) {
        return new RepaymentResponse(
                repayment.getId(),
                repayment.getInstallmentNumber(),
                repayment.getDueDate(),
                repayment.getAmount(),
                repayment.getStatus()
        );
    }
}
