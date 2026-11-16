const mongoose = require('mongoose');
const { Schema } = mongoose;

const UsuarioSchema = new Schema({
  nombre: { type: String, required: true, trim: true },
  email: { type: String, required: true, unique: true, lowercase: true, trim: true },
  password: { type: String, required: true, minlength: 6 },
  rol: { type: String, enum: ['admin', 'cliente'], default: 'cliente' }
}, { timestamps: true });

module.exports = mongoose.model('Usuario', UsuarioSchema);