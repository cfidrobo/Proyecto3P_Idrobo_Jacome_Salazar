import React, { useState } from 'react';
import {
  Box,
  Typography,
  Grid,
  Paper,
  Button,
  LinearProgress,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Tabs,
  Tab,
  Card,
  CardContent
} from '@mui/material';
import { Assessment as AssessmentIcon } from '@mui/icons-material';
import { ApolloClient, InMemoryCache, ApolloProvider, useQuery, useMutation, gql, createHttpLink } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';

// Configuración de Apollo Client
const httpLink = createHttpLink({
  uri: 'http://localhost:8090/candidatos/graphql', 
  credentials: 'include'
});

const authLink = setContext((_, { headers }) => {
  return {
    headers: {
      ...headers,
      'Authorization': 'Basic ' + btoa('admin:admin123'), 
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }
  };
});

const client = new ApolloClient({
  link: authLink.concat(httpLink),
  cache: new InMemoryCache(),
  defaultOptions: {
    watchQuery: {
      fetchPolicy: 'no-cache',
      errorPolicy: 'all'
    },
    query: {
      fetchPolicy: 'no-cache',
      errorPolicy: 'all'
    }
  }
});

// Queries y Mutations
const GET_EVALUACIONES = gql`
  query {
    candidatos {
      id
      nombre
      puntajePsicotecnico
      puntajeTecnico
      puntajeEntrevista
      puntajeTotal
      estado
      habilidades
    }
  }
`;

const GET_ESTADISTICAS = gql`
  query {
    candidatos {
      id
      estado
      puntajeTotal
    }
  }
`;

const REGISTRAR_EVALUACION = gql`
  mutation RegistrarEvaluacionPsicotecnica($evaluacion: EvaluacionInput!) {
    registrarEvaluacionPsicotecnica(evaluacion: $evaluacion) {
      id
      nombre
      estado
      puntajePsicotecnico
      puntajeTecnico
      puntajeEntrevista
      puntajeTotal
    }
  }
`;

const REGISTRAR_EVALUACION_TECNICA = gql`
  mutation RegistrarEvaluacionTecnica($evaluacion: EvaluacionInput!) {
    registrarEvaluacionTecnica(evaluacion: $evaluacion) {
      id
      nombre
      estado
      puntajePsicotecnico
      puntajeTecnico
      puntajeEntrevista
      puntajeTotal
    }
  }
`;

const REGISTRAR_ENTREVISTA = gql`
  mutation RegistrarEntrevista($evaluacion: EvaluacionInput!) {
    registrarEntrevista(evaluacion: $evaluacion) {
      id
      nombre
      estado
      puntajePsicotecnico
      puntajeTecnico
      puntajeEntrevista
      puntajeTotal
    }
  }
`;

