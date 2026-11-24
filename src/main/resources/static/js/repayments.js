const loanId = document.body.dataset.loanId;
const repaymentsLoading = document.querySelector("#repaymentsLoading");
const repaymentsError = document.querySelector("#repaymentsError");
const repaymentsEmpty = document.querySelector("#repaymentsEmpty");
const repaymentsTableWrap = document.querySelector("#repaymentsTableWrap");
const repaymentsTableBody = document.querySelector("#repaymentsTableBody");
const repaymentMessage = document.querySelector("#repaymentMessage");

const formatMoney = (amount) => new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2
}).format(amount);

function addCell(row, text) {
    const cell = document.createElement("td");
    cell.textContent = text;
    row.append(cell);
    return cell;
}

function addPayAction(cell, repaymentId) {
    const button = document.createElement("button");
    button.className = "button button-primary";
    button.type = "button";
    button.textContent = "Mark paid";
    button.addEventListener("click", async () => UdhariApi.withBusy(button, "Updating…", "Mark paid", async () => {
        repaymentsError.hidden = true;
        repaymentMessage.hidden = true;
        try {
            const body = await UdhariApi.request(`/api/repayments/${encodeURIComponent(repaymentId)}/pay`, {
                method: "PUT",
                body: { confirm: true }
            });
            repaymentMessage.textContent = body.loanStatus === "CLOSED"
                ? "Instalment marked paid. This loan is now closed."
                : "Instalment marked paid.";
            repaymentMessage.hidden = false;
            await loadRepayments();
        } catch (error) {
            if (UdhariApi.handleUnauthorized(error)) {
                return;
            }
            repaymentsError.textContent = error.message || "A connection error occurred. Please try again.";
            repaymentsError.hidden = false;
        }
    }));
    cell.append(button);
}

function renderRepayment(repayment) {
    const row = document.createElement("tr");
    addCell(row, String(repayment.installmentNumber));
    addCell(row, new Intl.DateTimeFormat("en-IN", { dateStyle: "medium" }).format(new Date(`${repayment.dueDate}T00:00:00`)));
    addCell(row, formatMoney(repayment.amount));

    const statusCell = document.createElement("td");
    const status = document.createElement("span");
    status.className = `status-chip status-${String(repayment.status).toLowerCase()}`;
    status.textContent = repayment.status;
    statusCell.append(status);
    row.append(statusCell);

    const actionCell = document.createElement("td");
    if (repayment.status === "PENDING") {
        addPayAction(actionCell, repayment.repaymentId);
    }
    row.append(actionCell);
    repaymentsTableBody.append(row);
}

async function loadRepayments() {
    if (!UdhariApi.requireRole("BORROWER")) {
        return;
    }

    repaymentsLoading.hidden = false;
    repaymentsError.hidden = true;
    repaymentsTableBody.replaceChildren();
    try {
        const body = await UdhariApi.request(`/api/loans/${encodeURIComponent(loanId)}/repayments`);
        if (!Array.isArray(body) || body.length === 0) {
            repaymentsEmpty.hidden = false;
            repaymentsTableWrap.hidden = true;
            return;
        }
        repaymentsEmpty.hidden = true;
        body.forEach(renderRepayment);
        repaymentsTableWrap.hidden = false;
    } catch (error) {
        if (UdhariApi.handleUnauthorized(error)) {
            return;
        }
        repaymentsError.textContent = error.message || "A connection error occurred. Please try again.";
        repaymentsError.hidden = false;
    } finally {
        repaymentsLoading.hidden = true;
    }
}

loadRepayments();
