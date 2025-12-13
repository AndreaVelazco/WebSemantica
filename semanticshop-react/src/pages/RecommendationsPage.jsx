import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
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
    setLoading(true);
    setError('');

    // Verificar que el usuario tenga clienteIdOntologia
    if (!user?.clienteIdOntologia) {
      console.error('❌ Usuario sin clienteIdOntologia:', user);
      setError('No se encontró tu perfil de recomendaciones');
      setLoading(false);
      return;
    }

    console.log('Cargando recomendaciones para:', user.clienteIdOntologia);

    try {
      const data = await recommendationService.getRecommendations(user.clienteIdOntologia);
      console.log('✅ Recomendaciones recibidas:', data);
      
      setRecommendations(data);
      setError('');
    } catch (err) {
      console.error('❌ Error cargando recomendaciones:', err);
      setError(err.response?.data?.message || 'Error al cargar recomendaciones');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-slate-800 mb-2">
            Recomendaciones Personalizadas
          </h1>
          <p className="text-slate-600">
            Productos seleccionados especialmente para ti 
          </p>
        </div>

        {/* User Info Card */}
        {user && (
          <div className="bg-gradient-to-r from-purple-500 to-blue-500 rounded-2xl p-6 mb-8 text-white">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div>
                <div className="text-sm text-white/80 mb-1">Tu perfil</div>
                <div className="text-xl font-bold">{user.nombreCompleto || user.username}</div>
              </div>
              <div>
                <div className="text-sm text-white/80 mb-1">Marca preferida</div>
                <div className="text-xl font-bold">{user.marcaPreferida || 'No especificada'}</div>
              </div>
              <div>
                <div className="text-sm text-white/80 mb-1">Sistema operativo</div>
                <div className="text-xl font-bold">{user.sistemaOperativoPreferido || 'No especificado'}</div>
              </div>
            </div>
          </div>
        )}

        {/* Loading State */}
        {loading && (
          <div className="text-center py-20">
            <div className="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-600 border-t-transparent mb-4"></div>
            <p className="text-slate-600 text-lg font-semibold">
              Generando recomendaciones personalizadas...
            </p>
            <p className="text-sm text-slate-500 mt-2">
              Esto puede tomar unos segundos
            </p>
          </div>
        )}

        {/* Error State */}
        {!loading && error && (
          <div className="bg-red-50 border border-red-200 rounded-xl p-8 text-center">
            <svg className="w-16 h-16 text-red-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <h3 className="text-xl font-bold text-red-700 mb-2">Error al cargar recomendaciones</h3>
            <p className="text-red-600 mb-6">{error}</p>
            <div className="flex items-center justify-center space-x-4">
              <button
                onClick={loadRecommendations}
                className="px-6 py-3 bg-red-600 text-white font-semibold rounded-xl hover:bg-red-700 transition"
              >
                Reintentar
              </button>
              <Link
                to="/products"
                className="px-6 py-3 bg-slate-200 text-slate-700 font-semibold rounded-xl hover:bg-slate-300 transition"
              >
                Ver Todos los Productos
              </Link>
            </div>
          </div>
        )}

        {/* Success State with Recommendations */}
        {!loading && !error && recommendations && recommendations.productos && recommendations.productos.length > 0 && (
          <>
            {/* Reason Card */}
            <div className="bg-white rounded-2xl border border-slate-200 p-6 mb-8">
              <div className="flex items-start space-x-4">
                <div className="w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center flex-shrink-0">
                  <svg className="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
                  </svg>
                </div>
                <div className="flex-1">
                  <h3 className="text-lg font-bold text-slate-800 mb-2">
                    ¿Por qué estas recomendaciones?
                  </h3>
                  <p className="text-slate-600">
                    {recommendations.razonRecomendacion || 'Productos seleccionados según tus preferencias'}
                  </p>
                  <div className="mt-3 flex items-center space-x-4 text-sm text-slate-500">
                    <span>📊 {recommendations.totalRecomendaciones || recommendations.productos.length} productos</span>
                    <span>🎯 Basado en tu perfil</span>
                  </div>
                </div>
                <button
                  onClick={loadRecommendations}
                  className="px-4 py-2 bg-purple-50 text-purple-600 font-semibold rounded-xl hover:bg-purple-100 transition flex items-center space-x-2"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                  </svg>
                  <span>Actualizar</span>
                </button>
              </div>
            </div>

            {/* Products Grid */}
            <ProductGrid products={recommendations.productos} />
          </>
        )}

        {/* Empty State */}
        {!loading && !error && (!recommendations || !recommendations.productos || recommendations.productos.length === 0) && (
          <div className="text-center py-20">
            <div className="w-32 h-32 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <svg className="w-16 h-16 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-slate-800 mb-3">
              No hay recomendaciones disponibles
            </h2>
            <p className="text-slate-600 mb-8">
              Actualiza tus preferencias o explora nuestro catálogo de productos
            </p>
            <div className="flex items-center justify-center space-x-4">
              <button
                onClick={loadRecommendations}
                className="px-6 py-3 bg-purple-600 text-white font-semibold rounded-xl hover:bg-purple-700 transition"
              >
                Reintentar
              </button>
              <Link
                to="/products"
                className="px-6 py-3 bg-slate-200 text-slate-700 font-semibold rounded-xl hover:bg-slate-300 transition"
              >
                Ver Todos los Productos
              </Link>
            </div>
          </div>
        )}
      </div>
    </Layout>
  );
};

export default RecommendationsPage;