// UNIVERSAL WRAPPER FETCH
export async function fetchAPI(url, options = {}) {

    const token = localStorage.getItem("jwtToken");
    // IF FORMDATA, THE BROWSER SETS THE 'Content-Type...'
    // IF NOT, ASSUME JSON
    const isFormData = options.body instanceof FormData;

    const headers = {
          ...(!isFormData && { "Content-Type": "application/json" }),
          ...(options.headers || {}),
          ...(token ? { "Authorization": `Bearer ${token}` } : {})
      };

    const response = await fetch(url, { ...options, headers });

    // 401 EXPIRED TOKEN - 403 INVALID TOKEN
    if (response.status === 401 || response.status === 403) {
        logout();
        throw new Error("Sesión expirada o inválida");
    }

     if (!response.ok) {
          const errorText = await response.text();
          throw new Error(errorText || `Error HTTP: ${response.status}`);
    }

    const contentType = response.headers.get("content-type") || "";
    return contentType.includes("application/json") ? response.json() : response.text();
}


// AUTH AND ROLE HANDLER
export async function initAuthGuards() {
    const path = window.location.pathname;

    //WHITE LIST
    const publicPaths = ["/index.html"];

    // IF IT'S A PUBLIC  ROUTE, RETURN
    if (publicPaths.some(p => path.endsWith(p))) return;

    try {
        // REFRESH CURRENT USER AT RELOAD
        const user = await fetchAPI("/api/users/me");

        // USER DATA
        localStorage.setItem("userRole", user.role);
        localStorage.setItem("userId", user.id);
        localStorage.setItem("userEmail", user.email);

        console.log(`Sesión validada: ${user.email} [${user.role}]`);

        // ROLE ACCESS CONTROL
        checkRoleAccess(path, user.role);

        } catch (error) {
            console.warn("Acceso denegado o sesión inválida:", error);
            logout();
        }
    // LOGOUT BUTTON LISTENER
    document.querySelector(".logout-button")?.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
    });
  });

// AUXILIAR FUNCTION TO VALIDATE AUTH BY URL
function checkRoleAccess(path, role) {
    const adminOnly = ["/create-professor.html"];
    const professorOnly = ["/user-upload.html", "/professor-subject.html"];

    if (adminOnly.some(p => path.endsWith(p)) && currentRole !== "admin") {
        alert("Acceso exclusivo para Administradores.");
        window.location.href = "/home.html";
    }

    if (professorOnly.some(p => path.endsWith(p)) && currentRole !== "profesor") {
        alert("Acceso exclusivo para Profesores.");
        window.location.href = "/home.html";
    }
}

function logout() {
    localStorage.clear();
    // Evitar loop infinito si ya estamos en index
    if (!window.location.pathname.endsWith("/index.html")) {
        window.location.href = "/index.html";
    }
}

}

initAuthGuards();
