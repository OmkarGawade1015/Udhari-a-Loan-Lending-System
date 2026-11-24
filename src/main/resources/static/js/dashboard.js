const dashboardLoading = document.querySelector("#dashboardLoading");
const dashboardError = document.querySelector("#dashboardError");
const dashboardEmpty = document.querySelector("#dashboardEmpty");
const loanSummary = document.querySelector("#loanSummary");

async function loadDashboard() {
    if (!UdhariApi.requireRole("BORROWER")) {
        return;
    }

    try {
        const body = await UdhariApi.request("/api/loans");
        const loans = Array.isArray(body) ? body : [];
        const counts = {
            APPLIED: 0,
            APPROVED: 0,
            REJECTED: 0,
            CLOSED: 0
        };
        loans.forEach((loan) => {
            if (Object.hasOwn(counts, loan.status)) {
                counts[loan.status] += 1;
            }
        });

        document.querySelector("#appliedCount").textContent = counts.APPLIED;
        document.querySelector("#approvedCount").textContent = counts.APPROVED;
        document.querySelector("#rejectedCount").textContent = counts.REJECTED;
        document.querySelector("#closedCount").textContent = counts.CLOSED;
        loanSummary.hidden = false;
        dashboardEmpty.hidden = loans.length > 0;
    } catch (error) {
        if (UdhariApi.handleUnauthorized(error)) {
            return;
        }
        dashboardError.textContent = error.message || "A connection error occurred. Please try again.";
        dashboardError.hidden = false;
    } finally {
        dashboardLoading.hidden = true;
    }
}

loadDashboard();
