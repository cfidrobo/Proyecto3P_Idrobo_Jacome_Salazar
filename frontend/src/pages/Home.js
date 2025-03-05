import React from 'react';
import { Link } from 'react-router-dom';
import {
  Box,
  Grid,
  Paper,
  Typography,
  Button,
  Container
} from '@mui/material';

function Home() {
  const modules = [
    {
      title: "Candidatos",
      description: "Gestiona los candidatos y sus perfiles",
      path: "/candidatos",
      buttonText: "IR A CANDIDATOS"
    },
    {
      title: "Entrevistas",
      description: "Administra las entrevistas y sus resultados",
      path: "/entrevistas",
      buttonText: "IR A ENTREVISTAS"
    },
    {
      title: "Vacantes",
      description: "Controla las vacantes y sus requisitos",
      path: "/vacantes",
      buttonText: "IR A VACANTES"
    },
    {
      title: "Evaluaciones",
      description: "Gestiona las evaluaciones y reportes de candidatos",
      path: "/evaluaciones",
      buttonText: "IR A EVALUACIONES"
    },
    {
      title: "Seleccion",
      description: "Gestiona el proceso de selección de candidatos",
      path: "/seleccion",
      buttonText: "IR A SELECCIÓN"
    }
  ];

  return (
    <Container maxWidth="lg">
      <Box sx={{ py: 4 }}>
        <Typography variant="h3" align="center" gutterBottom>
          Bienvenido al Sistema de Reclutamiento
        </Typography>
        <Typography variant="h6" align="center" color="textSecondary" gutterBottom>
          Selecciona una opción para comenzar
        </Typography>

        <Grid container spacing={3} sx={{ mt: 3 }}>
          {modules.map((module, index) => (
            <Grid item xs={12} md={6} key={index}>
              <Paper sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column' }}>
                <Typography variant="h5" gutterBottom>
                  {module.title}
                </Typography>
                <Typography color="textSecondary" sx={{ mb: 2 }}>
                  {module.description}
                </Typography>
                <Box sx={{ mt: 'auto' }}>
                  <Button
                    component={Link}  
                    to={module.path}
                    color="primary"
                    variant="contained"
                    fullWidth
                  >
                    {module.buttonText}
                  </Button>
                </Box>
              </Paper>
            </Grid>
          ))}
        </Grid>
      </Box>
    </Container>
  );
}

export default Home; 