const loansLoading = document.querySelector("#loansLoading");
const loansError = document.querySelector("#loansError");
const loansEmpty = document.querySelector("#loansEmpty");
const loansTableWrap = document.querySelector("#loansTableWrap");
const loansTableBody = document.querySelector("#loansTableBody");

const formatMoney = (amount) => new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2
}).format(amount);

function appendCell(row, text) {
    const cell = document.createElement("td");
    cell.textContent = text;
    row.append(cell);
    return cell;
}

function renderLoan(loan) {
    const row = document.createElement("tr");
    appendCell(row, formatMoney(loan.principal));
    appendCell(row, `${loan.tenureMonths} months`);
    appendCell(row, formatMoney(loan.emi));

    const statusCell = document.createElement("td");
    const status = document.createElement("span");
    status.className = `status-chip status-${String(loan.status).toLowerCase()}`;
    status.textContent = loan.status;
    statusCell.append(status);
    row.append(statusCell);

    appendCell(row, loan.reviewRemark || "—");
    const actionCell = document.createElement("td");
    if (loan.status === "APPROVED" || loan.status === "CLOSED") {
        const link = document.createElement("a");
        link.href = `/loans/${encodeURIComponent(loan.loanId)}/repayments`;
        link.textContent = "View schedule";
        actionCell.append(link);
    }
    row.append(actionCell);
    loansTableBody.append(row);
}

async function loadLoans() {
    if (!UdhariApi.requireRole("BORROWER")) {
        return;
    }

    try {
        const body = await UdhariApi.request("/api/loans");
        if (!Array.isArray(body) || body.length === 0) {
            loansEmpty.hidden = false;
            return;
        }
        body.forEach(renderLoan);
        loansTableWrap.hidden = false;
    } catch (error) {
        if (UdhariApi.handleUnauthorized(error)) {
            return;
        }
        loansError.textContent = error.message || "A connection error occurred. Please try again.";
        loansError.hidden = false;
    } finally {
        loansLoading.hidden = true;
    }
}

loadLoans();
