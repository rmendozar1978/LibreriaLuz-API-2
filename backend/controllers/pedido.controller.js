const Pedido = require('../models/Pedido');
const Libro = require('../models/Libro');
const pedidoCtrl = {};

pedidoCtrl.calcularTotalPedido = (items) => {
  return items.reduce((acum, item) => acum + (item.precioUnitario * item.cantidad), 0);
};

pedidoCtrl.createPedido = async (req, res, next) => {
  try {
    const { items } = req.body;
    if (!items || !Array.isArray(items) || items.length === 0) {
      return res.status(400).json({ mensaje: 'Debe incluir al menos un libro en el pedido.' });
    }

    const itemsProcesados = [];
    for (const item of items) {
      const libroDB = await Libro.findById(item.libroId);
      if (!libroDB) {
        return res.status(404).json({ mensaje: `El libro con ID ${item.libroId} no existe.` });
      }
      if (libroDB.stock < item.cantidad) {
        return res.status(400).json({ mensaje: `Stock insuficiente para '${libroDB.titulo}'. Disponible: ${libroDB.stock}` });
      }

      const subtotal = libroDB.precio * item.cantidad;
      itemsProcesados.push({
        libro: libroDB._id,
        titulo: libroDB.titulo,
        cantidad: item.cantidad,
        precioUnitario: libroDB.precio,
        subtotal
      });

      libroDB.stock -= item.cantidad;
      await libroDB.save();
    }

    const total = pedidoCtrl.calcularTotalPedido(itemsProcesados);
    const nuevoPedido = new Pedido({
      usuario: req.usuario.id,
      items: itemsProcesados,
      total
    });

    await nuevoPedido.save();
    res.status(201).json({ status: 'Pedido registrado con éxito', pedido: nuevoPedido });
  } catch (error) {
    next(error);
  }
};

pedidoCtrl.getPedidos = async (req, res, next) => {
  try {
    const filtro = req.usuario.rol === 'admin' ? {} : { usuario: req.usuario.id };
    const pedidos = await Pedido.find(filtro)
      .populate('usuario', 'nombre email')
      .sort({ createdAt: -1 });
    res.json(pedidos);
  } catch (error) {
    next(error);
  }
};

module.exports = pedidoCtrl;