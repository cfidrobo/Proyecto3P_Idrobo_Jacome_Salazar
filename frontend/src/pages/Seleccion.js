import React, { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Paper,
  Grid,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  IconButton,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tabs,
  Tab,
  CircularProgress,
  Chip
} from '@mui/material';
import { Edit as EditIcon, Add as AddIcon } from '@mui/icons-material';
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

// Definición de consultas GraphQL
const GET_SELECCIONES = gql`
  query {
    getSelecciones {
      id
      candidatoId
      requisicionId
      fecha
      estado
      comentarios
    }
  }
`;

const GET_CANDIDATOS = gql`
  query {
    candidatos {
      id
      nombre
      correo
      telefono
      experiencia
      habilidades
      tipo
      estado
      vacanteId
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

const GET_VACANTES = gql`
  query {
    getVacantes {
      id
      cargo
      departamento
    }
  }
`;

const CREATE_SELECCION = gql`
  mutation CreateSeleccion($input: SeleccionInput!) {
    createSeleccion(input: $input) {
      id
      candidatoId
      requisicionId
      fecha
      estado
      comentarios
    }
  }
`;

const SeleccionComponent = () => {
  const [open, setOpen] = useState(false);
  const [selectedSeleccion, setSelectedSeleccion] = useState(null);
  const [selectedTab, setSelectedTab] = useState(0);
  const [formData, setFormData] = useState({
    candidatoId: '',
    requisicionId: '',
    fecha: '',
    estado: 'PENDIENTE',
    comentarios: ''
  });

  const { loading: loadingSelecciones, error: errorSelecciones, data: dataSelecciones, refetch } = useQuery(GET_SELECCIONES);
  const { loading, error, data } = useQuery(GET_CANDIDATOS);
  const { loading: loadingVacantes, data: dataVacantes } = useQuery(GET_VACANTES);
  const [createSeleccion] = useMutation(CREATE_SELECCION);

  const estados = ['PENDIENTE', 'APROBADA', 'RECHAZADA'];

  const getEstadoColor = (estado) => {
    switch (estado) {
      case 'POSTULADO': return 'info';
      case 'EN_EVALUACION_PSICOTECNICA': return 'warning';
      case 'EN_EVALUACION_TECNICA': return 'warning';
      case 'FINALISTA': return 'success';
      case 'SELECCIONADO': return 'success';
      case 'RECHAZADO': return 'error';
      default: return 'default';
    }
  };

  const handleOpen = (seleccion = null) => {
    if (seleccion) {
      setFormData({
        candidatoId: seleccion.candidatoId,
        requisicionId: seleccion.requisicionId,
        fecha: seleccion.fecha,
        estado: seleccion.estado,
        comentarios: seleccion.comentarios || ''
      });
      setSelectedSeleccion(seleccion);
    } else {
      setFormData({
        candidatoId: '',
        requisicionId: '',
        fecha: '',
        estado: 'PENDIENTE',
        comentarios: ''
      });
      setSelectedSeleccion(null);
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedSeleccion(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await createSeleccion({
        variables: {
          input: formData
        }
      });
      handleClose();
      refetch();
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleTabChange = (event, newValue) => {
    setSelectedTab(newValue);
  };

  if (loadingSelecciones || loadingVacantes || loading) {
    return <CircularProgress />;
  }

  if (errorSelecciones || error) {
    return <Typography color="error">Error: {errorSelecciones?.message || error.message}</Typography>;
  }

  const candidatos = data?.candidatos || [];
  const candidatosFiltrados = selectedTab === 0 
    ? candidatos.filter(c => c.estado !== 'RECHAZADO' && c.estado !== 'SELECCIONADO')
    : candidatos.filter(c => c.estado === 'RECHAZADO' || c.estado === 'SELECCIONADO');

  return (
    <Box sx={{ py: 3 }}>
      <Container maxWidth="lg">
        <Grid container spacing={3}>
          <Grid item xs={12}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="h4">Proceso de Selección</Typography>
              
            </Box>
          </Grid>

          <Grid item xs={12}>
            <Paper sx={{ width: '100%', mb: 2 }}>
              <Tabs value={selectedTab} onChange={handleTabChange}>
                <Tab label="Procesos Activos" />
                <Tab label="Procesos Finalizados" />
              </Tabs>
            </Paper>
          </Grid>

          <Grid item xs={12}>
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Candidato</TableCell>
                    <TableCell>Tipo</TableCell>
                    <TableCell>Vacante ID</TableCell>
                    <TableCell>Estado</TableCell>
                    <TableCell>Evaluaciones</TableCell>
                    <TableCell>Fecha Postulación</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {candidatosFiltrados.map((candidato) => (
                    <TableRow key={candidato.id}>
                      <TableCell>
                        <Box>
                          <Typography variant="subtitle2">{candidato.nombre}</Typography>
                          <Typography variant="body2" color="text.secondary">
                            {candidato.correo}
                          </Typography>
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Chip 
                          label={candidato.tipo} 
                          color={candidato.tipo === 'INTERNO' ? 'primary' : 'default'}
                          size="small"
                        />
                      </TableCell>
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
                            Sin asignar
                          </Typography>
                        )}
                      </TableCell>
                      <TableCell>
                        <Chip 
                          label={candidato.estado.replace(/_/g, ' ')} 
                          color={getEstadoColor(candidato.estado)}
                          size="small"
                        />
                      </TableCell>
                      <TableCell>
                        <Box>
                          {candidato.puntajePsicotecnico && (
                            <Typography variant="body2">
                              Psicotécnico: {candidato.puntajePsicotecnico.toFixed(1)}%
                            </Typography>
                          )}
                          {candidato.puntajeTecnico && (
                            <Typography variant="body2">
                              Técnico: {candidato.puntajeTecnico.toFixed(1)}%
                            </Typography>
                          )}
                          {candidato.puntajeEntrevista && (
                            <Typography variant="body2">
                              Entrevista: {candidato.puntajeEntrevista.toFixed(1)}%
                            </Typography>
                          )}
                          {!candidato.puntajePsicotecnico && 
                           !candidato.puntajeTecnico && 
                           !candidato.puntajeEntrevista && (
                            <Typography variant="body2" color="text.secondary">
                              Sin evaluaciones
                            </Typography>
                          )}
                        </Box>
                      </TableCell>
                      <TableCell>
                        {new Date(candidato.fechaPostulacion).toLocaleDateString()}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Grid>
        </Grid>

        {/* Diálogo para crear/editar selección */}
        <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
          <DialogTitle>
            {selectedSeleccion ? 'Editar Selección' : 'Nueva Selección'}
          </DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12} md={6}>
                <TextField
                  select
                  fullWidth
                  label="Candidato"
                  value={formData.candidatoId}
                  onChange={(e) => setFormData({ ...formData, candidatoId: e.target.value })}
                >
                  {data?.candidatos?.map((candidato) => (
                    <MenuItem key={candidato.id} value={candidato.id}>
                      {`${candidato.nombre} ${candidato.correo}`}
                    </MenuItem>
                  )) || []}
                </TextField>
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  select
                  fullWidth
                  label="Vacante"
                  value={formData.requisicionId}
                  onChange={(e) => setFormData({ ...formData, requisicionId: e.target.value })}
                >
                  {dataVacantes?.getVacantes?.map((vacante) => (
                    <MenuItem key={vacante.id} value={vacante.id}>
                      {`${vacante.cargo} - ${vacante.departamento}`}
                    </MenuItem>
                  )) || []}
                </TextField>
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  type="date"
                  label="Fecha"
                  InputLabelProps={{ shrink: true }}
                  value={formData.fecha}
                  onChange={(e) => setFormData({ ...formData, fecha: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  select
                  fullWidth
                  label="Estado"
                  value={formData.estado}
                  onChange={(e) => setFormData({ ...formData, estado: e.target.value })}
                >
                  {estados.map((estado) => (
                    <MenuItem key={estado} value={estado}>
                      {estado}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={4}
                  label="Comentarios"
                  value={formData.comentarios}
                  onChange={(e) => setFormData({ ...formData, comentarios: e.target.value })}
                />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Cancelar</Button>
            <Button onClick={handleSubmit} variant="contained" color="primary">
              Guardar
            </Button>
          </DialogActions>
        </Dialog>
      </Container>
    </Box>
  );
};

const Seleccion = () => {
  return (
    <ApolloProvider client={client}>
      <SeleccionComponent />
    </ApolloProvider>
  );
};

export default Seleccion;