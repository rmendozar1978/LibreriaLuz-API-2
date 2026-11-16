const Libro = require('../models/Libro');
const libroCtrl = {};

libroCtrl.getLibros = async (req, res, next) => {
  try {
    const libros = await Libro.find().sort({ createdAt: -1 });
    res.json(libros);
  } catch (error) {
    next(error);
  }
};

libroCtrl.getLibroById = async (req, res, next) => {
  try {
    const libro = await Libro.findById(req.params.id);
    if (!libro) return res.status(404).json({ mensaje: 'Libro no encontrado' });
    res.json(libro);
  } catch (error) {
    next(error);
  }
};

libroCtrl.createLibro = async (req, res, next) => {
  try {
    const libro = new Libro(req.body);
    await libro.save();
    res.status(201).json({ status: 'Libro guardado correctamente', libro });
  } catch (error) {
    next(error);
  }
};

libroCtrl.updateLibro = async (req, res, next) => {
  try {
    const libroActualizado = await Libro.findByIdAndUpdate(
      req.params.id,
      { $set: req.body },
      { new: true, runValidators: true }
    );
    if (!libroActualizado) return res.status(404).json({ mensaje: 'Libro no encontrado' });
    res.json({ status: 'Libro actualizado correctamente', libro: libroActualizado });
  } catch (error) {
    next(error);
  }
};

libroCtrl.deleteLibro = async (req, res, next) => {
  try {
    const libroEliminado = await Libro.findByIdAndDelete(req.params.id);
    if (!libroEliminado) return res.status(404).json({ mensaje: 'Libro no encontrado' });
    res.json({ status: 'Libro eliminado correctamente' });
  } catch (error) {
    next(error);
  }
};

module.exports = libroCtrl;