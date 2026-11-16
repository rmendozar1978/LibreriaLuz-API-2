const mongoose = require('mongoose');
const { Schema } = mongoose;

const LibroSchema = new Schema({
  isbn: { type: String, required: true, unique: true, trim: true },
  titulo: { type: String, required: true, trim: true },
  autor: { type: String, required: true, trim: true },
  categoria: { type: String, default: 'General', trim: true },
  precio: { type: Number, required: true, min: 0 },
  stock: { type: Number, required: true, min: 0, default: 0 }
}, { timestamps: true });

module.exports = mongoose.model('Libro', LibroSchema);