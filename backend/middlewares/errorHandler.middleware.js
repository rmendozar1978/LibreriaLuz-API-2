const errorHandler = (err, req, res, next) => {
  console.error('[API Error]', err.stack || err.message);

  if (err.name === 'ValidationError') {
    const mensajes = Object.values(err.errors).map(val => val.message);
    return res.status(400).json({ mensaje: 'Error de validación de datos', errores: mensajes });
  }

  if (err.code === 11000) {
    const campo = Object.keys(err.keyValue)[0];
    return res.status(409).json({ mensaje: `El valor ingresado para '${campo}' ya existe en el sistema.` });
  }

  res.status(err.statusCode || 500).json({
    mensaje: err.message || 'Error interno del servidor en Librería Luz'
  });
};

module.exports = errorHandler;