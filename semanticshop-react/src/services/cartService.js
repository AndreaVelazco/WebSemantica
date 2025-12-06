import axios from 'axios';

const API_URL = 'http://localhost:8081/api/carrito';

const cartService = {
  /**
   * Agregar producto al carrito (backend)
   */
  agregarProducto: async (productoId, cantidad = 1) => {
    try {
      const response = await axios.post(`${API_URL}/agregar`, {
        productoId,
        cantidad,
      });
      return response.data;
    } catch (error) {
      console.error('Error agregando producto:', error);
      throw error;
    }
  },

  /**
   * Obtener carrito del backend
   */
  obtenerCarrito: async () => {
    try {
      const response = await axios.get(API_URL);
      return response.data;
    } catch (error) {
      console.error('Error obteniendo carrito:', error);
      throw error;
    }
  },

  /**
   * Actualizar cantidad de un producto
   */
  actualizarCantidad: async (productoId, cantidad) => {
    try {
      const response = await axios.put(`${API_URL}/actualizar/${productoId}`, {
        cantidad,
      });
      return response.data;
    } catch (error) {
      console.error('Error actualizando cantidad:', error);
      throw error;
    }
  },

  /**
   * Eliminar producto del carrito
   */
  eliminarProducto: async (productoId) => {
    try {
      const response = await axios.delete(`${API_URL}/eliminar/${productoId}`);
      return response.data;
    } catch (error) {
      console.error('Error eliminando producto:', error);
      throw error;
    }
  },

  /**
   * Limpiar todo el carrito
   */
  limpiarCarrito: async () => {
    try {
      const response = await axios.delete(`${API_URL}/limpiar`);
      return response.data;
    } catch (error) {
      console.error('Error limpiando carrito:', error);
      throw error;
    }
  },

  /**
   * Sincronizar carrito local con backend
   * (envía todos los items del localStorage al backend)
   */
  sincronizarCarrito: async (cartItems) => {
    try {
      // Primero limpiar el carrito en el backend
      await cartService.limpiarCarrito();

      // Luego agregar cada producto
      const promises = cartItems.map(item =>
        cartService.agregarProducto(item.id, item.cantidad)
      );

      await Promise.all(promises);
      
      return { success: true };
    } catch (error) {
      console.error('Error sincronizando carrito:', error);
      throw error;
    }
  },
};

export default cartService;