package com.sprint.lending.service;

import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.Repayment;
import com.sprint.lending.entity.RepaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class RepaymentScheduleGenerator {

    private static final BigDecimal MONTHS_PER_YEAR = BigDecimal.valueOf(12);
    private static final MathContext CALCULATION_CONTEXT = new MathContext(24, RoundingMode.HALF_UP);
    private static final int MONEY_SCALE = 2;

    public List<Repayment> generate(Loan loan, LocalDate approvalDate) {
        if (loan == null || approvalDate == null) {
            throw new IllegalArgumentException("Loan and approval date are required.");
        }
        if (loan.getPrincipal() == null || loan.getPrincipal().signum() <= 0
                || loan.getAnnualInterestRate() == null || loan.getAnnualInterestRate().signum() < 0
                || loan.getEmi() == null || loan.getEmi().signum() <= 0
                || loan.getTenureMonths() == null || loan.getTenureMonths() <= 0) {
            throw new IllegalArgumentException("Loan financial terms are invalid.");
        }

        BigDecimal monthlyRate = loan.getAnnualInterestRate().divide(MONTHS_PER_YEAR, CALCULATION_CONTEXT);
        BigDecimal remainingPrincipal = loan.getPrincipal().setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        List<Repayment> schedule = new ArrayList<>(loan.getTenureMonths());

        for (int installment = 1; installment <= loan.getTenureMonths(); installment++) {
            BigDecimal interest = remainingPrincipal.multiply(monthlyRate, CALCULATION_CONTEXT)
                    .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            BigDecimal amount = installment == loan.getTenureMonths()
                    ? remainingPrincipal.add(interest).setScale(MONEY_SCALE, RoundingMode.HALF_UP)
                    : loan.getEmi().setScale(MONEY_SCALE, RoundingMode.HALF_UP);

            LocalDate dueDate = approvalDate.plusMonths(installment);
            schedule.add(new Repayment(
                    installment,
                    dueDate,
                    amount,
                    RepaymentStatus.PENDING,
                    loan
            ));

            if (installment < loan.getTenureMonths()) {
                BigDecimal principalPaid = amount.subtract(interest);
                remainingPrincipal = remainingPrincipal.subtract(principalPaid)
                        .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
                if (remainingPrincipal.signum() <= 0) {
                    throw new IllegalStateException("Loan terms amortize before the final instalment.");
                }
            }
        }

        return schedule;
    }
}
