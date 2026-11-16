const express = require('express');
const router = express.Router();
const pedidoCtrl = require('../controllers/pedido.controller');
const { verificarToken } = require('../middlewares/auth.middleware');

router.get('/', verificarToken, pedidoCtrl.getPedidos);
router.post('/', verificarToken, pedidoCtrl.createPedido);

module.exports = router;