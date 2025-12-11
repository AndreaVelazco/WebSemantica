import axios from 'axios';

const API_URL = 'http://localhost:8081/api/productos';

// Crear instancia de axios sin cache
const axiosInstance = axios.create({
  baseURL: API_URL,
  headers: {
    'Cache-Control': 'no-cache',
    'Pragma': 'no-cache'
  }
});

// Interceptor para agregar token JWT a todas las peticiones
axiosInstance.interceptors.request.use(
  (config) => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user.token) {
      config.headers.Authorization = `${user.type || 'Bearer'} ${user.token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

const searchService = {
  /**
   * Búsqueda avanzada con todos los filtros
   */
  buscarProductos: async (params) => {
    try {
      const response = await axiosInstance.get('/buscar', { params });
      console.log('🔍 Respuesta buscar:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error en búsqueda:', error);
      throw error;
    }
  },
  obtenerMarcas: async () => {
    const response = await fetch('${API_URL}/marcas');
    return response.json();
  },
  obtenerCategorias: async () => {
    const response = await fetch('${API_URL}/categorias');
    return response.json();
  },


  /**
   * Obtener rango de precios (min y max)
   */
  obtenerRangoPrecios: async () => {
    try {
      const response = await axiosInstance.get('/rango-precios');
      console.log('💰 Respuesta rango precios:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error obteniendo rango de precios:', error);
      throw error;
    }
  },

  /**
   * Obtener sugerencias de autocompletado
   */
  obtenerSugerencias: async (query, limite = 5) => {
    try {
      const response = await axiosInstance.get('/sugerencias', {
        params: { q: query, limite }
      });
      return response.data;
    } catch (error) {
      console.error('Error obteniendo sugerencias:', error);
      throw error;
    }
  },
};

export default searchService;