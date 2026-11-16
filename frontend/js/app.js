document.addEventListener('DOMContentLoaded', () => {
  actualizarInterfazSesion();
  cargarLibros();
  if (localStorage.getItem('token_libreria_luz')) cargarPedidos();

  document.getElementById('form-login').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      const res = await apiService.login({
        email: document.getElementById('login-email').value,
        password: document.getElementById('login-password').value
      });
      localStorage.setItem('token_libreria_luz', res.token);
      localStorage.setItem('usuario_libreria_luz', JSON.stringify(res.usuario));
      M.toast({ html: `Bienvenido, ${res.usuario.nombre}`, classes: 'green darken-1' });
      e.target.reset();
      actualizarInterfazSesion();
      cargarPedidos();
    } catch (err) {
      M.toast({ html: err.message, classes: 'red darken-1' });
    }
  });

  document.getElementById('form-register').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      await apiService.registrar({
        nombre: document.getElementById('reg-nombre').value,
        email: document.getElementById('reg-email').value,
        password: document.getElementById('reg-password').value,
        rol: document.getElementById('reg-rol').value
      });
      M.toast({ html: 'Usuario registrado. Ahora puede iniciar sesión.', classes: 'teal darken-1' });
      e.target.reset();
    } catch (err) {
      M.toast({ html: err.message, classes: 'red darken-1' });
    }
  });

  document.getElementById('btn-logout').addEventListener('click', () => {
    localStorage.removeItem('token_libreria_luz');
    localStorage.removeItem('usuario_libreria_luz');
    actualizarInterfazSesion();
    document.getElementById('tabla-pedidos').innerHTML = '';
    M.toast({ html: 'Sesión cerrada correctamente' });
  });

  document.getElementById('form-libro').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('libro-id').value;
    const libroData = {
      isbn: document.getElementById('libro-isbn').value,
      titulo: document.getElementById('libro-titulo').value,
      autor: document.getElementById('libro-autor').value,
      precio: Number(document.getElementById('libro-precio').value),
      stock: Number(document.getElementById('libro-stock').value)
    };

    try {
      if (id) {
        await apiService.actualizarLibro(id, libroData);
        M.toast({ html: 'Libro actualizado correctamente', classes: 'blue darken-1' });
      } else {
        await apiService.crearLibro(libroData);
        M.toast({ html: 'Libro guardado satisfactoriamente', classes: 'green darken-1' });
      }
      limpiarFormularioLibro();
      cargarLibros();
    } catch (err) {
      M.toast({ html: err.message, classes: 'red darken-1' });
    }
  });

  document.getElementById('btn-limpiar-libro').addEventListener('click', limpiarFormularioLibro);
});

function actualizarInterfazSesion() {
  const usuario = JSON.parse(localStorage.getItem('usuario_libreria_luz'));
  const navInfo = document.getElementById('nav-user-info');
  const navLogout = document.getElementById('nav-btn-logout');

  if (usuario) {
    document.getElementById('lbl-usuario').textContent = `${usuario.nombre} (${usuario.rol})`;
    navInfo.classList.remove('hide');
    navLogout.classList.remove('hide');
  } else {
    navInfo.classList.add('hide');
    navLogout.classList.add('hide');
  }
}

async function cargarLibros() {
  try {
    const libros = await apiService.obtenerLibros();
    const tbody = document.getElementById('tabla-libros');
    tbody.innerHTML = libros.map(l => `
      <tr>
        <td>${l.isbn}</td>
        <td>${l.titulo}</td>
        <td>${l.autor}</td>
        <td>$${l.precio.toLocaleString('es-CO')}</td>
        <td>${l.stock}</td>
        <td>
          <button class="btn-small amber darken-2" onclick='prepararEdicion(${JSON.stringify(l)})' title="Editar">
            <i class="material-icons">edit</i>
          </button>
          <button class="btn-small red darken-2" onclick="borrarLibro('${l._id}')" title="Eliminar">
            <i class="material-icons">delete</i>
          </button>
          <button class="btn-small green darken-2" onclick="comprarLibro('${l._id}')" title="Comprar 1 unidad">
            <i class="material-icons">shopping_cart</i>
          </button>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    console.error(err);
  }
}

function prepararEdicion(libro) {
  document.getElementById('libro-id').value = libro._id;
  document.getElementById('libro-isbn').value = libro.isbn;
  document.getElementById('libro-titulo').value = libro.titulo;
  document.getElementById('libro-autor').value = libro.autor;
  document.getElementById('libro-precio').value = libro.precio;
  document.getElementById('libro-stock').value = libro.stock;
  document.getElementById('titulo-form-libro').textContent = 'Editar Libro';
}

function limpiarFormularioLibro() {
  document.getElementById('form-libro').reset();
  document.getElementById('libro-id').value = '';
  document.getElementById('titulo-form-libro').textContent = 'Registrar Libro';
}

async function borrarLibro(id) {
  if (!confirm('¿Desea eliminar este libro del catálogo?')) return;
  try {
    await apiService.eliminarLibro(id);
    M.toast({ html: 'Libro eliminado', classes: 'orange darken-2' });
    cargarLibros();
  } catch (err) {
    M.toast({ html: err.message, classes: 'red darken-1' });
  }
}

async function comprarLibro(libroId) {
  try {
    await apiService.crearPedido([{ libroId, cantidad: 1 }]);
    M.toast({ html: 'Pedido registrado con éxito', classes: 'green darken-1' });
    cargarLibros();
    cargarPedidos();
  } catch (err) {
    M.toast({ html: err.message, classes: 'red darken-1' });
  }
}

async function cargarPedidos() {
  try {
    const pedidos = await apiService.obtenerPedidos();
    const tbody = document.getElementById('tabla-pedidos');
    tbody.innerHTML = pedidos.map(p => `
      <tr>
        <td>${p._id.slice(-6).toUpperCase()}</td>
        <td>${p.usuario ? p.usuario.nombre : 'N/A'}</td>
        <td>${p.items.map(i => `${i.titulo} (x${i.cantidad})`).join(', ')}</td>
        <td>$${p.total.toLocaleString('es-CO')}</td>
        <td><span class="new badge green" data-badge-caption="">${p.estado}</span></td>
      </tr>
    `).join('');
  } catch (err) {
    console.error(err);
  }
}