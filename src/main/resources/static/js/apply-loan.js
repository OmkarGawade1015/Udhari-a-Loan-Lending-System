const loanApplicationForm = document.querySelector("#loanApplicationForm");
const applyButton = document.querySelector("#applyButton");
const applicationError = document.querySelector("#applicationError");
const applicationSuccess = document.querySelector("#applicationSuccess");
const emiEstimate = document.querySelector("#emiEstimate");
const applicationResult = document.querySelector("#applicationResult");
const principalInput = document.querySelector("#principal");
const tenureInput = document.querySelector("#tenureMonths");
const minimumPrincipal = Number(principalInput.min);
const maximumPrincipal = Number(principalInput.max);

function estimateEmi(principal, tenureMonths, annualRate) {
    const monthlyRate = annualRate / 12;
    if (monthlyRate === 0) {
        return principal / tenureMonths;
    }
    const factor = (1 + monthlyRate) ** tenureMonths;
    return principal * monthlyRate * factor / (factor - 1);
}

function updateEmiEstimate() {
    const principal = Number(principalInput.value);
    const tenureMonths = Number(tenureInput.value);
    if (!principal || !tenureMonths || principal < minimumPrincipal || principal > maximumPrincipal || tenureMonths < 3 || tenureMonths > 60) {
        emiEstimate.textContent = "Enter an amount and tenure";
        return;
    }

    const annualRate = Number(loanApplicationForm.dataset.annualRate || "0.18");
    const estimated = estimateEmi(principal, tenureMonths, annualRate);
    emiEstimate.textContent = new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 2
    }).format(estimated);
}

principalInput?.addEventListener("input", updateEmiEstimate);
tenureInput?.addEventListener("input", updateEmiEstimate);

if (!UdhariApi.requireRole("BORROWER")) {
    loanApplicationForm.hidden = true;
}

loanApplicationForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
    applicationError.hidden = true;
    applicationSuccess.hidden = true;
    applicationResult.hidden = true;

    if (!loanApplicationForm.reportValidity()) {
        return;
    }

    if (!UdhariApi.requireRole("BORROWER")) {
        return;
    }

    await UdhariApi.withBusy(applyButton, "Submitting…", "Submit application", async () => {
        try {
            const body = await UdhariApi.request("/api/loans", {
                method: "POST",
                body: {
                principal: Number(principalInput.value),
                tenureMonths: Number(tenureInput.value)
                }
            });

            applicationSuccess.textContent = "Your application has been submitted.";
            applicationSuccess.hidden = false;
            applicationResult.textContent = `Application #${body.loanId} · Monthly EMI ${new Intl.NumberFormat("en-IN", {
                style: "currency", currency: "INR", maximumFractionDigits: 2
            }).format(body.emi)}`;
            applicationResult.hidden = false;
        } catch (error) {
            if (UdhariApi.handleUnauthorized(error)) {
                return;
            }
            applicationError.textContent = error.message || "We could not submit your application.";
            applicationError.hidden = false;
        }
    });
});
