import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Candidatos from './pages/Candidatos';
import Entrevistas from './pages/Entrevistas';
import Vacantes from './pages/Vacantes';
import Evaluaciones from './pages/Evaluaciones';
import Seleccion from './pages/Seleccion';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
          <Navbar />
          <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/candidatos" element={<Candidatos />} />
              <Route path="/entrevistas" element={<Entrevistas />} />
              <Route path="/vacantes" element={<Vacantes />} />
              <Route path="/evaluaciones" element={<Evaluaciones />} />
              <Route path="/seleccion" element={<Seleccion />} />
            </Routes>
          </Box>
        </Box>
      </Router>
    </ThemeProvider>
  );
}

export default App;
