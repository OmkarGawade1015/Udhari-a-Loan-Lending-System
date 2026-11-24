package com.sprint.lending.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@Controller
public class PageController {

    private final BigDecimal minimumPrincipal;
    private final BigDecimal maximumPrincipal;
    private final BigDecimal annualInterestRate;

    public PageController(
            @Value("${app.loan.minimum-principal}") BigDecimal minimumPrincipal,
            @Value("${app.loan.maximum-principal}") BigDecimal maximumPrincipal,
            @Value("${app.loan.annual-interest-rate}") BigDecimal annualInterestRate
    ) {
        this.minimumPrincipal = minimumPrincipal;
        this.maximumPrincipal = maximumPrincipal;
        this.annualInterestRate = annualInterestRate;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/loans/apply")
    public String applyLoan(Model model) {
        model.addAttribute("minimumPrincipal", minimumPrincipal);
        model.addAttribute("maximumPrincipal", maximumPrincipal);
        model.addAttribute("annualInterestRate", annualInterestRate);
        return "apply-loan";
    }

    @GetMapping("/loans")
    public String loans() {
        return "loans";
    }

    @GetMapping("/loans/{loanId}/repayments")
    public String repayments(@PathVariable Long loanId, Model model) {
        model.addAttribute("loanId", loanId);
        return "repayments";
    }

    @GetMapping("/admin/loans")
    public String adminLoans() {
        return "admin-loans";
    }

    @GetMapping("/admin/loans/{loanId}")
    public String adminReview(@PathVariable Long loanId, Model model) {
        model.addAttribute("loanId", loanId);
        return "admin-review";
    }
}
