const registerForm = document.querySelector("#registerForm");
const registerButton = document.querySelector("#registerButton");
const registerError = document.querySelector("#registerError");
const registerSuccess = document.querySelector("#registerSuccess");

function clearRegistrationErrors() {
    registerError.hidden = true;
    document.querySelectorAll(".field-error").forEach((element) => {
        element.textContent = "";
        document.querySelector(`#${element.dataset.errorFor}`)?.removeAttribute("aria-invalid");
    });
}

registerForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearRegistrationErrors();
    registerSuccess.hidden = true;

    if (!registerForm.reportValidity()) {
        return;
    }

    const formData = new FormData(registerForm);
    await UdhariApi.withBusy(registerButton, "Creating account…", "Create account", async () => {
        try {
            await UdhariApi.request("/api/auth/register", {
                method: "POST",
                body: {
                fullName: formData.get("fullName"),
                email: formData.get("email"),
                password: formData.get("password"),
                monthlyIncome: Number(formData.get("monthlyIncome")),
                employmentType: formData.get("employmentType")
                }
            });
            registerForm.hidden = true;
            registerSuccess.innerHTML = 'Your account is ready. <a href="/login">Sign in</a> to continue.';
            registerSuccess.hidden = false;
        } catch (error) {
            Object.entries(error.fieldErrors || {}).forEach(([field, message]) => {
                const target = document.querySelector(`[data-error-for="${field}"]`);
                if (target) {
                    target.textContent = message;
                    document.querySelector(`#${field}`)?.setAttribute("aria-invalid", "true");
                }
            });
            registerError.textContent = error.message || "Please correct the highlighted fields and try again.";
            registerError.hidden = false;
        }
    });
});
