import axios from 'axios';

const API_URL = 'http://localhost:8081/api';

const productService = {
  // Obtener todos los productos
  getAllProducts: async () => {
    const response = await axios.get(`${API_URL}/productos`);
    return response.data;
  },

  // Obtener producto por ID
  getProductById: async (id) => {
    const response = await axios.get(`${API_URL}/productos/${id}`);
    return response.data;
  },

  // Obtener productos por categoría
  getProductsByCategory: async (category) => {
    const response = await axios.get(`${API_URL}/productos/categoria/${category}`);
    return response.data;
  },

  // Buscar productos
  searchProducts: async (query) => {
    const response = await axios.get(`${API_URL}/productos/buscar?q=${query}`);
    return response.data;
  },
};

export default productService;
