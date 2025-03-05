import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
} from '@mui/material';

const pages = [
  { name: 'INICIO', path: '/' },
  { name: 'CANDIDATOS', path: '/candidatos' },
  { name: 'ENTREVISTAS', path: '/entrevistas' },
  { name: 'VACANTES', path: '/vacantes' },
  { name: 'EVALUACIONES', path: '/evaluaciones' },
  { name: 'SELECCIONES', path: '/seleccion' }
];

function Navbar() {
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Sistema de Reclutamiento
        </Typography>
        <Box>
          <Button color="inherit" component={RouterLink} to="/">
            Inicio
          </Button>
          <Button color="inherit" component={RouterLink} to="/candidatos">
            Candidatos
          </Button>
          <Button color="inherit" component={RouterLink} to="/entrevistas">
            Entrevistas
          </Button>
          <Button color="inherit" component={RouterLink} to="/vacantes">
            Vacantes
          </Button>
          <Button color="inherit" component={RouterLink} to="/seleccion">
            Selecciones
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
}

export default Navbar; 