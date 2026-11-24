const loginForm = document.querySelector("#loginForm");
const loginButton = document.querySelector("#loginButton");
const loginError = document.querySelector("#loginError");

loginForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
    loginError.hidden = true;

    if (!loginForm.reportValidity()) {
        return;
    }

    const formData = new FormData(loginForm);
    await UdhariApi.withBusy(loginButton, "Signing in…", "Sign in", async () => {
        try {
            const body = await UdhariApi.request("/api/auth/login", {
                method: "POST",
                body: {
                email: formData.get("email"),
                password: formData.get("password")
                }
            });
            UdhariApi.saveAuth(body);
            window.location.assign(body.role === "ADMIN" ? "/admin/loans" : "/dashboard");
        } catch (error) {
            loginError.textContent = error.message || "We could not sign you in. Check your details and try again.";
            loginError.hidden = false;
        }
    });
});
