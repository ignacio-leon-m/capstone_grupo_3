// Utilidades comunes para logout y back con soporte Bootstrap.
// Ajuste: incluir .logout-button en los selectores de logout
(function attachGlobalHandlers() {
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', bindHandlers);
  } else {
    bindHandlers();
  }
  function logout() {
    try { localStorage.removeItem('jwtToken'); localStorage.removeItem('userRole'); } catch(e) {}
    window.location.href = '/index.html';
  }
  function bindHandlers() {
    document.querySelectorAll('#logout-button, .logout-icon-button, .logout-button').forEach(el => {
      if (el.dataset.bound === '1') return; el.dataset.bound='1';
      el.addEventListener('click', e => { e.preventDefault(); logout(); });
    });
    document.querySelectorAll('a.back-button').forEach(el => {
      const href = el.getAttribute('href');
      if (!href || href === '#') {
        if (el.dataset.bound === '1') return; el.dataset.bound='1';
        el.addEventListener('click', e => { e.preventDefault(); if (history.length>1) history.back(); });
      }
    });
    window.logout = logout;
  }
})();

