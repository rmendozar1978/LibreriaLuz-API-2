const mongoose = require('mongoose');

const conectarDB = async (uri) => {
  try {
    await mongoose.connect(uri);
    console.log('[DB] Conexión exitosa a MongoDB - Base de datos: libreria_luz');
  } catch (error) {
    console.error('[DB Error] Fallo al conectar con MongoDB:', error.message);
    process.exit(1);
  }
};

module.exports = { conectarDB, mongoose };