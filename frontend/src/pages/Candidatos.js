import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Typography,
  MenuItem,
  Chip,
  Grid,
  Divider,
  Tab,
  Tabs,
  Card,
  CardContent,
  LinearProgress,
  CircularProgress,
  FormControl,
  InputLabel,
  Select
} from '@mui/material';
import { 
  Edit as EditIcon, 
  Delete as DeleteIcon, 
  Add as AddIcon,
  Assessment as AssessmentIcon,
  Update as UpdateIcon,
  BarChart as BarChartIcon,
  Work as WorkIcon
} from '@mui/icons-material';
import { ApolloClient, InMemoryCache, ApolloProvider, useQuery, useMutation, gql, createHttpLink } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';

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

const GET_CANDIDATOS = gql`
  query {
    candidatos {
      id
      nombre
      correo
      telefono
      experiencia
      habilidades
      vacanteId
      tipo
      estado
      puntajePsicotecnico
      puntajeTecnico
      puntajeEntrevista
      puntajeTotal
      fechaPostulacion
      fechaUltimaEvaluacion
      observaciones
    }
  }
`;

const GET_ESTADISTICAS = gql`
  query {
    estadisticasHabilidades {
      habilidad
      cantidad
      porcentajeCandidatos
    }
  }
`;

const CREATE_CANDIDATO = gql`
  mutation CreateCandidato($candidato: CandidatoInput!) {
    createCandidato(candidato: $candidato) {
      id
      nombre
      correo
      telefono
      experiencia
      habilidades
      tipo
      estado
    }
  }
`;

const UPDATE_CANDIDATO = gql`
  mutation ActualizarCandidato($id: ID!, $input: CandidatoInput!) {
    actualizarCandidato(id: $id, input: $input) {
      id
      nombre
      correo
      telefono
      experiencia
      habilidades
      tipo
      estado
    }
  }
`;

const DELETE_CANDIDATO = gql`
  mutation EliminarCandidato($id: ID!) {
    eliminarCandidato(id: $id)
  }
`;

const REGISTRAR_EVALUACION_PSICOTECNICA = gql`
  mutation RegistrarEvaluacionPsicotecnica($evaluacion: EvaluacionInput!) {
    registrarEvaluacionPsicotecnica(evaluacion: $evaluacion) {
      id
      puntajePsicotecnico
      puntajeTecnico
      puntajeEntrevista
      puntajeTotal
      fechaUltimaEvaluacion
      estado
      observaciones
    }
  }
`;

const REGISTRAR_EVALUACION_TECNICA = gql`
  mutation RegistrarEvaluacionTecnica($evaluacion: EvaluacionInput!) {
    registrarEvaluacionTecnica(evaluacion: $evaluacion) {
      id
      puntajePsicotecnico
      puntajeTecnico
      puntajeEntrevista
      puntajeTotal
      fechaUltimaEvaluacion
      estado
      observaciones
    }
  }
`;

const REGISTRAR_ENTREVISTA = gql`
  mutation RegistrarEntrevista($evaluacion: EvaluacionInput!) {
    registrarEntrevista(evaluacion: $evaluacion) {
      id
      puntajePsicotecnico
      puntajeTecnico
      puntajeEntrevista
      puntajeTotal
      fechaUltimaEvaluacion
      estado
      observaciones
    }
  }
`;

const POSTULAR_A_VACANTE = gql`
  mutation PostularAVacante($postulacion: PostulacionInput!) {
    postularAVacante(postulacion: $postulacion) {
      id
      estado
    }
  }
`;

