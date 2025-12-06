import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../components/Layout/Layout';
import OrderCard from '../components/orders/OrderCard';
import orderService from '../services/orderService';

const OrdersPage = () => {
  const [pedidos, setPedidos] = useState([]);
  const [estadisticas, setEstadisticas] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('TODOS');

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    setLoading(true);
    setError('');

    try {
      const [pedidosData, statsData] = await Promise.all([
        orderService.obtenerMisPedidos(),
        orderService.obtenerMisEstadisticas(),
      ]);

      if (pedidosData.success) {
        setPedidos(pedidosData.pedidos || []);
      }

      if (statsData.success) {
        setEstadisticas(statsData.estadisticas);
      }
    } catch (err) {
      console.error('Error cargando datos:', err);
      setError('Error al cargar tus pedidos');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelarPedido = async (pedidoId) => {
    if (!window.confirm('¿Estás seguro de cancelar este pedido?')) {
      return;
    }

    try {
      const response = await orderService.cancelarPedido(pedidoId);
      if (response.success) {
        // Recargar datos
        cargarDatos();
        alert('✅ Pedido cancelado correctamente');
      }
    } catch (err) {
      console.error('Error cancelando pedido:', err);
      alert('❌ Error al cancelar el pedido');
    }
  };

  const pedidosFiltrados = filtroEstado === 'TODOS'
    ? pedidos
    : pedidos.filter(p => p.estado === filtroEstado);

  return (
    <Layout>
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-slate-800 mb-2">
            Mis Pedidos
          </h1>
          <p className="text-slate-600">
            Historial y seguimiento de tus compras
          </p>
        </div>

        {/* Estadísticas */}
        {estadisticas && (
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
            <div className="bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl p-6 text-white">
              <div className="text-3xl mb-2">📦</div>
              <div className="text-3xl font-bold mb-1">
                {estadisticas.totalPedidos || 0}
              </div>
              <div className="text-sm text-blue-100">Total Pedidos</div>
            </div>

            <div className="bg-gradient-to-br from-green-500 to-green-600 rounded-2xl p-6 text-white">
              <div className="text-3xl mb-2">✅</div>
              <div className="text-3xl font-bold mb-1">
                {estadisticas.pedidosEntregados || 0}
              </div>
              <div className="text-sm text-green-100">Entregados</div>
            </div>

            <div className="bg-gradient-to-br from-purple-500 to-purple-600 rounded-2xl p-6 text-white">
              <div className="text-3xl mb-2">🚚</div>
              <div className="text-3xl font-bold mb-1">
                {estadisticas.pedidosEnviados || 0}
              </div>
              <div className="text-sm text-purple-100">En Camino</div>
            </div>

            <div className="bg-gradient-to-br from-amber-500 to-amber-600 rounded-2xl p-6 text-white">
              <div className="text-3xl mb-2">💰</div>
              <div className="text-3xl font-bold mb-1">
                S/.{estadisticas.totalGastado?.toFixed(2) || '0.00'}
              </div>
              <div className="text-sm text-amber-100">Total Gastado</div>
            </div>
          </div>
        )}

        {/* Filtros */}
        <div className="bg-white rounded-2xl border border-slate-200 p-4 mb-6">
          <div className="flex items-center justify-between flex-wrap gap-4">
            <div className="flex items-center space-x-2">
              <span className="text-sm font-semibold text-slate-700">Filtrar por estado:</span>
              <select
                value={filtroEstado}
                onChange={(e) => setFiltroEstado(e.target.value)}
                className="px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
              >
                <option value="TODOS">Todos ({pedidos.length})</option>
                <option value="PENDIENTE">
                  Pendientes ({pedidos.filter(p => p.estado === 'PENDIENTE').length})
                </option>
                <option value="PROCESANDO">
                  Procesando ({pedidos.filter(p => p.estado === 'PROCESANDO').length})
                </option>
                <option value="ENVIADO">
                  Enviados ({pedidos.filter(p => p.estado === 'ENVIADO').length})
                </option>
                <option value="ENTREGADO">
                  Entregados ({pedidos.filter(p => p.estado === 'ENTREGADO').length})
                </option>
                <option value="CANCELADO">
                  Cancelados ({pedidos.filter(p => p.estado === 'CANCELADO').length})
                </option>
              </select>
            </div>

            <button
              onClick={cargarDatos}
              className="px-4 py-2 bg-purple-50 text-purple-600 font-semibold rounded-xl hover:bg-purple-100 transition flex items-center space-x-2"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              <span>Actualizar</span>
            </button>
          </div>
        </div>

        {/* Loading State */}
        {loading && (
          <div className="text-center py-20">
            <div className="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-600 border-t-transparent mb-4"></div>
            <p className="text-slate-600 text-lg font-semibold">Cargando pedidos...</p>
          </div>
        )}

        {/* Error State */}
        {error && (
          <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-center">
            <svg className="w-12 h-12 text-red-500 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <p className="text-red-700 font-semibold mb-2">Error al cargar pedidos</p>
            <p className="text-red-600 text-sm">{error}</p>
            <button
              onClick={cargarDatos}
              className="mt-4 px-6 py-2 bg-red-600 text-white font-semibold rounded-xl hover:bg-red-700 transition"
            >
              Reintentar
            </button>
          </div>
        )}

        {/* Lista de Pedidos */}
        {!loading && !error && (
          <>
            {pedidosFiltrados.length > 0 ? (
              <div className="grid grid-cols-1 gap-6">
                {pedidosFiltrados.map((pedido) => (
                  <OrderCard
                    key={pedido.id}
                    pedido={pedido}
                    onCancelar={handleCancelarPedido}
                  />
                ))}
              </div>
            ) : (
              <div className="text-center py-20">
                <div className="w-32 h-32 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-6">
                  <svg className="w-16 h-16 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
                  </svg>
                </div>
                <h2 className="text-2xl font-bold text-slate-800 mb-3">
                  {filtroEstado === 'TODOS' ? 'No tienes pedidos aún' : `No hay pedidos ${filtroEstado.toLowerCase()}`}
                </h2>
                <p className="text-slate-600 mb-8">
                  {filtroEstado === 'TODOS' 
                    ? '¡Comienza a comprar y haz tu primer pedido!'
                    : 'Cambia el filtro para ver otros pedidos'
                  }
                </p>
                {filtroEstado === 'TODOS' && (
                  <Link
                    to="/products"
                    className="inline-block px-8 py-3 bg-gradient-to-r from-purple-600 to-blue-500 text-white font-semibold rounded-xl hover:shadow-lg transition"
                  >
                    Explorar Productos
                  </Link>
                )}
              </div>
            )}
          </>
        )}
      </div>
    </Layout>
  );
};

export default OrdersPage;