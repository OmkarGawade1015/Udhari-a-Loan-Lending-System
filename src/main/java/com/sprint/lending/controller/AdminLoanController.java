//admin loan controller java file

package com.sprint.lending.controller;

import com.sprint.lending.dto.AdminLoanReviewResponse;
import com.sprint.lending.dto.LoanResponse;
import com.sprint.lending.dto.ReviewRequest;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.User;
import com.sprint.lending.service.AdminLoanReviewService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loans")
@Tag(name = "Admin loans")
@SecurityRequirement(name = "bearerAuth")
public class AdminLoanController {

    private final AdminLoanReviewService adminLoanReviewService;

    public AdminLoanController(AdminLoanReviewService adminLoanReviewService) {
        this.adminLoanReviewService = adminLoanReviewService;
    }

    @GetMapping
    @Operation(summary = "List loan applications", description = "Optionally filter the admin loan list by status.")
    public List<AdminLoanReviewResponse> list(
            @AuthenticationPrincipal User authenticatedAdmin,
            @RequestParam(required = false) String status
    ) {
        return adminLoanReviewService.listLoans(authenticatedAdmin, status);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a loan", description = "Approve an APPLIED loan and create its repayment schedule.")
    public LoanResponse approve(
            @AuthenticationPrincipal User authenticatedAdmin,
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request
    ) {
        return toResponse(adminLoanReviewService.approve(authenticatedAdmin, id, request.remark()));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a loan", description = "Reject an APPLIED loan and save the admin remark.")
    public LoanResponse reject(
            @AuthenticationPrincipal User authenticatedAdmin,
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request
    ) {
        return toResponse(adminLoanReviewService.reject(authenticatedAdmin, id, request.remark()));
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
