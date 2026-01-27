/*******************************
 * Backend API Base URL
 *******************************/
const API_BASE = "http://localhost:5000";

/*******************************
 * DOM Elements
 *******************************/
const navLinks = document.querySelectorAll(".nav-link");
const pages = document.querySelectorAll(".page");

const totalUsersEl = document.getElementById("totalUsers");
const totalAccountsEl = document.getElementById("totalAccounts");
const totalTransactionsEl = document.getElementById("totalTransactions");
const totalBalanceEl = document.getElementById("totalBalance");
const txnTableRecent = document.getElementById("txnTableRecent");

const usersTable = document.getElementById("usersTable");
const accountsTable = document.getElementById("accountsTable");
const txnTableAll = document.getElementById("txnTableAll");
const auditTable = document.getElementById("auditTable");

const depositBtn = document.getElementById("depositBtn");
const withdrawBtn = document.getElementById("withdrawBtn");
const transferBtn = document.getElementById("transferBtn");

const refreshUsersBtn = document.getElementById("refreshUsersBtn");
const refreshAccountsBtn = document.getElementById("refreshAccountsBtn");
const refreshTransactionsBtn = document.getElementById("refreshTransactionsBtn");
const refreshAuditBtn = document.getElementById("refreshAuditBtn");

const createCustomerBtn = document.getElementById("createCustomerBtn");
const createCustomerMsg = document.getElementById("createCustomerMsg");
const resequenceAccountsBtn = document.getElementById("resequenceAccountsBtn");

/*******************************
 * Load Dashboard Stats
 *******************************/
async function loadStats() {
    try {
        const res = await fetch(`${API_BASE}/stats`);
        const data = await res.json();

        totalUsersEl.textContent = data.totalUsers;
        totalAccountsEl.textContent = data.totalAccounts;
        totalTransactionsEl.textContent = data.totalTransactions;
        totalBalanceEl.textContent = "₹ " + Number(data.totalBalance).toLocaleString();
    } catch (err) {
        console.error("Stats load failed:", err);
        alert("❌ Backend not running / stats API error");
    }
}

/*******************************
 * Navigation (simple SPA)
 *******************************/
function setActivePage(pageName) {
    pages.forEach((p) => p.classList.remove("active"));
    navLinks.forEach((a) => a.classList.remove("active"));

    const pageEl = document.getElementById(`page-${pageName}`);
    if (pageEl) pageEl.classList.add("active");

    const activeLink = document.querySelector(`.nav-link[data-page="${pageName}"]`);
    if (activeLink) activeLink.classList.add("active");
}

function wireNavigation() {
    navLinks.forEach((link) => {
        link.addEventListener("click", async (e) => {
            e.preventDefault();
            const page = link.dataset.page;
            setActivePage(page);

            // Lazy-load data for each page
            if (page === "dashboard") {
                await loadStats();
                await loadTransactionsRecent();
            } else if (page === "users") {
                await loadUsers();
            } else if (page === "accounts") {
                await loadAccounts();
            } else if (page === "transactions") {
                await loadTransactionsAll();
            } else if (page === "audit") {
                await loadAuditLogs();
            }
        });
    });
}

/*******************************
 * Load Tables
 *******************************/
function renderEmptyRow(tbodyEl, message, colSpan) {
    tbodyEl.innerHTML = "";
    const row = document.createElement("tr");
    row.innerHTML = `<td colspan="${colSpan}">${message}</td>`;
    tbodyEl.appendChild(row);
}

async function loadTransactionsRecent() {
    try {
        const res = await fetch(`${API_BASE}/transactions?limit=10`);
        const txns = await res.json();

        txnTableRecent.innerHTML = "";

        if (!Array.isArray(txns) || txns.length === 0) {
            renderEmptyRow(txnTableRecent, "No transactions found.", 7);
            return;
        }

        txns.forEach((t) => {
            const row = document.createElement("tr");

            row.innerHTML = `
        <td>${t.transaction_id}</td>
        <td>${t.from_account ?? "-"}</td>
        <td>${t.to_account ?? "-"}</td>
        <td>${t.transaction_type}</td>
        <td>${t.amount}</td>
        <td>${t.status}</td>
        <td>${new Date(t.created_at).toLocaleString()}</td>
      `;

            txnTableRecent.appendChild(row);
        });
    } catch (err) {
        console.error("Transactions load failed:", err);
        alert("❌ Backend not running / transactions API error");
    }
}

