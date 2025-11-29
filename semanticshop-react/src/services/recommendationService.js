import axios from 'axios';

const API_URL = 'http://localhost:8081/api';

const recommendationService = {
  // Obtener recomendaciones para el cliente actual
  getRecommendations: async (clienteId) => {
    const response = await axios.get(`${API_URL}/recomendaciones/cliente/${clienteId}`);
    return response.data;
  },

  // Obtener todos los clientes
  getAllClientes: async () => {
    const response = await axios.get(`${API_URL}/recomendaciones/clientes`);
    return response.data;
  },

  // Obtener info de un cliente
  getClienteInfo: async (clienteId) => {
    const response = await axios.get(`${API_URL}/recomendaciones/clientes/${clienteId}`);
    return response.data;
  },

  // Recomendar accesorios compatibles
  getCompatibleAccessories: async (productoId) => {
    const response = await axios.get(`${API_URL}/recomendaciones/accesorios/${productoId}`);
    return response.data;
  },

  // Recomendar por historial
  getHistoryRecommendations: async (clienteId) => {
    const response = await axios.get(`${API_URL}/recomendaciones/historial/${clienteId}`);
    return response.data;
  },
};

export default recommendationService;
