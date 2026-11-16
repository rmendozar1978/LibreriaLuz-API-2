const mongoose = require('mongoose');
const { Schema } = mongoose;

const DetalleItemSchema = new Schema({
  libro: { type: Schema.Types.ObjectId, ref: 'Libro', required: true },
  titulo: { type: String, required: true },
  cantidad: { type: Number, required: true, min: 1 },
  precioUnitario: { type: Number, required: true, min: 0 },
  subtotal: { type: Number, required: true, min: 0 }
}, { _id: false });

const PedidoSchema = new Schema({
  usuario: { type: Schema.Types.ObjectId, ref: 'Usuario', required: true },
  items: { type: [DetalleItemSchema], required: true },
  total: { type: Number, required: true, min: 0 },
  estado: { type: String, enum: ['Pendiente', 'Pagado', 'Entregado', 'Cancelado'], default: 'Pagado' }
}, { timestamps: true });

module.exports = mongoose.model('Pedido', PedidoSchema);