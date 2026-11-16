class ApiService {
  constructor() {
    this.BASE_URL = 'http://localhost:3000/api';
  }

  getHeaders(requiereAuth = false) {
    const headers = { 'Content-Type': 'application/json' };
    if (requiereAuth) {
      const token = localStorage.getItem('token_libreria_luz');
      if (token) headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
  }

  async request(endpoint, method = 'GET', body = null, requiereAuth = false) {
    const opciones = {
      method,
      headers: this.getHeaders(requiereAuth)
    };
    if (body) opciones.body = JSON.stringify(body);

    const respuesta = await fetch(`${this.BASE_URL}${endpoint}`, opciones);
    const datos = await respuesta.json();

    if (!respuesta.ok) {
      throw new Error(datos.mensaje || 'Error en la comunicación con el servidor');
    }
    return datos;
  }

  login(credenciales) { return this.request('/auth/login', 'POST', credenciales); }
  registrar(usuario) { return this.request('/auth/register', 'POST', usuario); }

  obtenerLibros() { return this.request('/libros', 'GET'); }
  crearLibro(libro) { return this.request('/libros', 'POST', libro, true); }
  actualizarLibro(id, libro) { return this.request(`/libros/${id}`, 'PUT', libro, true); }
  eliminarLibro(id) { return this.request(`/libros/${id}`, 'DELETE', null, true); }

  obtenerPedidos() { return this.request('/pedidos', 'GET', null, true); }
  crearPedido(items) { return this.request('/pedidos', 'POST', { items }, true); }
}

const apiService = new ApiService();