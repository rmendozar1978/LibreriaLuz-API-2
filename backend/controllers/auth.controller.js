const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const Usuario = require('../models/Usuario');

const authCtrl = {};

authCtrl.registrar = async (req, res, next) => {
  try {
    const { nombre, email, password, rol } = req.body;
    const salt = await bcrypt.genSalt(10);
    const passwordHash = await bcrypt.hash(password, salt);

    const nuevoUsuario = new Usuario({
      nombre,
      email,
      password: passwordHash,
      rol: rol || 'cliente'
    });

    await nuevoUsuario.save();
    res.status(201).json({
      status: 'Usuario registrado exitosamente',
      usuario: { id: nuevoUsuario._id, nombre: nuevoUsuario.nombre, email: nuevoUsuario.email, rol: nuevoUsuario.rol }
    });
  } catch (error) {
    next(error);
  }
};

authCtrl.login = async (req, res, next) => {
  try {
    const { email, password } = req.body;
    const usuario = await Usuario.findOne({ email });
    if (!usuario) {
      return res.status(401).json({ mensaje: 'Credenciales inválidas (usuario no encontrado).' });
    }

    const passwordValida = await bcrypt.compare(password, usuario.password);
    if (!passwordValida) {
      return res.status(401).json({ mensaje: 'Credenciales inválidas (contraseña incorrecta).' });
    }

    const payload = { id: usuario._id, nombre: usuario.nombre, rol: usuario.rol };
    const token = jwt.sign(
      payload,
      process.env.JWT_SECRET || 'LibreriaLuz_SENA_ADSO_3235898_SecretKey',
      { expiresIn: '4h' }
    );

    res.json({
      status: 'Autenticación exitosa',
      token,
      usuario: payload
    });
  } catch (error) {
    next(error);
  }
};

module.exports = authCtrl;