const EvaluacionesComponent = () => {
  const [tabValue, setTabValue] = useState(0);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedCandidato, setSelectedCandidato] = useState(null);
  const [evaluacionData, setEvaluacionData] = useState({
    tipo: 'PSICOTECNICO',
    puntaje: '',
    observaciones: ''
  });

  const { loading: loadingEvaluaciones, error: errorEvaluaciones, data: dataEvaluaciones, refetch } = useQuery(GET_EVALUACIONES);
  const { loading: loadingEstadisticas, error: errorEstadisticas, data: dataEstadisticas } = useQuery(GET_ESTADISTICAS);
  const [registrarEvaluacionPsicotecnica] = useMutation(REGISTRAR_EVALUACION);
  const [registrarEvaluacionTecnica] = useMutation(REGISTRAR_EVALUACION_TECNICA);
  const [registrarEntrevista] = useMutation(REGISTRAR_ENTREVISTA);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleOpenDialog = (candidato) => {
    setSelectedCandidato(candidato);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedCandidato(null);
  };

  const handleSubmit = async () => {
    try {
      if (!evaluacionData.puntaje) {
        alert('Por favor ingrese un puntaje válido');
        return;
      }

      const puntaje = parseFloat(evaluacionData.puntaje);
      if (isNaN(puntaje) || puntaje < 0 || puntaje > 100) {
        alert('El puntaje debe ser un número entre 0 y 100');
        return;
      }

      // Crear el objeto evaluacion con la estructura correcta
      const variables = {
        evaluacion: {
          candidatoId: selectedCandidato.id,
          puntaje,
          observaciones: evaluacionData.observaciones || ''
        }
      };

      console.log('Enviando evaluación:', variables);

      let response;
      switch (evaluacionData.tipo) {
        case 'PSICOTECNICO':
          response = await registrarEvaluacionPsicotecnica({
            variables,
            refetchQueries: [{ query: GET_EVALUACIONES }]
          });
          break;
        case 'TECNICO':
          response = await registrarEvaluacionTecnica({
            variables,
            refetchQueries: [{ query: GET_EVALUACIONES }]
          });
          break;
        case 'ENTREVISTA':
          response = await registrarEntrevista({
            variables,
            refetchQueries: [{ query: GET_EVALUACIONES }]
          });
          break;
        default:
          throw new Error('Tipo de evaluación no válido');
      }

      if (response.data) {
        alert('Evaluación registrada con éxito');
        handleCloseDialog();
        setEvaluacionData({
          tipo: 'PSICOTECNICO',
          puntaje: '',
          observaciones: ''
        });
      }
    } catch (error) {
      console.error('Error completo:', error);
      const errorMessage = error.graphQLErrors?.[0]?.message || error.message || 'Error desconocido';
      alert(`Error al registrar la evaluación: ${errorMessage}`);
    }
  };

  if (loadingEvaluaciones || loadingEstadisticas) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (errorEvaluaciones || errorEstadisticas) {
    return <Typography color="error">Error: {errorEvaluaciones?.message || errorEstadisticas?.message}</Typography>;
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Evaluaciones y Reportes
      </Typography>

      <Tabs value={tabValue} onChange={handleTabChange} sx={{ mb: 3 }}>
        <Tab label="Evaluaciones" />
        <Tab label="Reportes" />
      </Tabs>

      {/* Panel de Evaluaciones */}
      {tabValue === 0 && (
        <Grid container spacing={2}>
          {dataEvaluaciones?.candidatos.map((candidato) => (
            <Grid item xs={12} md={6} key={candidato.id}>
              <Paper sx={{ p: 2 }}>
                <Typography variant="h6">{candidato.nombre}</Typography>
                
                <Box sx={{ my: 2 }}>
                  {candidato.puntajePsicotecnico && (
                    <Box sx={{ mb: 1 }}>
                      <Typography>Psicotécnico: {candidato.puntajePsicotecnico}%</Typography>
                      <LinearProgress variant="determinate" value={candidato.puntajePsicotecnico} />
                    </Box>
                  )}
                  {candidato.puntajeTecnico && (
                    <Box sx={{ mb: 1 }}>
                      <Typography>Técnico: {candidato.puntajeTecnico}%</Typography>
                      <LinearProgress variant="determinate" value={candidato.puntajeTecnico} color="secondary" />
                    </Box>
                  )}
                  {candidato.puntajeEntrevista && (
                    <Box sx={{ mb: 1 }}>
                      <Typography>Entrevista: {candidato.puntajeEntrevista}%</Typography>
                      <LinearProgress variant="determinate" value={candidato.puntajeEntrevista} color="success" />
                    </Box>
                  )}
                  {candidato.puntajeTotal && (
                    <Box sx={{ mt: 2 }}>
                      <Typography variant="subtitle1">Total: {candidato.puntajeTotal}%</Typography>
                      <LinearProgress variant="determinate" value={candidato.puntajeTotal} color="primary" />
                    </Box>
                  )}
                </Box>

                <Button
                  variant="contained"
                  onClick={() => handleOpenDialog(candidato)}
                  startIcon={<AssessmentIcon />}
                >
                  Nueva Evaluación
                </Button>
              </Paper>
            </Grid>
          ))}
        </Grid>
      )}

      {/* Panel de Reportes */}
      {tabValue === 1 && (
        <Grid container spacing={3}>
          {/* Estadísticas por Estado */}
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Candidatos por Estado
                </Typography>
                <Grid container spacing={2}>
                  {(() => {
                    const estadosPorTipo = dataEvaluaciones?.candidatos.reduce((acc, req) => {
                      acc[req.estado] = (acc[req.estado] || 0) + 1;
                      return acc;
                    }, {});
                    
                    const total = dataEvaluaciones?.candidatos.length || 0;
                    
                    return Object.entries(estadosPorTipo || {}).map(([estado, cantidad]) => (
                      <Grid item xs={12} md={4} key={estado}>
                        <Typography variant="body2">{estado}</Typography>
                        <Typography variant="body2" color="textSecondary">
                          {cantidad} candidatos ({((cantidad/total) * 100).toFixed(1)}%)
                        </Typography>
                        <LinearProgress 
                          variant="determinate" 
                          value={(cantidad/total) * 100} 
                        />
                      </Grid>
                    ));
                  })()}
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          {/* Habilidades más Comunes */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Habilidades más Comunes
                </Typography>
                <Box sx={{ mt: 2 }}>
                  {(() => {
                    const habilidadesPorCandidato = dataEvaluaciones?.candidatos.reduce((acc, candidato) => {
                      if (candidato.habilidades) {
                        // Asumiendo que habilidades es una cadena separada por comas
                        const habilidadesArray = candidato.habilidades.split(',').map(h => h.trim());
                        habilidadesArray.forEach(habilidad => {
                          acc[habilidad] = (acc[habilidad] || 0) + 1;
                        });
                      }
                      return acc;
                    }, {});
                    
                    const total = dataEvaluaciones?.candidatos.length || 0;
                    
                    // Ordenar habilidades por frecuencia
                    const habilidadesOrdenadas = Object.entries(habilidadesPorCandidato || {})
                      .sort(([,a], [,b]) => b - a)
                      .slice(0, 5); // Mostrar solo las 5 más comunes
                    
                    return habilidadesOrdenadas.map(([habilidad, cantidad]) => (
                      <Box key={habilidad} sx={{ mb: 2 }}>
                        <Typography>{habilidad}</Typography>
                        <Typography variant="body2" color="textSecondary">
                          {cantidad} candidatos ({((cantidad/total) * 100).toFixed(1)}%)
                        </Typography>
                        <LinearProgress 
                          variant="determinate" 
                          value={(cantidad/total) * 100} 
                          color="primary"
                        />
                      </Box>
                    ));
                  })()}
                </Box>
              </CardContent>
            </Card>
          </Grid>

          {/* Total de Candidatos */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Resumen General
                </Typography>
                <Typography variant="h4" color="primary">
                  {dataEvaluaciones?.candidatos.length || 0}
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  Total de Candidatos
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* Diálogo para nueva evaluación */}
      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>
          Nueva Evaluación - {selectedCandidato?.nombre}
        </DialogTitle>
        <DialogContent>
          <TextField
            select
            fullWidth
            label="Tipo de Evaluación"
            value={evaluacionData.tipo}
            onChange={(e) => setEvaluacionData({...evaluacionData, tipo: e.target.value})}
            margin="normal"
          >
            <MenuItem value="PSICOTECNICO">Evaluación Psicotécnica</MenuItem>
            <MenuItem value="TECNICO">Evaluación Técnica</MenuItem>
            <MenuItem value="ENTREVISTA">Entrevista</MenuItem>
          </TextField>
          
          <TextField
            fullWidth
            type="number"
            label="Puntaje"
            value={evaluacionData.puntaje}
            onChange={(e) => setEvaluacionData({...evaluacionData, puntaje: e.target.value})}
            margin="normal"
            inputProps={{ min: 0, max: 100, step: 0.1 }}
            helperText="Ingrese un valor entre 0 y 100"
          />

          <TextField
            fullWidth
            multiline
            rows={4}
            label="Observaciones"
            value={evaluacionData.observaciones}
            onChange={(e) => setEvaluacionData({...evaluacionData, observaciones: e.target.value})}
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancelar</Button>
          <Button 
            onClick={handleSubmit} 
            variant="contained" 
            color="primary"
            disabled={!evaluacionData.puntaje}
          >
            Guardar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

const Evaluaciones = () => {
  return (
    <ApolloProvider client={client}>
      <EvaluacionesComponent />
    </ApolloProvider>
  );
};

export default Evaluaciones; 