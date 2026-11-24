const loanId = document.body.dataset.loanId;
const reviewLoading = document.querySelector("#reviewLoading");
const reviewError = document.querySelector("#reviewError");
const reviewSuccess = document.querySelector("#reviewSuccess");
const reviewDetails = document.querySelector("#reviewDetails");
const reviewForm = document.querySelector("#reviewForm");
const remarkInput = document.querySelector("#remark");
const approveButton = document.querySelector("#approveButton");
const rejectButton = document.querySelector("#rejectButton");

const formatMoney = (amount) => new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2
}).format(amount);

function currentAdminAuth() {
    return UdhariApi.requireRole("ADMIN");
}

async function loadApplication() {
    const auth = currentAdminAuth();
    if (!auth) {
        return;
    }

    try {
        const body = await UdhariApi.request("/api/admin/loans?status=APPLIED");

        const loan = body.find((item) => String(item.loanId) === String(loanId));
        if (!loan) {
            throw new Error("This application is not available for review.");
        }
        document.querySelector("#borrowerDetails").textContent = `${loan.borrowerName} · ${loan.borrowerEmail}`;
        document.querySelector("#principalDetails").textContent = formatMoney(loan.principal);
        document.querySelector("#tenureDetails").textContent = `${loan.tenureMonths} months`;
        document.querySelector("#emiDetails").textContent = formatMoney(loan.emi);
        document.querySelector("#statusDetails").textContent = loan.status;
        reviewDetails.hidden = false;
        reviewForm.hidden = false;
    } catch (error) {
        if (UdhariApi.handleUnauthorized(error)) {
            return;
        }
        reviewError.textContent = error.message || "A connection error occurred. Please try again.";
        reviewError.hidden = false;
    } finally {
        reviewLoading.hidden = true;
    }
}

async function submitReview(action) {
    reviewError.hidden = true;
    reviewSuccess.hidden = true;
    const remark = remarkInput.value.trim();
    if (action === "reject" && !remark) {
        reviewError.textContent = "Add a remark before rejecting this application.";
        reviewError.hidden = false;
        remarkInput.focus();
        return;
    }

    const auth = currentAdminAuth();
    if (!auth) {
        return;
    }

    approveButton.disabled = true;
    rejectButton.disabled = true;
    try {
        const body = await UdhariApi.request(`/api/admin/loans/${encodeURIComponent(loanId)}/${action}`, {
            method: "PUT",
            body: { remark }
        });
        reviewForm.hidden = true;
        reviewSuccess.textContent = `Application ${action === "approve" ? "approved" : "rejected"}.`;
        reviewSuccess.hidden = false;
        reviewDetails.querySelector("#statusDetails").textContent = body.status;
    } catch (error) {
        if (UdhariApi.handleUnauthorized(error)) {
            return;
        }
        reviewError.textContent = error.message || "A connection error occurred. Please try again.";
        reviewError.hidden = false;
    } finally {
        approveButton.disabled = false;
        rejectButton.disabled = false;
    }
}

approveButton.addEventListener("click", () => submitReview("approve"));
rejectButton.addEventListener("click", () => submitReview("reject"));
loadApplication();