const CandidatosComponent = () => {
  const [open, setOpen] = useState(false);
  const [openEvaluacion, setOpenEvaluacion] = useState(false);
  const [openVerEvaluaciones, setOpenVerEvaluaciones] = useState(false);
  const [openPostulacion, setOpenPostulacion] = useState(false);
  const [selectedTab, setSelectedTab] = useState(0);
  const [selectedCandidato, setSelectedCandidato] = useState(null);
  const [selectedVacante, setSelectedVacante] = useState('');
  const [formData, setFormData] = useState({
    nombre: '',
    correo: '',
    telefono: '',
    experiencia: '',
    habilidades: '',
    vacanteId: '',
    tipo: 'EXTERNO',
    estado: 'POSTULADO',
    observaciones: ''
  });

  const [evaluacionData, setEvaluacionData] = useState({
    tipo: 'PSICOTECNICO',
    puntaje: '',
    observaciones: ''
  });

  const { loading: loadingCandidatos, error: errorCandidatos, data: dataCandidatos, refetch } = useQuery(GET_CANDIDATOS);
  const { loading: loadingEstadisticas, error: errorEstadisticas, data: dataEstadisticas } = useQuery(GET_ESTADISTICAS);
  const [createCandidato] = useMutation(CREATE_CANDIDATO);
  const [updateCandidato] = useMutation(UPDATE_CANDIDATO);
  const [deleteCandidato] = useMutation(DELETE_CANDIDATO);
  const [registrarEvaluacionPsicotecnica] = useMutation(REGISTRAR_EVALUACION_PSICOTECNICA);
  const [registrarEvaluacionTecnica] = useMutation(REGISTRAR_EVALUACION_TECNICA);
  const [registrarEntrevista] = useMutation(REGISTRAR_ENTREVISTA);
  // Desestructuramos loading para manejar el estado de la postulación
const [postularAVacante, { loading: loadingPostulacion }] = useMutation(POSTULAR_A_VACANTE, {
  onCompleted: () => {
    alert('Postulación realizada con éxito');
    handleClosePostulacion();
    refetch();
  },
  onError: (error) => {
    console.error('Error al postular:', error);
    alert('Error al realizar la postulación');
  }
});


  const candidatos = dataCandidatos?.candidatos || [];
  const estadisticas = dataEstadisticas?.estadisticasHabilidades || [];

  const estados = [
    'POSTULADO',
    'EN_EVALUACION_TECNICA',
    'FINALISTA',
    'SELECCIONADO',
    'RECHAZADO'
  ];

  const tipos = ['INTERNO', 'EXTERNO', 'RECOMENDADO'];

  const getEstadoColor = (estado) => {
    switch (estado) {
      case 'POSTULADO': return 'info';
      case 'EN_EVALUACION_TECNICA': return 'warning';
      case 'FINALISTA': return 'primary';
      case 'SELECCIONADO': return 'success';
      case 'RECHAZADO': return 'error';
      default: return 'default';
    }
  };

  const handleOpen = (candidato = null) => {
    if (candidato) {
      setFormData({
        nombre: candidato.nombre || '',
        correo: candidato.correo || '',
        telefono: candidato.telefono || '',
        experiencia: candidato.experiencia || '',
        habilidades: candidato.habilidades || '',
        tipo: candidato.tipo || 'EXTERNO',
        vacanteId: candidato.vacanteId || '',
        observaciones: candidato.observaciones || ''
      });
      setSelectedCandidato(candidato);
    } else {
      setFormData({
        nombre: '',
        correo: '',
        telefono: '',
        experiencia: '',
        habilidades: '',
        vacanteId: '',
        tipo: 'EXTERNO',
        estado: 'POSTULADO',
        observaciones: ''
      });
      setSelectedCandidato(null);
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedCandidato(null);
  };

  const handleOpenEvaluacion = (candidato) => {
    setSelectedCandidato(candidato);
    setEvaluacionData({
      tipo: 'PSICOTECNICO',
      puntaje: '',
      observaciones: ''
    });
    setOpenEvaluacion(true);
  };

  const handleCloseEvaluacion = () => {
    setOpenEvaluacion(false);
    setSelectedCandidato(null);
  };

  const handleOpenVerEvaluaciones = (candidato) => {
    setSelectedCandidato(candidato);
    setOpenVerEvaluaciones(true);
  };

  const handleCloseVerEvaluaciones = () => {
    setOpenVerEvaluaciones(false);
    setSelectedCandidato(null);
  };

  const handleOpenPostulacion = (candidato) => {
    setSelectedCandidato(candidato);
    setOpenPostulacion(true);
  };

  const handleClosePostulacion = () => {
    setOpenPostulacion(false);
    setSelectedVacante('');
    setSelectedCandidato(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (selectedCandidato) {
        const { 
          id, 
          estado, 
          puntajeTotal,
          puntajePsicotecnico,
          puntajeTecnico,
          puntajeEntrevista,
          fechaPostulacion,
          fechaUltimaEvaluacion,
          ...inputData 
        } = formData;
        
        await updateCandidato({
          variables: {
            id: selectedCandidato.id,
            input: {
              nombre: inputData.nombre,
              correo: inputData.correo,
              telefono: inputData.telefono,
              experiencia: inputData.experiencia,
              habilidades: inputData.habilidades,
              tipo: inputData.tipo,
              vacanteId: inputData.vacanteId || null,
              observaciones: inputData.observaciones || ''
            }
          }
        });
      } else {
        const candidatoInput = {
          nombre: formData.nombre,
          correo: formData.correo,
          telefono: formData.telefono,
          experiencia: formData.experiencia,
          habilidades: formData.habilidades,
          tipo: formData.tipo
        };

        await createCandidato({
          variables: {
            candidato: candidatoInput
          }
        });
      }
      handleClose();
      refetch();
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleEvaluacionSubmit = async (e) => {
    e.preventDefault();
    try {
      const puntaje = parseFloat(evaluacionData.puntaje);
      if (isNaN(puntaje) || puntaje < 0 || puntaje > 100) {
        alert('El puntaje debe ser un número entre 0 y 100');
        return;
      }

      const evaluacionInput = {
        candidatoId: selectedCandidato.id,
        puntaje: puntaje,
        observaciones: evaluacionData.observaciones || ''
      };

      console.log('Enviando evaluación:', evaluacionInput);

      let mutationResult;
      let mutation;

      switch (evaluacionData.tipo) {
        case 'PSICOTECNICO':
          mutation = registrarEvaluacionPsicotecnica;
          break;
        case 'TECNICO':
          mutation = registrarEvaluacionTecnica;
          break;
        case 'ENTREVISTA':
          mutation = registrarEntrevista;
          break;
        default:
          throw new Error('Tipo de evaluación no válido');
      }

      mutationResult = await mutation({
        variables: { evaluacion: evaluacionInput },
        refetchQueries: [{ query: GET_CANDIDATOS }]
      });

      if (mutationResult.errors) {
        console.error('Errores en la mutación:', mutationResult.errors);
        throw new Error(mutationResult.errors[0].message);
      }

      handleCloseEvaluacion();
      
      const tipoEvaluacion = {
        'PSICOTECNICO': 'psicotécnica',
        'TECNICO': 'técnica',
        'ENTREVISTA': 'entrevista'
      }[evaluacionData.tipo];
      
      alert(`Evaluación ${tipoEvaluacion} registrada con éxito`);
      
    } catch (error) {
      console.error('Error completo:', error);
      const errorMessage = error.message || 'Error desconocido';
      console.log('Mensaje de error:', errorMessage);
      alert(`Error al registrar la evaluación: ${errorMessage}`);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este candidato?')) {
      try {
        await deleteCandidato({
          variables: { id }
        });
      } catch (error) {
        console.error('Error al eliminar candidato:', error);
      }
    }
  };

  const handlePostular = async () => {
    if (!selectedCandidato || !selectedVacante) {
      alert('Debe seleccionar un candidato e ingresar el ID de la vacante');
      return;
    }
  
    try {
      await postularAVacante({
        variables: {
          postulacion: {
            candidatoId: selectedCandidato.id,
            vacanteId: parseInt(selectedVacante)
          }
        }
      });
    } catch (error) {
      console.error('Error al postular:', error);
    }
  };
  

  const handleTabChange = (event, newValue) => {
    setSelectedTab(newValue);
  };

  const DialogoPostulacion = () => (
    <Dialog open={openPostulacion} onClose={handleClosePostulacion}>
      <DialogTitle>
        Postular Candidato: {selectedCandidato?.nombre}
      </DialogTitle>
      <DialogContent>
        <Box sx={{ mt: 2 }}>
          <TextField
            fullWidth
            type="number"
            label="ID de la Vacante"
            value={selectedVacante}
            onChange={(e) => setSelectedVacante(e.target.value)}
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClosePostulacion}>
          Cancelar
        </Button>
        <Button 
          onClick={handlePostular}
          variant="contained"
          color="primary"
          disabled={!selectedVacante || loadingPostulacion}
        >
          {loadingPostulacion ? 'Postulando...' : 'Postular'}
        </Button>
      </DialogActions>

    </Dialog>
  );

  if (loadingCandidatos || loadingEstadisticas) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
        <Typography sx={{ ml: 2 }}>Cargando datos...</Typography>
      </Box>
    );
  }

  if (errorCandidatos || errorEstadisticas) {
    console.error('Error en candidatos:', errorCandidatos);
    console.error('Error en estadísticas:', errorEstadisticas);
    return (
      <Box sx={{ p: 3, textAlign: 'center' }}>
        <Typography color="error" variant="h6">Error al cargar los datos</Typography>
        <Typography color="textSecondary">
          {errorCandidatos?.message || errorEstadisticas?.message || 'Error desconocido'}
        </Typography>
        <Button 
          variant="contained" 
          sx={{ mt: 2 }}
          onClick={() => window.location.reload()}
        >
          Reintentar
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4">Gestión de Candidatos</Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Nuevo Candidato
        </Button>
      </Box>

      <Tabs value={selectedTab} onChange={handleTabChange} sx={{ mb: 3 }}>
        <Tab label="Lista de Candidatos" />
        <Tab label="Evaluaciones" />
        <Tab label="Reportes" />
      </Tabs>

      {selectedTab === 0 && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Nombre</TableCell>
                <TableCell>Correo</TableCell>
                <TableCell>Teléfono</TableCell>
                <TableCell>Tipo</TableCell>
                <TableCell>Vacante</TableCell>
                <TableCell>Estado</TableCell>
                <TableCell>Puntaje Total</TableCell>
                <TableCell>Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {candidatos.map((candidato) => (
                <TableRow key={candidato.id}>
                  <TableCell>{candidato.nombre}</TableCell>
                  <TableCell>{candidato.correo}</TableCell>
                  <TableCell>{candidato.telefono}</TableCell>
                  <TableCell>{candidato.tipo}</TableCell>
                  <TableCell>
                    {candidato.vacanteId ? (
                      <Chip 
                        label={`Vacante ${candidato.vacanteId}`}
                        color="primary"
                        variant="outlined"
                        size="small"
                      />
                    ) : (
                      <Typography variant="body2" color="text.secondary">
                        Sin postulación
                      </Typography>
                    )}
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={candidato.estado}
                      color={getEstadoColor(candidato.estado)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <Box>
                      {candidato.puntajeTotal ? (
                        <Typography variant="body2" fontWeight="bold">
                          {candidato.puntajeTotal.toFixed(1)}%
                        </Typography>
                      ) : (
                        <Typography variant="body2" color="text.secondary">
                          Sin evaluaciones
                        </Typography>
                      )}
                    </Box>
                  </TableCell>
                  <TableCell>
                    <IconButton onClick={() => handleOpen(candidato)}>
                      <EditIcon />
                    </IconButton>
                    <IconButton onClick={() => handleDelete(candidato.id)}>
                      <DeleteIcon />
                    </IconButton>
                    <IconButton 
                      onClick={() => handleOpenVerEvaluaciones(candidato)}
                    >
                      <AssessmentIcon />
                    </IconButton>
                    <IconButton 
                      onClick={() => handleOpenPostulacion(candidato)}
                      color="primary"
                      title="Postular a vacante"
                    >
                      <WorkIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {selectedTab === 1 && (
        <Box>
          <Typography variant="h6" sx={{ mb: 2 }}>Registro de Evaluaciones</Typography>
          <Grid container spacing={2}>
            {candidatos.map((candidato) => (
              <Grid item xs={12} md={6} key={candidato.id}>
                <Paper sx={{ p: 2 }}>
                  <Typography variant="subtitle1">{candidato.nombre}</Typography>
                  <Typography variant="body2" sx={{ mb: 1 }}>Estado: {candidato.estado}</Typography>
                  
                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" color="text.secondary">Evaluaciones:</Typography>
                    {candidato.puntajePsicotecnico && (
                      <Box sx={{ mt: 1 }}>
                        <Typography variant="body2">
                          Psicotécnico: {candidato.puntajePsicotecnico.toFixed(1)}
                        </Typography>
                        <LinearProgress 
                          variant="determinate" 
                          value={candidato.puntajePsicotecnico} 
                          sx={{ mt: 0.5 }}
                        />
                      </Box>
                    )}
                    {candidato.puntajeTecnico && (
                      <Box sx={{ mt: 1 }}>
                        <Typography variant="body2">
                          Técnico: {candidato.puntajeTecnico.toFixed(1)}
                        </Typography>
                        <LinearProgress 
                          variant="determinate" 
                          value={candidato.puntajeTecnico}
                          color="secondary"
                          sx={{ mt: 0.5 }}
                        />
                      </Box>
                    )}
                    {candidato.puntajeEntrevista && (
                      <Box sx={{ mt: 1 }}>
                        <Typography variant="body2">
                          Entrevista: {candidato.puntajeEntrevista.toFixed(1)}
                        </Typography>
                        <LinearProgress 
                          variant="determinate" 
                          value={candidato.puntajeEntrevista}
                          color="success"
                          sx={{ mt: 0.5 }}
                        />
                      </Box>
                    )}
                    {candidato.puntajeTotal && (
                      <Box sx={{ mt: 1 }}>
                        <Typography variant="body2" fontWeight="bold">
                          Total: {candidato.puntajeTotal.toFixed(1)}
                        </Typography>
                        <LinearProgress 
                          variant="determinate" 
                          value={candidato.puntajeTotal}
                          color="primary"
                          sx={{ mt: 0.5 }}
                        />
                      </Box>
                    )}
                  </Box>

                  <Button
                    variant="contained"
                    onClick={() => handleOpenEvaluacion(candidato)}
                    sx={{ mt: 1 }}
                    startIcon={<AssessmentIcon />}
                  >
                    Registrar Nueva Evaluación
                  </Button>
                </Paper>
              </Grid>
            ))}
          </Grid>
        </Box>
      )}

      {selectedTab === 2 && (
        <Box>
          <Typography variant="h6" sx={{ mb: 2 }}>Reportes y Estadísticas</Typography>
          
          <Grid container spacing={3}>
            {/* Estadísticas de Habilidades */}
            <Grid item xs={12}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Habilidades más Comunes
                  </Typography>
                  <Grid container spacing={2}>
                    {estadisticas.map(({ habilidad, cantidad, porcentajeCandidatos }) => (
                      <Grid item xs={12} md={4} key={habilidad}>
                        <Box sx={{ mb: 1 }}>
                          <Typography variant="body2">{habilidad}</Typography>
                          <Typography variant="body2" color="text.secondary">
                            {cantidad} candidatos ({porcentajeCandidatos ? porcentajeCandidatos.toFixed(1) : '0.0'}%)
                          </Typography>
                          <LinearProgress 
                            variant="determinate" 
                            value={porcentajeCandidatos || 0}
                            color="secondary"
                          />
                        </Box>
                      </Grid>
                    ))}
                  </Grid>
                </CardContent>
              </Card>
            </Grid>

            {/* Promedio de Evaluaciones Técnicas */}
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Promedio de Evaluaciones Técnicas
                  </Typography>
                  <Typography variant="h4" color="primary">
                    {estadisticas.reduce((total, { cantidad }) => total + cantidad, 0) / estadisticas.length}
                  </Typography>
                  <LinearProgress 
                    variant="determinate" 
                    value={estadisticas.reduce((total, { cantidad }) => total + cantidad, 0) / estadisticas.length}
                    color="primary"
                    sx={{ mt: 2 }}
                  />
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </Box>
      )}

      {/* Diálogo para crear/editar candidato */}
      <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
        <DialogTitle>
          {selectedCandidato ? 'Editar Candidato' : 'Nuevo Candidato'}
        </DialogTitle>
        <form onSubmit={handleSubmit}>
          <DialogContent>
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  label="Nombre"
                  value={formData.nombre}
                  onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  required
                  label="Correo"
                  type="email"
                  value={formData.correo}
                  onChange={(e) => setFormData({ ...formData, correo: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  required
                  label="Teléfono"
                  value={formData.telefono}
                  onChange={(e) => setFormData({ ...formData, telefono: e.target.value })}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  label="Experiencia"
                  multiline
                  rows={2}
                  value={formData.experiencia}
                  onChange={(e) => setFormData({ ...formData, experiencia: e.target.value })}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  label="Habilidades"
                  multiline
                  rows={2}
                  value={formData.habilidades}
                  onChange={(e) => setFormData({ ...formData, habilidades: e.target.value })}
                />
              </Grid>
              <Grid item xs={12}>
                <FormControl fullWidth required>
                  <InputLabel>Tipo</InputLabel>
                  <Select
                    value={formData.tipo}
                    onChange={(e) => setFormData({ ...formData, tipo: e.target.value })}
                    label="Tipo"
                  >
                    <MenuItem value="INTERNO">Interno</MenuItem>
                    <MenuItem value="EXTERNO">Externo</MenuItem>
                    <MenuItem value="AMBOS">Ambos</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Cancelar</Button>
            <Button type="submit" variant="contained" color="primary">
              {selectedCandidato ? 'Actualizar' : 'Crear'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Diálogo para evaluación */}
      <Dialog open={openEvaluacion} onClose={handleCloseEvaluacion} maxWidth="sm" fullWidth>
        <DialogTitle>Registrar Evaluación</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                select
                label="Tipo de Evaluación"
                value={evaluacionData.tipo}
                onChange={(e) => setEvaluacionData({ ...evaluacionData, tipo: e.target.value })}
              >
                <MenuItem value="PSICOTECNICO">Psicotécnica</MenuItem>
                <MenuItem value="TECNICO">Técnica</MenuItem>
                <MenuItem value="ENTREVISTA">Entrevista</MenuItem>
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                type="number"
                label="Puntaje"
                value={evaluacionData.puntaje}
                onChange={(e) => setEvaluacionData({ 
                  ...evaluacionData, 
                  puntaje: e.target.value === '' ? '' : Number(e.target.value)
                })}
                inputProps={{ 
                  min: 0, 
                  max: 100,
                  step: "0.1"
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                rows={3}
                label="Observaciones"
                value={evaluacionData.observaciones}
                onChange={(e) => setEvaluacionData({ ...evaluacionData, observaciones: e.target.value })}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseEvaluacion}>Cancelar</Button>
          <Button onClick={handleEvaluacionSubmit} variant="contained" color="primary">
            Registrar
          </Button>
        </DialogActions>
      </Dialog>

      {/* Diálogo para postulación */}
      <DialogoPostulacion />

      {/* Diálogo para ver evaluaciones */}
      <Dialog open={openVerEvaluaciones} onClose={handleCloseVerEvaluaciones} maxWidth="sm" fullWidth>
        <DialogTitle>Evaluaciones de {selectedCandidato?.nombre}</DialogTitle>
        <DialogContent>
          <Box sx={{ py: 2 }}>
            {selectedCandidato?.puntajePsicotecnico && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="subtitle2" color="primary">Evaluación Psicotécnica</Typography>
                <Typography variant="h6">{selectedCandidato.puntajePsicotecnico.toFixed(1)}%</Typography>
                <LinearProgress 
                  variant="determinate" 
                  value={selectedCandidato.puntajePsicotecnico}
                  sx={{ mt: 1 }}
                />
              </Box>
            )}
            
            {selectedCandidato?.puntajeTecnico && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="subtitle2" color="secondary">Evaluación Técnica</Typography>
                <Typography variant="h6">{selectedCandidato.puntajeTecnico.toFixed(1)}%</Typography>
                <LinearProgress 
                  variant="determinate" 
                  value={selectedCandidato.puntajeTecnico}
                  color="secondary"
                  sx={{ mt: 1 }}
                />
              </Box>
            )}
            
            {selectedCandidato?.puntajeEntrevista && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="subtitle2" color="success.main">Entrevista</Typography>
                <Typography variant="h6">{selectedCandidato.puntajeEntrevista.toFixed(1)}%</Typography>
                <LinearProgress 
                  variant="determinate" 
                  value={selectedCandidato.puntajeEntrevista}
                  color="success"
                  sx={{ mt: 1 }}
                />
              </Box>
            )}
            
            {selectedCandidato?.puntajeTotal && (
              <Box sx={{ mt: 3, pt: 2, borderTop: 1, borderColor: 'divider' }}>
                <Typography variant="subtitle2" color="text.secondary">Puntaje Total</Typography>
                <Typography variant="h5" color="primary">{selectedCandidato.puntajeTotal.toFixed(1)}%</Typography>
                <LinearProgress 
                  variant="determinate" 
                  value={selectedCandidato.puntajeTotal}
                  color="primary"
                  sx={{ mt: 1 }}
                />
              </Box>
            )}

            {!selectedCandidato?.puntajeTotal && (
              <Typography color="text.secondary" align="center">
                No hay evaluaciones registradas
              </Typography>
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseVerEvaluaciones}>Cerrar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

const Candidatos = () => {
  return (
    <ApolloProvider client={client}>
      <CandidatosComponent />
    </ApolloProvider>
  );
};

export default Candidatos; 