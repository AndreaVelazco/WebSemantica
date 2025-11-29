import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../components/Layout/Layout';
import ProductGrid from '../components/Products/ProductGrid';
import { useAuth } from '../context/AuthContext';
import productService from '../services/productService';

const DashboardPage = () => {
  const { user } = useAuth();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const data = await productService.getAllProducts();
      setProducts(data.slice(0, 8));
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="max-w-7xl mx-auto px-6 py-12">
        {/* Welcome */}
        <div className="mb-12">
          <h2 className="text-4xl font-bold text-slate-800 mb-2">
            ¡Hola, {user?.nombreCompleto || user?.username}!
          </h2>
          <p className="text-slate-600 mb-8">
            Descubre productos especialmente para ti!
          </p>

          <div className="bg-white rounded-2xl shadow-lg p-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="text-center">
                <div className="text-3xl font-bold text-purple-600">{user?.marcaPreferida || 'N/A'}</div>
                <div className="text-sm text-slate-600">Marca Preferida</div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold text-purple-600">{user?.sistemaOperativoPreferido || 'N/A'}</div>
                <div className="text-sm text-slate-600">Sistema Operativo</div>
              </div>
              <div className="text-center">
                <Link to="/recommendations" className="btn-primary inline-block">
                  Ver Recomendaciones
                </Link>
              </div>
            </div>
          </div>
        </div>

        {/* Products */}
        {loading ? (
          <div className="text-center py-12">
            <div className="text-2xl font-bold gradient-text">Cargando...</div>
          </div>
        ) : (
          <ProductGrid products={products} title="Productos Disponibles" />
        )}
      </div>
    </Layout>
  );
};

export default DashboardPage;