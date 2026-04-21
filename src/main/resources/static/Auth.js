const API = "http://localhost:8081";

const KEYS = {
  acceso: "jwt_acceso",
  refresco: "jwt_refresco",
  usuario: "jwt_usuario",
  nombre: "jwt_nombre",
  apellido: "jwt_apellido",
  telefono: "jwt_telefono"
};

function guardarSesion(data, correo) {
  localStorage.setItem(KEYS.acceso, data.acceso_token);
  localStorage.setItem(KEYS.refresco, data.refrescar_token);
  localStorage.setItem(KEYS.usuario, correo);
}

function guardarDatosUsuario(nombre, apellido, correo, telefono) {
  localStorage.setItem(KEYS.nombre, nombre || "-");
  localStorage.setItem(KEYS.apellido, apellido || "-");
  localStorage.setItem(KEYS.usuario, correo || "-");
  localStorage.setItem(KEYS.telefono, telefono || "-");
}

function obtenerAcceso() { return localStorage.getItem(KEYS.acceso); }
function obtenerRefresco() { return localStorage.getItem(KEYS.refresco); }
function obtenerUsuario() { return localStorage.getItem(KEYS.usuario) || "-"; }
function obtenerNombre() { return localStorage.getItem(KEYS.nombre) || "-"; }
function obtenerApellido() { return localStorage.getItem(KEYS.apellido) || "-"; }
function obtenerTelefono() { return localStorage.getItem(KEYS.telefono) || "-"; }

function limpiarSesion() {
  Object.values(KEYS).forEach((k) => localStorage.removeItem(k));
}

function estaLogueado() {
  return !!obtenerAcceso();
}

async function leerJsonSeguro(res) {
  const raw = await res.text();
  if (!raw) return null;

  try {
    return JSON.parse(raw);
  } catch (_) {
    return { _raw: raw };
  }
}

async function registrar(nombre, apellido, correo, contrasena, telefono) {
  const res = await fetch(`${API}/usuario/registrar`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nombre, apellido, correo, contrasena, telefono })
  });

  const data = await leerJsonSeguro(res);
  if (!res.ok) throw new Error(data?.message || "Error al registrar");
  return data;
}

async function login(correo, contrasena) {
  const res = await fetch(`${API}/usuario/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ correo, contrasena })
  });

  const data = await leerJsonSeguro(res);
  if (!res.ok) {
    throw new Error(`Credenciales invalidas (status: ${res.status})`);
  }
  if (!data?.acceso_token) {
    throw new Error("La respuesta del login no contiene un token valido");
  }

  return data;
}

async function refrescarToken() {
  const refresco = obtenerRefresco();
  if (!refresco) throw new Error("No hay token de refresco");

  const res = await fetch(`${API}/usuario/refrescar`, {
    method: "POST",
    headers: { "Authorization": `Bearer ${refresco}` }
  });

  const data = await leerJsonSeguro(res);
  if (!res.ok) throw new Error("No se pudo renovar el token");
  return data;
}

async function fetchPerfilUsuario() {
  const token = obtenerAcceso();

  const res = await fetch(`${API}/usuario/perfil`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type": "application/json"
    }
  });

  if (res.status === 401 || res.status === 403) {
    throw new Error("Token invalido o vencido. Inicia sesion de nuevo.");
  }

  const data = await leerJsonSeguro(res);
  if (!res.ok) {
    throw new Error("No se pudo cargar el perfil del usuario");
  }

  // Compatibilidad con una version vieja del backend que devolvia texto plano.
  if (data?._raw) {
    return {
      nombre: obtenerNombre(),
      apellido: obtenerApellido(),
      correo: obtenerUsuario(),
      telefono: obtenerTelefono()
    };
  }

  return data;
}

async function fetchPerfil() {
  const perfil = await fetchPerfilUsuario();
  return `Acceso autorizado al perfil de ${perfil.correo}`;
}

function mostrarAlerta(id, mensaje, tipo = "error") {
  const el = document.getElementById(id);
  if (!el) return;
  el.className = `alert alert-${tipo} show`;
  el.innerHTML = `<span>${tipo === "success" ? "OK" : "X"}</span><span>${mensaje}</span>`;
  setTimeout(() => el.classList.remove("show"), 6000);
}

function setBtnLoading(id, loading, textoNormal) {
  const btn = document.getElementById(id);
  if (!btn) return;
  btn.disabled = loading;
  btn.innerHTML = loading ? `<span class="spin"></span>Procesando...` : textoNormal;
}

async function handleLogin(e) {
  e.preventDefault();

  const correo = document.getElementById("correo").value.trim();
  const contrasena = document.getElementById("contrasena").value;

  if (!correo || !contrasena) {
    mostrarAlerta("alerta", "Completa todos los campos");
    return;
  }

  setBtnLoading("btnLogin", true, "Iniciar Sesion");

  try {
    const data = await login(correo, contrasena);
    guardarSesion(data, correo);

    try {
      const perfil = await fetchPerfilUsuario();
      guardarDatosUsuario(perfil.nombre, perfil.apellido, perfil.correo, perfil.telefono);
    } catch (_) {
      guardarDatosUsuario("-", "-", correo, "-");
    }

    mostrarAlerta("alerta", "Acceso concedido. Redirigiendo...", "success");
    setTimeout(() => window.location.href = "Dashboard.html", 900);
  } catch (err) {
    mostrarAlerta("alerta", err.message);
  } finally {
    setBtnLoading("btnLogin", false, "Iniciar Sesion");
  }
}

async function handleRegistro(e) {
  e.preventDefault();

  const nombre = document.getElementById("nombre").value.trim();
  const apellido = document.getElementById("apellido").value.trim();
  const correo = document.getElementById("correo").value.trim();
  const tel = document.getElementById("telefono").value.trim();
  const contrasena = document.getElementById("contrasena").value;
  const confirmar = document.getElementById("confirmar").value;

  if (!nombre || !apellido || !correo || !contrasena || !confirmar) {
    mostrarAlerta("alerta", "Completa todos los campos");
    return;
  }
  if (contrasena !== confirmar) {
    mostrarAlerta("alerta", "Las contrasenas no coinciden");
    return;
  }
  if (contrasena.length < 4) {
    mostrarAlerta("alerta", "La contrasena debe tener minimo 4 caracteres");
    return;
  }

  setBtnLoading("btnReg", true, "Crear cuenta");

  try {
    await registrar(nombre, apellido, correo, contrasena, tel);
    guardarDatosUsuario(nombre, apellido, correo, tel);
    mostrarAlerta("alerta", "Cuenta creada. Inicia sesion...", "success");
    setTimeout(() => window.location.href = "index.html", 1200);
  } catch (err) {
    mostrarAlerta("alerta", err.message);
  } finally {
    setBtnLoading("btnReg", false, "Crear cuenta");
  }
}

function proteger() {
  if (!estaLogueado()) window.location.href = "index.html";
}

function redirigirSiLogueado() {
  if (estaLogueado()) window.location.href = "Dashboard.html";
}

function logout() {
  limpiarSesion();
  window.location.href = "index.html";
}