async function loadTransactionsAll() {
    try {
        const res = await fetch(`${API_BASE}/transactions?limit=100`);
        const txns = await res.json();

        txnTableAll.innerHTML = "";
        if (!Array.isArray(txns) || txns.length === 0) {
            renderEmptyRow(txnTableAll, "No transactions found.", 7);
            return;
        }

        txns.forEach((t) => {
            const row = document.createElement("tr");
            row.innerHTML = `
        <td>${t.transaction_id}</td>
        <td>${t.from_account ?? "-"}</td>
        <td>${t.to_account ?? "-"}</td>
        <td>${t.transaction_type}</td>
        <td>${t.amount}</td>
        <td>${t.status}</td>
        <td>${new Date(t.created_at).toLocaleString()}</td>
      `;
            txnTableAll.appendChild(row);
        });
    } catch (err) {
        console.error("Transactions(all) load failed:", err);
        alert("❌ Backend not running / transactions API error");
    }
}

async function loadUsers() {
    try {
        const res = await fetch(`${API_BASE}/users`);
        const users = await res.json();

        usersTable.innerHTML = "";
        if (!Array.isArray(users) || users.length === 0) {
            renderEmptyRow(usersTable, "No users found.", 5);
            return;
        }

        users.forEach((u) => {
            const row = document.createElement("tr");
            row.innerHTML = `
        <td>${u.user_id}</td>
        <td>${u.name}</td>
        <td>${u.email}</td>
        <td>${u.role}</td>
        <td>${u.created_at ? new Date(u.created_at).toLocaleString() : "-"}</td>
      `;
            usersTable.appendChild(row);
        });
    } catch (err) {
        console.error("Users load failed:", err);
        alert("❌ Backend not running / users API error");
    }
}

async function loadAccounts() {
    try {
        const res = await fetch(`${API_BASE}/accounts`);
        const accounts = await res.json();

        accountsTable.innerHTML = "";
        if (!Array.isArray(accounts) || accounts.length === 0) {
            renderEmptyRow(accountsTable, "No accounts found.", 5);
            return;
        }

        accounts.forEach((a) => {
            const row = document.createElement("tr");
            row.innerHTML = `
        <td>${a.account_id ?? "-"}</td>
        <td>${a.account_number ?? "-"}</td>
        <td>${a.balance ?? "-"}</td>
        <td>${a.status ?? "-"}</td>
        <td>${a.created_at ? new Date(a.created_at).toLocaleString() : "-"}</td>
      `;
            accountsTable.appendChild(row);
        });
    } catch (err) {
        console.error("Accounts load failed:", err);
        alert("❌ Backend not running / accounts API error");
    }
}

async function loadAuditLogs() {
    try {
        const res = await fetch(`${API_BASE}/audit?limit=100`);
        const logs = await res.json();

        auditTable.innerHTML = "";
        if (!Array.isArray(logs) || logs.length === 0) {
            renderEmptyRow(auditTable, "No audit logs found.", 5);
            return;
        }

        logs.forEach((l) => {
            const row = document.createElement("tr");
            row.innerHTML = `
        <td>${l.audit_id ?? "-"}</td>
        <td>${l.user_id ?? "-"}</td>
        <td>${l.action ?? "-"}</td>
        <td>${l.ip_address ?? "-"}</td>
        <td>${l.created_at ? new Date(l.created_at).toLocaleString() : "-"}</td>
      `;
            auditTable.appendChild(row);
        });
    } catch (err) {
        console.error("Audit load failed:", err);
        alert("❌ Backend not running / audit API error");
    }
}

/*******************************
 * Deposit API Call
 *******************************/
