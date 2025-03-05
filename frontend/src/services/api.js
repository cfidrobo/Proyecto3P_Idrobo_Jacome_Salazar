import { ApolloClient, InMemoryCache, gql, createHttpLink } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';

const httpLink = createHttpLink({
  uri: 'http://localhost:8090/graphql',
  credentials: 'include'
});

const authLink = setContext((_, { headers }) => {
  return {
    headers: {
      ...headers,
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
      fetchPolicy: 'network-only',
      errorPolicy: 'all'
    },
    query: {
      fetchPolicy: 'network-only',
      errorPolicy: 'all'
    }
  }
});

// Queries
export const GET_VACANTES = gql`
  query GetRequisiciones {
    getRequisiciones {
      id
      cargo
      descripcion
      categoriaSalarial
      perfil
      fechaCreacion
      fechaLimiteConvocatoria
      tipoReclutamiento
      estado
      aprobadoPorRRHH
    }
  }
`;

export const GET_CANDIDATOS = gql`
  query GetCandidatos {
    getCandidatos {
      id
      nombre
      apellido
      email
      telefono
      estado
    }
  }
`;

export const GET_ENTREVISTAS = gql`
  query GetEntrevistas {
    getEntrevistas {
      id
      candidatoId
      requisicionId
      fecha
      estado
      comentarios
    }
  }
`;

export const GET_SELECCIONES = gql`
  query GetSelecciones {
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

// Mutations
export const CREATE_VACANTE = gql`
  mutation CreateRequisicion($input: RequisicionInput!) {
    createRequisicion(requisicion: $input) {
      id
      cargo
      descripcion
      categoriaSalarial
      perfil
      fechaCreacion
      fechaLimiteConvocatoria
      tipoReclutamiento
      estado
      aprobadoPorRRHH
    }
  }
`;

export const CREATE_ENTREVISTA = gql`
  mutation CreateEntrevista($input: EntrevistaInput!) {
    createEntrevista(entrevista: $input) {
      id
      candidatoId
      requisicionId
      fecha
      estado
      comentarios
    }
  }
`;

export const CREATE_SELECCION = gql`
  mutation CreateSeleccion($input: SeleccionInput!) {
    createSeleccion(seleccion: $input) {
      id
      candidatoId
      requisicionId
      fecha
      estado
      comentarios
    }
  }
`;

export default client; 