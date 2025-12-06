import axios from 'axios';

const API_URL = 'http://localhost:8081/api/productos';

const searchService = {
  /**
   * Búsqueda avanzada con todos los filtros
   */
  buscarProductos: async (params) => {
    try {
      const response = await axios.get(`${API_URL}/buscar`, { params });
      return response.data;
    } catch (error) {
      console.error('Error en búsqueda:', error);
      throw error;
    }
  },

  /**
   * Obtener todas las categorías disponibles
   */
  obtenerCategorias: async () => {
    try {
      const response = await axios.get(`${API_URL}/categorias`);
      return response.data;
    } catch (error) {
      console.error('Error obteniendo categorías:', error);
      throw error;
    }
  },

  /**
   * Obtener todas las marcas disponibles
   */
  obtenerMarcas: async () => {
    try {
      const response = await axios.get(`${API_URL}/marcas`);
      return response.data;
    } catch (error) {
      console.error('Error obteniendo marcas:', error);
      throw error;
    }
  },

  /**
   * Obtener rango de precios (min y max)
   */
  obtenerRangoPrecios: async () => {
    try {
      const response = await axios.get(`${API_URL}/rango-precios`);
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
      const response = await axios.get(`${API_URL}/sugerencias`, {
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