require('dotenv').config();
const express = require('express');
const morgan = require('morgan');
const cors = require('cors');
const { conectarDB } = require('./config/database');
const errorHandler = require('./middlewares/errorHandler.middleware');

const app = express();

if (process.env.NODE_ENV !== 'test') {
  const mongoUri = process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/libreria_luz';
  conectarDB(mongoUri);
}

app.set('port', process.env.PORT || 3000);
app.use(morgan('dev'));
app.use(express.json());
app.use(cors({
  origin: ['http://localhost:4200', 'http://127.0.0.1:5500', 'http://localhost:5500'],
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

app.get('/api/status', (req, res) => {
  res.json({
    proyecto: 'Librería Luz API REST',
    ficha: 'ADSO 3235898',
    estado: 'Operativo'
  });
});

app.use('/api/auth', require('./routes/auth.routes'));
app.use('/api/libros', require('./routes/libro.routes'));
app.use('/api/pedidos', require('./routes/pedido.routes'));

app.use(errorHandler);

if (process.env.NODE_ENV !== 'test') {
  app.listen(app.get('port'), () => {
    console.log(`[SERVER] Servidor Librería Luz activo en http://localhost:${app.get('port')}`);
  });
}

module.exports = app;