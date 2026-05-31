# Banking System - Frontend

This is the frontend for the Banking System project.

## How to Run

Simply open `frontend/index.html` in any modern browser.

No server or build step needed — it's a standalone HTML file.

---

## Features

### Authentication
- **Login** — Authenticate with Name + Customer ID
- **Register** — Create a new account instantly

### Dashboard
- **Overview** — Balance summary, stats (Total Deposited, Withdrawn, Transaction count), Recent Activity
- **Transactions** — Deposit and Withdraw money with confirmation modal; full transaction history
- **Loan Section** — CIBIL score eligibility check (≥ 650), 9 loan types with interest rates, apply flow
- **Profile** — Account details view

### How it Connects to Backend
The frontend currently simulates the backend logic locally using `localStorage`.
To connect to the real Java MySQL backend, replace the `handleLogin()`, `handleRegister()`,
`handleDeposit()`, and `handleWithdraw()` functions in `frontend/index.html`
with `fetch()` calls to your Java REST API endpoints.

Example:
```js
// Replace mock login with real API call
async function handleLogin() {
  const res = await fetch('http://localhost:8080/api/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, customerId })
  });
  const data = await res.json();
  if (data.success) launchDashboard();
}
