import React, { useEffect, useState } from 'react';
import Layout from '../components/Layout/Layout';
import ProductGrid from '../components/Products/ProductGrid';
import { useAuth } from '../context/AuthContext';
import recommendationService from '../services/recommendationService';

const RecommendationsPage = () => {
  const { user } = useAuth();
  const [recommendations, setRecommendations] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadRecommendations();
  }, [user]);

  const loadRecommendations = async () => {
    if (!user?.clienteIdOntologia) {
      setError('No se pudo cargar tu ID de cliente');
      setLoading(false);
      return;
    }

    try {
      const data = await recommendationService.getRecommendations(user.clienteIdOntologia);
      setRecommendations(data);
      setError('');
    } catch (err) {
      setError('Error al cargar recomendaciones');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="max-w-7xl mx-auto px-6 py-12">
        <h2 className="text-4xl font-bold text-slate-800 mb-2">
          Recomendaciones Personalizadas
        </h2>
        <p className="text-slate-600 mb-8">
          Basadas en tu perfil y preferencias
        </p>

        {loading ? (
          <div className="text-center py-12">
            <div className="text-2xl font-bold gradient-text">Cargando recomendaciones...</div>
          </div>
        ) : error ? (
          <div className="bg-red-100 text-red-700 p-4 rounded-lg">
            {error}
          </div>
        ) : recommendations?.productos?.length > 0 ? (
          <>
            <div className="bg-white rounded-2xl shadow-lg p-6 mb-8">
              <h3 className="text-xl font-bold text-slate-800 mb-2">
                {recommendations.razonRecomendacion}
              </h3>
              <p className="text-slate-600">
                Total de recomendaciones: {recommendations.totalRecomendaciones}
              </p>
            </div>
            <ProductGrid products={recommendations.productos} />
          </>
        ) : (
          <div className="bg-yellow-100 text-yellow-800 p-6 rounded-lg text-center">
            <p className="text-lg font-semibold mb-2">No hay recomendaciones disponibles</p>
            <p>Actualiza tus preferencias o explora nuestro catálogo de productos</p>
          </div>
        )}
      </div>
    </Layout>
  );
};

export default RecommendationsPage;