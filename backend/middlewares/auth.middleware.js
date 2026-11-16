const jwt = require('jsonwebtoken');

const verificarToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ mensaje: 'Acceso denegado. Token no proporcionado.' });
  }

  const token = authHeader.split(' ')[1];
  try {
    const decodificado = jwt.verify(token, process.env.JWT_SECRET || 'LibreriaLuz_SENA_ADSO_3235898_SecretKey');
    req.usuario = decodificado;
    next();
  } catch (error) {
    return res.status(403).json({ mensaje: 'Token inválido o expirado.' });
  }
};

const esAdmin = (req, res, next) => {
  if (!req.usuario || req.usuario.rol !== 'admin') {
    return res.status(403).json({ mensaje: 'Acceso restringido: se requieren permisos de administrador.' });
  }
  next();
};

module.exports = { verificarToken, esAdmin };