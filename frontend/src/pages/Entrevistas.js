import React, { useState } from 'react';
import {
  Container,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  MenuItem,
  CircularProgress,
  Box,
} from '@mui/material';
import { Edit as EditIcon, Add as AddIcon } from '@mui/icons-material';
import { ApolloClient, InMemoryCache, ApolloProvider, useQuery, useMutation, gql, createHttpLink } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';

// Apollo Client Configuration
const httpLink = createHttpLink({
  uri: 'http://localhost:8090/entrevistas/graphql',
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

// GraphQL Queries
const GET_ENTREVISTAS = gql`
  query GetEntrevistas {
    entrevistas {
      id
      candidatoId
      vacanteId
      fechaEntrevista
      entrevistador
      tipo
      estado
      calificacion
      aprobada
      estadoDecision
      comentarios
      comentariosDecision
      areaAprobadora
    }
  }
`;

const GET_CANDIDATOS = gql`
  query GetCandidatos {
    candidatos {
      id
      nombre
      apellido
    }
  }
`;

const GET_VACANTES = gql`
  query GetVacantes {
    requisiciones {
      id
      cargo
    }
  }
`;

// GraphQL Mutations
const AGENDAR_ENTREVISTA = gql`
  mutation AgendarEntrevista($input: EntrevistaInput!) {
    agendarEntrevista(input: $input) {
      id
      candidatoId
      vacanteId
      fechaEntrevista
      entrevistador
      tipo
      estado
    }
  }
`;

const REGISTRAR_CALIFICACION = gql`
  mutation RegistrarCalificacion($input: CalificacionInput!) {
    registrarCalificacion(input: $input) {
      id
      calificacion
      aprobada
      comentarios
    }
  }
`;

const ACTUALIZAR_ESTADO_DECISION = gql`
  mutation ActualizarEstadoDecision($input: DecisionInput!) {
    actualizarEstadoDecision(input: $input) {
      id
      estadoDecision
      comentariosDecision
      areaAprobadora
    }
  }
`;

const EntrevistasComponent = () => {
  const [open, setOpen] = useState(false);
  const [selectedEntrevista, setSelectedEntrevista] = useState(null);
  const [formData, setFormData] = useState({
    candidatoId: '',
    vacanteId: '',
    fechaEntrevista: '',
    entrevistador: '',
    tipo: 'PSICOLOGICA',
    comentarios: ''
  });

  // Queries
  const { loading, error, data, refetch } = useQuery(GET_ENTREVISTAS);
  const { data: candidatosData } = useQuery(GET_CANDIDATOS);
  const { data: vacantesData } = useQuery(GET_VACANTES);

  // Mutations
  const [agendarEntrevista] = useMutation(AGENDAR_ENTREVISTA, {
    onCompleted: () => {
      handleClose();
      refetch();
    }
  });

  const [registrarCalificacion] = useMutation(REGISTRAR_CALIFICACION);
  const [actualizarEstadoDecision] = useMutation(ACTUALIZAR_ESTADO_DECISION);

  const handleClickOpen = (entrevista = null) => {
    if (entrevista) {
      setSelectedEntrevista(entrevista);
      setFormData({
        candidatoId: entrevista.candidatoId,
        vacanteId: entrevista.vacanteId,
        fechaEntrevista: entrevista.fechaEntrevista,
        entrevistador: entrevista.entrevistador,
        tipo: entrevista.tipo,
        comentarios: entrevista.comentarios || '',
      });
    } else {
      setSelectedEntrevista(null);
      setFormData({
        candidatoId: '',
        vacanteId: '',
        fechaEntrevista: '',
        entrevistador: '',
        tipo: 'PSICOLOGICA',
        comentarios: '',
      });
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedEntrevista(null);
  };

  const handleSubmit = async () => {
    try {
      await agendarEntrevista({
        variables: {
          input: {
            ...formData,
            candidatoId: parseInt(formData.candidatoId),
            vacanteId: parseInt(formData.vacanteId)
          }
        }
      });
    } catch (error) {
      console.error('Error al agendar la entrevista:', error);
      alert('Error al agendar la entrevista');
    }
  };

  const handleCalificacion = async (entrevistaId, calificacion, aprobada, comentarios) => {
    try {
      await registrarCalificacion({
        variables: {
          input: {
            entrevistaId,
            calificacion,
            aprobada,
            comentarios
          }
        }
      });
      refetch();
    } catch (error) {
      console.error('Error al registrar calificación:', error);
    }
  };

  const handleDecision = async (entrevistaId, estadoDecision, comentarios, areaAprobadora) => {
    try {
      await actualizarEstadoDecision({
        variables: {
          input: {
            entrevistaId,
            estadoDecision,
            comentarios,
            areaAprobadora
          }
        }
      });
      refetch();
    } catch (error) {
      console.error('Error al actualizar decisión:', error);
    }
  };

  if (loading) return <CircularProgress />;
  if (error) return <Typography color="error">Error: {error.message}</Typography>;

  return (
    <Container maxWidth="lg">
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h4">Gestión de Entrevistas</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleClickOpen()}
        >
          Nueva Entrevista
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Candidato</TableCell>
              <TableCell>Vacante</TableCell>
              <TableCell>Tipo</TableCell>
              <TableCell>Fecha</TableCell>
              <TableCell>Estado</TableCell>
              <TableCell>Calificación</TableCell>
              <TableCell>Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {data?.entrevistas.map((entrevista) => (
              <TableRow key={entrevista.id}>
                <TableCell>{entrevista.candidatoId}</TableCell>
                <TableCell>{entrevista.vacanteId}</TableCell>
                <TableCell>{entrevista.tipo}</TableCell>
                <TableCell>{new Date(entrevista.fechaEntrevista).toLocaleString()}</TableCell>
                <TableCell>{entrevista.estado}</TableCell>
                <TableCell>{entrevista.calificacion || 'Pendiente'}</TableCell>
                <TableCell>
                  <IconButton onClick={() => handleClickOpen(entrevista)}>
                    <EditIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
        <DialogTitle>
          {selectedEntrevista ? 'Editar Entrevista' : 'Nueva Entrevista'}
        </DialogTitle>
        <DialogContent>
          <TextField
            select
            margin="dense"
            label="Candidato"
            fullWidth
            name="candidatoId"
            value={formData.candidatoId}
            onChange={(e) => setFormData({ ...formData, candidatoId: e.target.value })}
          >
            {candidatosData?.candidatos.map((candidato) => (
              <MenuItem key={candidato.id} value={candidato.id}>
                {`${candidato.nombre} ${candidato.apellido}`}
              </MenuItem>
            ))}
          </TextField>

          <TextField
            select
            margin="dense"
            label="Vacante"
            fullWidth
            name="vacanteId"
            value={formData.vacanteId}
            onChange={(e) => setFormData({ ...formData, vacanteId: e.target.value })}
          >
            {vacantesData?.requisiciones.map((vacante) => (
              <MenuItem key={vacante.id} value={vacante.id}>
                {vacante.cargo}
              </MenuItem>
            ))}
          </TextField>

          <TextField
            margin="dense"
            label="Fecha de Entrevista"
            type="datetime-local"
            fullWidth
            name="fechaEntrevista"
            value={formData.fechaEntrevista}
            onChange={(e) => setFormData({ ...formData, fechaEntrevista: e.target.value })}
            InputLabelProps={{
              shrink: true,
            }}
          />

          <TextField
            margin="dense"
            label="Entrevistador"
            fullWidth
            name="entrevistador"
            value={formData.entrevistador}
            onChange={(e) => setFormData({ ...formData, entrevistador: e.target.value })}
          />

          <TextField
            select
            margin="dense"
            label="Tipo de Entrevista"
            fullWidth
            name="tipo"
            value={formData.tipo}
            onChange={(e) => setFormData({ ...formData, tipo: e.target.value })}
          >
            <MenuItem value="PSICOLOGICA">Psicológica</MenuItem>
            <MenuItem value="TECNICA">Técnica</MenuItem>
            <MenuItem value="DIRECTOR_RRHH">Director RRHH</MenuItem>
          </TextField>

          <TextField
            margin="dense"
            label="Comentarios"
            fullWidth
            multiline
            rows={4}
            name="comentarios"
            value={formData.comentarios}
            onChange={(e) => setFormData({ ...formData, comentarios: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancelar</Button>
          <Button onClick={handleSubmit} color="primary">
            {selectedEntrevista ? 'Actualizar' : 'Guardar'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

const Entrevistas = () => {
  return (
    <ApolloProvider client={client}>
      <EntrevistasComponent />
    </ApolloProvider>
  );
};

export default Entrevistas;