(() => {
    const AUTH_KEY = "udhariAuth";
    const busyButtons = new WeakSet();

    class ApiError extends Error {
        constructor(message, status, fieldErrors = {}) {
            super(message);
            this.name = "ApiError";
            this.status = status;
            this.fieldErrors = fieldErrors;
        }
    }

    function getAuth() {
        try {
            return JSON.parse(sessionStorage.getItem(AUTH_KEY) || "null");
        } catch {
            sessionStorage.removeItem(AUTH_KEY);
            return null;
        }
    }

    function saveAuth(auth) {
        sessionStorage.setItem(AUTH_KEY, JSON.stringify(auth));
    }

    function clearAuth() {
        sessionStorage.removeItem(AUTH_KEY);
    }

    function requireRole(role) {
        const auth = getAuth();
        if (!auth?.token || auth.role !== role) {
            window.location.replace("/login");
            return null;
        }
        return auth;
    }

    function handleUnauthorized(error) {
        if (error?.status === 401 || error?.status === 403) {
            clearAuth();
            window.location.replace("/login");
            return true;
        }
        return false;
    }

    function updateNavigation() {
        const auth = getAuth();
        document.querySelectorAll("[data-nav-role]").forEach((link) => {
            link.hidden = !auth?.token || link.dataset.navRole !== auth.role;
        });
        const logoutButton = document.querySelector("#logoutButton");
        if (logoutButton) {
            logoutButton.hidden = !auth?.token;
        }
        const brandLink = document.querySelector("[data-brand-link]");
        if (brandLink) {
            brandLink.href = auth?.role === "ADMIN" ? "/admin/loans" : auth?.role === "BORROWER" ? "/dashboard" : "/";
        }
    }

    async function request(path, options = {}) {
        const url = new URL(path, window.location.origin);
        if (url.origin !== window.location.origin) {
            throw new Error("API requests must use this site.");
        }

        const headers = new Headers(options.headers || {});
        headers.set("Accept", "application/json");
        const auth = getAuth();
        if (auth?.token && !headers.has("Authorization")) {
            headers.set("Authorization", `Bearer ${auth.token}`);
        }

        let body = options.body;
        if (body !== undefined && typeof body !== "string" && !(body instanceof FormData)) {
            headers.set("Content-Type", "application/json");
            body = JSON.stringify(body);
        }

        const response = await fetch(url.pathname + url.search, {
            ...options,
            headers,
            body
        });
        let data = null;
        if (response.status !== 204) {
            const contentType = response.headers.get("Content-Type") || "";
            if (contentType.includes("application/json")) {
                data = await response.json();
            } else {
                const text = await response.text();
                data = text ? { message: text } : null;
            }
        }
        if (!response.ok) {
            throw new ApiError(
                data?.message || `Request failed (${response.status}).`,
                response.status,
                data?.fieldErrors || {}
            );
        }
        return data;
    }

    async function withBusy(button, busyText, idleText, action) {
        if (!button || busyButtons.has(button)) {
            return;
        }
        busyButtons.add(button);
        button.disabled = true;
        button.textContent = busyText;
        try {
            return await action();
        } finally {
            button.disabled = false;
            button.textContent = idleText;
            busyButtons.delete(button);
        }
    }

    document.addEventListener("click", (event) => {
        if (event.target instanceof Element && event.target.closest("#logoutButton")) {
            clearAuth();
            window.location.replace("/login");
        }
    });

    window.UdhariApi = { ApiError, request, getAuth, saveAuth, clearAuth, requireRole, handleUnauthorized, withBusy };
    updateNavigation();
})();
