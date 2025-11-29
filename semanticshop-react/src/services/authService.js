import axios from 'axios';

const API_URL = 'http://localhost:8081/api/auth';

// Configurar interceptor para agregar token a todas las peticiones
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

const authService = {
  // Registro de usuario
  register: async (userData) => {
    const response = await axios.post(`${API_URL}/registro`, userData);
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  },

  // Login
  login: async (credentials) => {
    const response = await axios.post(`${API_URL}/login`, credentials);
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  },

  // Logout
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  // Obtener usuario actual
  getCurrentUser: () => {
    return JSON.parse(localStorage.getItem('user'));
  },

  // Verificar si está autenticado
  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },

  // Obtener perfil
  getProfile: async () => {
    const response = await axios.get(`${API_URL}/perfil`);
    return response.data;
  },

  // Actualizar perfil
  updateProfile: async (userData) => {
    const response = await axios.put(`${API_URL}/perfil`, userData);
    return response.data;
  },
};

export default authService;
