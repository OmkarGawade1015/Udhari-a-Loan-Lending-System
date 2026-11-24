const adminLoansLoading = document.querySelector("#adminLoansLoading");
const adminLoansError = document.querySelector("#adminLoansError");
const adminLoansEmpty = document.querySelector("#adminLoansEmpty");
const adminLoansTableWrap = document.querySelector("#adminLoansTableWrap");
const adminLoansTableBody = document.querySelector("#adminLoansTableBody");
const statusFilter = document.querySelector("#statusFilter");

const formatMoney = (amount) => new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2
}).format(amount);

function addAdminCell(row, text) {
    const cell = document.createElement("td");
    cell.textContent = text;
    row.append(cell);
    return cell;
}

function renderAdminLoan(loan) {
    const row = document.createElement("tr");
    addAdminCell(row, `${loan.borrowerName} · ${loan.borrowerEmail}`);
    addAdminCell(row, formatMoney(loan.principal));
    addAdminCell(row, `${loan.tenureMonths} months`);
    addAdminCell(row, formatMoney(loan.emi));

    const statusCell = document.createElement("td");
    const status = document.createElement("span");
    status.className = `status-chip status-${String(loan.status).toLowerCase()}`;
    status.textContent = loan.status;
    statusCell.append(status);
    row.append(statusCell);

    const actionCell = document.createElement("td");
    if (loan.status === "APPLIED") {
        const link = document.createElement("a");
        link.href = `/admin/loans/${encodeURIComponent(loan.loanId)}`;
        link.textContent = "Review";
        actionCell.append(link);
    }
    row.append(actionCell);
    adminLoansTableBody.append(row);
}

async function loadAdminLoans() {
    if (!UdhariApi.requireRole("ADMIN")) {
        return;
    }

    adminLoansLoading.hidden = false;
    adminLoansError.hidden = true;
    adminLoansTableBody.replaceChildren();
    const query = statusFilter.value ? `?status=${encodeURIComponent(statusFilter.value)}` : "";
    try {
        const body = await UdhariApi.request(`/api/admin/loans${query}`);

        if (!Array.isArray(body) || body.length === 0) {
            adminLoansEmpty.hidden = false;
            adminLoansTableWrap.hidden = true;
            return;
        }
        adminLoansEmpty.hidden = true;
        body.forEach(renderAdminLoan);
        adminLoansTableWrap.hidden = false;
    } catch (error) {
        if (UdhariApi.handleUnauthorized(error)) {
            return;
        }
        adminLoansError.textContent = error.message || "A connection error occurred. Please try again.";
        adminLoansError.hidden = false;
    } finally {
        adminLoansLoading.hidden = true;
    }
}

statusFilter.addEventListener("change", loadAdminLoans);
loadAdminLoans();
