import React, { useState } from 'react';
import { useQuery, useMutation, gql } from '@apollo/client';
import {
  Box,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Grid,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  IconButton,
  Tooltip
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Check as CheckIcon,
  Close as CloseIcon,
  Send as SendIcon
} from '@mui/icons-material';
import { ApolloClient, InMemoryCache, ApolloProvider, createHttpLink } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';

// Configuración de Apollo Client
const httpLink = createHttpLink({
  uri: 'http://localhost:8090/vacantes/graphql',  // URL del microservicio de vacantes
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

const GET_VACANTES = gql`
  query GetRequisiciones {
    getRequisiciones {
      id
      cargo
      descripcion
      categoriaSalarial
      perfil
      fechaCreacion
      fechaLimiteConvocatoria
      estado
      tipoReclutamiento
      aprobadoPorRRHH
    }
  }
`;

const CREATE_VACANTE = gql`
  mutation CreateRequisicion(
    $cargo: String!
    $descripcion: String!
    $categoriaSalarial: String!
    $perfil: String!
    $fechaLimiteConvocatoria: String!
    $tipoReclutamiento: TipoReclutamiento!
  ) {
    createRequisicion(
      cargo: $cargo
      descripcion: $descripcion
      categoriaSalarial: $categoriaSalarial
      perfil: $perfil
      fechaLimiteConvocatoria: $fechaLimiteConvocatoria
      tipoReclutamiento: $tipoReclutamiento
    ) {
      id
      cargo
      descripcion
      categoriaSalarial
      perfil
      fechaCreacion
      fechaLimiteConvocatoria
      estado
      tipoReclutamiento
      aprobadoPorRRHH
    }
  }
`;

const UPDATE_VACANTE = gql`
  mutation UpdateRequisicion(
    $id: ID!
    $cargo: String
    $descripcion: String
    $categoriaSalarial: String
    $perfil: String
    $fechaLimiteConvocatoria: String
    $tipoReclutamiento: TipoReclutamiento
  ) {
    updateRequisicion(
      id: $id
      cargo: $cargo
      descripcion: $descripcion
      categoriaSalarial: $categoriaSalarial
      perfil: $perfil
      fechaLimiteConvocatoria: $fechaLimiteConvocatoria
      tipoReclutamiento: $tipoReclutamiento
    ) {
      id
      cargo
      estado
    }
  }
`;

const DELETE_VACANTE = gql`
  mutation DeleteRequisicion($id: ID!) {
    deleteRequisicion(id: $id)
  }
`;

const APROBAR_VACANTE = gql`
  mutation AprobarRequisicion($id: ID!) {
    aprobarRequisicion(id: $id) {
      id
      estado
    }
  }
`;

const ENVIAR_PARA_APROBACION = gql`
  mutation EnviarParaAprobacion($id: ID!) {
    enviarParaAprobacion(id: $id) {
      id
      estado
    }
  }
`;

const CERRAR_VACANTE = gql`
  mutation CerrarVacante($id: ID!) {
    cerrarVacante(id: $id) {
      id
      estado
    }
  }
`;

const TIPOS_RECLUTAMIENTO = ['INTERNO', 'EXTERNO', 'AMBOS'];

const VacantesComponent = () => {
  const [open, setOpen] = useState(false);
  const [selectedVacante, setSelectedVacante] = useState(null);
  const [formData, setFormData] = useState({
    cargo: '',
    descripcion: '',
    categoriaSalarial: '',
    perfil: '',
    fechaLimiteConvocatoria: '',
    tipoReclutamiento: 'EXTERNO'
  });

  const { loading, error, data, refetch } = useQuery(GET_VACANTES, {
    onError: (error) => {
      console.error('Error detallado de la query:', {
        message: error.message,
        graphQLErrors: error.graphQLErrors,
        networkError: error.networkError,
        extraInfo: error.extraInfo
      });
    },
    onCompleted: (data) => {
      console.log('Datos recibidos:', data);
    }
  });
  const [createVacante] = useMutation(CREATE_VACANTE, {
    onError: (error) => {
      console.error('Error detallado de la mutación:', {
        message: error.message,
        graphQLErrors: error.graphQLErrors,
        networkError: error.networkError,
        extraInfo: error.extraInfo
      });
    },
    onCompleted: (data) => {
      console.log('Mutación completada:', data);
    }
  });
  const [updateVacante] = useMutation(UPDATE_VACANTE);
  const [deleteVacante] = useMutation(DELETE_VACANTE);
  const [aprobarVacante] = useMutation(APROBAR_VACANTE);
  const [enviarParaAprobacion] = useMutation(ENVIAR_PARA_APROBACION);
  const [cerrarVacante] = useMutation(CERRAR_VACANTE);

  const handleOpen = (vacante = null) => {
    if (vacante) {
      setFormData({
        cargo: vacante.cargo || '',
        descripcion: vacante.descripcion || '',
        categoriaSalarial: vacante.categoriaSalarial || '',
        perfil: vacante.perfil || '',
        fechaLimiteConvocatoria: vacante.fechaLimiteConvocatoria || '',
        tipoReclutamiento: vacante.tipoReclutamiento || 'EXTERNO'
      });
      setSelectedVacante(vacante);
    } else {
      setFormData({
        cargo: '',
        descripcion: '',
        categoriaSalarial: '',
        perfil: '',
        fechaLimiteConvocatoria: '',
        tipoReclutamiento: 'EXTERNO'
      });
      setSelectedVacante(null);
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedVacante(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (selectedVacante) {
        await updateVacante({
          variables: {
            id: selectedVacante.id,
            ...formData
          }
        });
      } else {
        // Validar campos requeridos
        if (!formData.cargo || !formData.descripcion || !formData.categoriaSalarial || 
            !formData.perfil || !formData.fechaLimiteConvocatoria || !formData.tipoReclutamiento) {
          alert('Todos los campos son requeridos');
          return;
        }

        // Convertir la fecha al formato que espera el backend (ISO con hora)
        const fecha = new Date(formData.fechaLimiteConvocatoria);
        fecha.setHours(23, 59, 59);
        const fechaFormateada = fecha.toISOString().split('.')[0]; // Formato: YYYY-MM-DDTHH:mm:ss

        const variables = {
          cargo: formData.cargo.trim(),
          descripcion: formData.descripcion.trim(),
          categoriaSalarial: formData.categoriaSalarial.trim(),
          perfil: formData.perfil.trim(),
          fechaLimiteConvocatoria: fechaFormateada, // Fecha en formato ISO
          tipoReclutamiento: formData.tipoReclutamiento
        };

        console.log('Enviando datos de vacante:', variables);

        try {
          const result = await createVacante({
            variables,
            errorPolicy: 'all'
          });

          console.log('Respuesta del servidor:', result);

          if (result.errors) {
            const errorMessages = result.errors.map(error => {
              console.error('Error detallado:', error);
              return error.message;
            }).join('\n');
            throw new Error(errorMessages);
          }

          if (result.data?.createRequisicion) {
            await refetch();
            handleClose();
            alert('Vacante creada exitosamente');
          } else {
            throw new Error('No se recibieron datos de la vacante creada');
          }
        } catch (mutationError) {
          console.error('Error en la mutación:', {
            message: mutationError.message,
            graphQLErrors: mutationError.graphQLErrors,
            networkError: mutationError.networkError,
            stack: mutationError.stack
          });
          throw mutationError;
        }
      }
    } catch (err) {
      console.error('Error detallado:', {
        message: err.message,
        graphQLErrors: err.graphQLErrors,
        networkError: err.networkError,
        stack: err.stack,
        response: err.response
      });

      let errorMessage = 'Error al crear la vacante: ';
      if (err.graphQLErrors?.length) {
        errorMessage += err.graphQLErrors.map(e => e.message).join('\n');
      } else if (err.networkError) {
        errorMessage += 'Error de red - ' + err.networkError.message;
      } else {
        errorMessage += err.message;
      }

      alert(errorMessage);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar esta vacante?')) {
      try {
        await deleteVacante({
          variables: { id }
        });
        refetch();
      } catch (error) {
        console.error('Error al eliminar vacante:', error);
        alert('Error al eliminar la vacante: ' + error.message);
      }
    }
  };

  const handleAprobar = async (id) => {
    try {
      await aprobarVacante({
        variables: { id }
      });
      refetch();
    } catch (error) {
      console.error('Error al aprobar vacante:', error);
      alert('Error al aprobar la vacante: ' + error.message);
    }
  };

  const handleEnviarAprobacion = async (id) => {
    try {
      await enviarParaAprobacion({
        variables: { id }
      });
      refetch();
    } catch (error) {
      console.error('Error al enviar para aprobación:', error);
      alert('Error al enviar para aprobación: ' + error.message);
    }
  };

  const handleCerrar = async (id) => {
    if (window.confirm('¿Está seguro de cerrar esta vacante?')) {
      try {
        await cerrarVacante({
          variables: { id }
        });
        refetch();
      } catch (error) {
        console.error('Error al cerrar vacante:', error);
        alert('Error al cerrar la vacante: ' + error.message);
      }
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
        <Typography sx={{ ml: 2 }}>Cargando vacantes...</Typography>
      </Box>
    );
  }

  if (error) {
    console.error('Error al cargar vacantes:', {
      message: error.message,
      graphQLErrors: error.graphQLErrors,
      networkError: error.networkError,
      extraInfo: error.extraInfo
    });

    return (
      <Box sx={{ p: 3, textAlign: 'center' }}>
        <Typography color="error" variant="h6">Error al cargar las vacantes</Typography>
        <Typography color="textSecondary">
          {error.graphQLErrors?.length > 0 
            ? error.graphQLErrors.map(e => e.message).join('\n')
            : error.message}
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4">Gestión de Vacantes</Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Nueva Vacante
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Cargo</TableCell>
              <TableCell>Categoría Salarial</TableCell>
              <TableCell>Fecha Límite</TableCell>
              <TableCell>Estado</TableCell>
              <TableCell>Tipo</TableCell>
              <TableCell>Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {data?.getRequisiciones?.map((vacante) => (
              <TableRow key={vacante.id}>
                <TableCell>{vacante.cargo}</TableCell>
                <TableCell>{vacante.categoriaSalarial}</TableCell>
                <TableCell>{new Date(vacante.fechaLimiteConvocatoria).toLocaleDateString()}</TableCell>
                <TableCell>{vacante.estado}</TableCell>
                <TableCell>{vacante.tipoReclutamiento}</TableCell>
                <TableCell>
                  <Tooltip title="Editar">
                    <IconButton onClick={() => handleOpen(vacante)}>
                      <EditIcon />
                    </IconButton>
                  </Tooltip>
                  {vacante.estado === 'BORRADOR' && (
                    <>
                      <Tooltip title="Enviar para aprobación">
                        <IconButton onClick={() => handleEnviarAprobacion(vacante.id)}>
                          <SendIcon />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Eliminar">
                        <IconButton onClick={() => handleDelete(vacante.id)}>
                          <DeleteIcon />
                        </IconButton>
                      </Tooltip>
                    </>
                  )}
                  {vacante.estado === 'PENDIENTE_APROBACION' && (
                    <Tooltip title="Aprobar">
                      <IconButton onClick={() => handleAprobar(vacante.id)}>
                        <CheckIcon />
                      </IconButton>
                    </Tooltip>
                  )}
                  {vacante.estado === 'PUBLICADA' && (
                    <Tooltip title="Cerrar">
                      <IconButton onClick={() => handleCerrar(vacante.id)}>
                        <CloseIcon />
                      </IconButton>
                    </Tooltip>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
        <form onSubmit={handleSubmit}>
          <DialogTitle>
            {selectedVacante ? 'Editar Vacante' : 'Nueva Vacante'}
          </DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  required
                  label="Cargo"
                  value={formData.cargo}
                  onChange={(e) => setFormData({ ...formData, cargo: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  required
                  label="Categoría Salarial"
                  value={formData.categoriaSalarial}
                  onChange={(e) => setFormData({ ...formData, categoriaSalarial: e.target.value })}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  label="Descripción"
                  multiline
                  rows={3}
                  value={formData.descripcion}
                  onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  label="Perfil"
                  multiline
                  rows={3}
                  value={formData.perfil}
                  onChange={(e) => setFormData({ ...formData, perfil: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  required
                  label="Fecha Límite"
                  type="date"
                  value={formData.fechaLimiteConvocatoria}
                  onChange={(e) => setFormData({ ...formData, fechaLimiteConvocatoria: e.target.value })}
                  InputLabelProps={{ shrink: true }}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <FormControl fullWidth required>
                  <InputLabel>Tipo de Reclutamiento</InputLabel>
                  <Select
                    value={formData.tipoReclutamiento}
                    label="Tipo de Reclutamiento"
                    onChange={(e) => setFormData({ ...formData, tipoReclutamiento: e.target.value })}
                  >
                    {TIPOS_RECLUTAMIENTO.map((tipo) => (
                      <MenuItem key={tipo} value={tipo}>
                        {tipo}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Cancelar</Button>
            <Button type="submit" variant="contained" color="primary">
              {selectedVacante ? 'Actualizar' : 'Crear'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
};

// Componente wrapper con ApolloProvider
const Vacantes = () => {
  return (
    <ApolloProvider client={client}>
      <VacantesComponent />
    </ApolloProvider>
  );
};

export default Vacantes;