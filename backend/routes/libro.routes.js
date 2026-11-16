const express = require('express');
const router = express.Router();
const libroCtrl = require('../controllers/libro.controller');
const { verificarToken, esAdmin } = require('../middlewares/auth.middleware');

router.get('/', libroCtrl.getLibros);
router.get('/:id', libroCtrl.getLibroById);
router.post('/', [verificarToken, esAdmin], libroCtrl.createLibro);
router.put('/:id', [verificarToken, esAdmin], libroCtrl.updateLibro);
router.delete('/:id', [verificarToken, esAdmin], libroCtrl.deleteLibro);

module.exports = router;