depositBtn.addEventListener("click", async () => {
    const account_number = document.getElementById("depositAcc").value.trim();
    const amount = parseFloat(document.getElementById("depositAmt").value);

    if (!account_number || isNaN(amount) || amount <= 0) {
        alert("❌ Enter valid deposit details");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/accounts/deposit`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ account_number, amount }),
        });

        const data = await res.json();
        alert(data.message || "✅ Deposit done");

        // clear inputs
        document.getElementById("depositAcc").value = "";
        document.getElementById("depositAmt").value = "";

        // refresh dashboard + table
        await loadStats();
        await loadTransactionsRecent();
    } catch (err) {
        console.error("Deposit failed:", err);
        alert("❌ Deposit failed (backend error)");
    }
});

/*******************************
 * Withdraw API Call
 *******************************/
withdrawBtn.addEventListener("click", async () => {
    const account_number = document.getElementById("withdrawAcc").value.trim();
    const amount = parseFloat(document.getElementById("withdrawAmt").value);

    if (!account_number || isNaN(amount) || amount <= 0) {
        alert("❌ Enter valid withdrawal details");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/accounts/withdraw`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ account_number, amount }),
        });

        const data = await res.json();
        alert(data.message || "✅ Withdrawal done");

        // clear inputs
        document.getElementById("withdrawAcc").value = "";
        document.getElementById("withdrawAmt").value = "";

        // refresh dashboard + table
        await loadStats();
        await loadTransactionsRecent();
    } catch (err) {
        console.error("Withdraw failed:", err);
        alert("❌ Withdraw failed (backend error)");
    }
});

/*******************************
 * Transfer API Call
 *******************************/
transferBtn.addEventListener("click", async () => {
    const from_account = document.getElementById("fromAcc").value.trim();
    const to_account = document.getElementById("toAcc").value.trim();
    const amount = parseFloat(document.getElementById("transferAmt").value);

    if (!from_account || !to_account || isNaN(amount) || amount <= 0) {
        alert("❌ Enter valid transfer details");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/accounts/transfer`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ from_account, to_account, amount }),
        });

        const data = await res.json();
        alert(data.message || "✅ Transfer done");

        // clear inputs
        document.getElementById("fromAcc").value = "";
        document.getElementById("toAcc").value = "";
        document.getElementById("transferAmt").value = "";

        // refresh dashboard + table
        await loadStats();
        await loadTransactionsRecent();
    } catch (err) {
        console.error("Transfer failed:", err);
        alert("❌ Transfer failed (backend error)");
    }
});

/*******************************
 * Create Customer
 *******************************/
createCustomerBtn.addEventListener("click", async () => {
    const name = document.getElementById("customerName").value.trim();
    const email = document.getElementById("customerEmail").value.trim();
    const role = document.getElementById("customerRole").value;
    const password = document.getElementById("customerPassword").value;

    createCustomerMsg.textContent = "";

    if (!name || !email) {
        createCustomerMsg.textContent = "❌ Name and email are required.";
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/users`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email, role, password }),
        });

        const data = await res.json();
        if (!res.ok) {
            createCustomerMsg.textContent = data?.message || "❌ Failed to create customer.";
            return;
        }

        const accInfo = data?.account?.account_number
            ? ` Account Number: ${data.account.account_number} (balance ₹0).`
            : "";
        createCustomerMsg.textContent =
            data?.message || `✅ Customer created.${accInfo}`;
        document.getElementById("customerName").value = "";
        document.getElementById("customerEmail").value = "";
        document.getElementById("customerPassword").value = "";

        await loadUsers();
        await loadStats();
    } catch (err) {
        console.error("Create customer failed:", err);
        createCustomerMsg.textContent = "❌ Create customer failed (backend error).";
    }
});

/*******************************
 * Refresh Buttons
 *******************************/
refreshUsersBtn.addEventListener("click", loadUsers);
refreshAccountsBtn.addEventListener("click", loadAccounts);
refreshTransactionsBtn.addEventListener("click", loadTransactionsAll);
refreshAuditBtn.addEventListener("click", loadAuditLogs);

/*******************************
 * Resequence Accounts
 *******************************/
resequenceAccountsBtn.addEventListener("click", async () => {
    if (!confirm("This will resequence all account numbers. Continue?")) return;

    try {
        const res = await fetch(`${API_BASE}/accounts/resequence`, {
            method: "POST",
        });
        const data = await res.json();
        alert(data?.message || "Resequence finished.");
        await loadAccounts();
    } catch (err) {
        console.error("Resequence accounts failed:", err);
        alert("❌ Failed to resequence accounts (backend error).");
    }
});

/*******************************
 * Initial Load
 *******************************/
wireNavigation();
setActivePage("dashboard");
loadStats();
loadTransactionsRecent();
