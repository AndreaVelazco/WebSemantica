import axios from 'axios';

const API_URL = 'http://localhost:8081/api';

const orderService = {
  /**
   * Crear pedido desde el carrito
   */
  crearPedido: async (pedidoData) => {
    try {
      const response = await axios.post(`${API_URL}/pedidos/crear`, pedidoData);
      return response.data;
    } catch (error) {
      console.error('Error creando pedido:', error);
      throw error;
    }
  },

  /**
   * Obtener mis pedidos (historial del usuario)
   */
  obtenerMisPedidos: async () => {
    try {
      const response = await axios.get(`${API_URL}/pedidos/mis-pedidos`);
      return response.data;
    } catch (error) {
      console.error('Error obteniendo pedidos:', error);
      throw error;
    }
  },

  /**
   * Obtener detalle de un pedido específico
   */
  obtenerPedido: async (pedidoId) => {
    try {
      const response = await axios.get(`${API_URL}/pedidos/${pedidoId}`);
      return response.data;
    } catch (error) {
      console.error('Error obteniendo pedido:', error);
      throw error;
    }
  },

  /**
   * Cancelar un pedido
   */
  cancelarPedido: async (pedidoId) => {
    try {
      const response = await axios.put(`${API_URL}/pedidos/${pedidoId}/cancelar`);
      return response.data;
    } catch (error) {
      console.error('Error cancelando pedido:', error);
      throw error;
    }
  },

  /**
   * Obtener estadísticas del usuario
   */
  obtenerMisEstadisticas: async () => {
    try {
      const response = await axios.get(`${API_URL}/pedidos/mis-estadisticas`);
      return response.data;
    } catch (error) {
      console.error('Error obteniendo estadísticas:', error);
      throw error;
    }
  },

  /**
   * Obtener todos los pedidos (admin)
   */
  obtenerTodosPedidos: async () => {
    try {
      const response = await axios.get(`${API_URL}/pedidos`);
      return response.data;
    } catch (error) {
      console.error('Error obteniendo todos los pedidos:', error);
      throw error;
    }
  },

  /**
   * Actualizar estado de pedido (admin)
   */
  actualizarEstado: async (pedidoId, nuevoEstado) => {
    try {
      const response = await axios.put(`${API_URL}/pedidos/${pedidoId}/estado`, {
        nuevoEstado
      });
      return response.data;
    } catch (error) {
      console.error('Error actualizando estado:', error);
      throw error;
    }
  },
};

export default orderService;