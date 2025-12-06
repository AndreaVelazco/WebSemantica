import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Layout from '../components/Layout/Layout';
import orderService from '../services/orderService';

const OrderDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [pedido, setPedido] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    cargarPedido();
  }, [id]);

  const cargarPedido = async () => {
    setLoading(true);
    setError('');

    try {
      const response = await orderService.obtenerPedido(id);
      if (response.success) {
        setPedido(response.pedido);
      } else {
        setError('Pedido no encontrado');
      }
    } catch (err) {
      console.error('Error cargando pedido:', err);
      setError('Error al cargar el pedido');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelar = async () => {
    if (!window.confirm('¿Estás seguro de cancelar este pedido?')) {
      return;
    }

    try {
      const response = await orderService.cancelarPedido(id);
      if (response.success) {
        alert('✅ Pedido cancelado correctamente');
        navigate('/orders');
      }
    } catch (err) {
      console.error('Error cancelando pedido:', err);
      alert('❌ Error al cancelar el pedido');
    }
  };

  const getEstadoBadge = (estado) => {
    const badges = {
      PENDIENTE: { color: 'bg-yellow-100 text-yellow-700 border-yellow-300', icon: '⏳' },
      PROCESANDO: { color: 'bg-blue-100 text-blue-700 border-blue-300', icon: '📦' },
      ENVIADO: { color: 'bg-purple-100 text-purple-700 border-purple-300', icon: '🚚' },
      ENTREGADO: { color: 'bg-green-100 text-green-700 border-green-300', icon: '✅' },
      CANCELADO: { color: 'bg-red-100 text-red-700 border-red-300', icon: '❌' },
    };
    return badges[estado] || badges.PENDIENTE;
  };

  const formatearFecha = (fecha) => {
    return new Date(fecha).toLocaleDateString('es-ES', {
      day: '2-digit',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  if (loading) {
    return (
      <Layout>
        <div className="text-center py-20">
          <div className="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-600 border-t-transparent mb-4"></div>
          <p className="text-slate-600 text-lg font-semibold">Cargando detalle del pedido...</p>
        </div>
      </Layout>
    );
  }

  if (error || !pedido) {
    return (
      <Layout>
        <div className="max-w-4xl mx-auto">
          <div className="bg-red-50 border border-red-200 rounded-xl p-8 text-center">
            <svg className="w-16 h-16 text-red-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <h2 className="text-2xl font-bold text-red-700 mb-3">Pedido no encontrado</h2>
            <p className="text-red-600 mb-6">{error}</p>
            <button
              onClick={() => navigate('/orders')}
              className="px-6 py-3 bg-red-600 text-white font-semibold rounded-xl hover:bg-red-700 transition"
            >
              Volver a Mis Pedidos
            </button>
          </div>
        </div>
      </Layout>
    );
  }

  const badge = getEstadoBadge(pedido.estado);
  const puedeCancelar = pedido.estado === 'PENDIENTE' || pedido.estado === 'PROCESANDO';

  return (
    <Layout>
      <div className="max-w-5xl mx-auto">
        {/* Breadcrumb */}
        <div className="mb-6">
          <button
            onClick={() => navigate('/orders')}
            className="flex items-center space-x-2 text-purple-600 hover:text-purple-700 font-semibold transition"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7" />
            </svg>
            <span>Volver a Mis Pedidos</span>
          </button>
        </div>

        {/* Header */}
        <div className="bg-white rounded-2xl border border-slate-200 p-8 mb-6">
          <div className="flex items-start justify-between mb-6">
            <div>
              <h1 className="text-3xl font-bold text-slate-800 mb-3">
                Pedido #{pedido.id}
              </h1>
              <div className="flex items-center space-x-4 text-sm text-slate-600">
                <span className="flex items-center space-x-1">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span>{formatearFecha(pedido.fechaPedido)}</span>
                </span>
                <span className={`px-4 py-1.5 rounded-full text-sm font-bold border ${badge.color}`}>
                  {badge.icon} {pedido.estado}
                </span>
              </div>
            </div>
            
            <div className="text-right">
              <div className="text-4xl font-bold text-purple-600 mb-1">
                S/.{pedido.total?.toFixed(2)}
              </div>
              <p className="text-sm text-slate-500">
                {pedido.detalles?.length || 0} producto{pedido.detalles?.length !== 1 ? 's' : ''}
              </p>
            </div>
          </div>

          {/* Timeline */}
          <div className="relative pt-6 border-t border-slate-200">
            <div className="flex items-center justify-between">
              {['PENDIENTE', 'PROCESANDO', 'ENVIADO', 'ENTREGADO'].map((estado, index) => {
                const activo = pedido.estado === estado || 
                  (['PROCESANDO', 'ENVIADO', 'ENTREGADO'].includes(pedido.estado) && estado === 'PENDIENTE') ||
                  (['ENVIADO', 'ENTREGADO'].includes(pedido.estado) && estado === 'PROCESANDO') ||
                  (pedido.estado === 'ENTREGADO' && estado === 'ENVIADO');
                
                return (
                  <div key={estado} className="flex flex-col items-center flex-1">
                    <div className={`w-10 h-10 rounded-full flex items-center justify-center font-bold text-sm mb-2 ${
                      activo ? 'bg-green-500 text-white' : 'bg-slate-200 text-slate-500'
                    }`}>
                      {index + 1}
                    </div>
                    <p className={`text-xs font-semibold ${activo ? 'text-green-600' : 'text-slate-500'}`}>
                      {estado}
                    </p>
                  </div>
                );
              })}
            </div>
            <div className="absolute top-11 left-0 right-0 h-1 bg-slate-200 -z-10" style={{ width: 'calc(100% - 5rem)', marginLeft: '2.5rem' }}>
              <div 
                className="h-full bg-green-500 transition-all duration-500"
                style={{ 
                  width: pedido.estado === 'ENTREGADO' ? '100%' : 
                         pedido.estado === 'ENVIADO' ? '66%' :
                         pedido.estado === 'PROCESANDO' ? '33%' : '0%'
                }}
              ></div>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Columna principal */}
          <div className="lg:col-span-2 space-y-6">
            {/* Productos */}
            <div className="bg-white rounded-2xl border border-slate-200 p-6">
              <h2 className="text-xl font-bold text-slate-800 mb-4">Productos</h2>
              <div className="space-y-3">
                {pedido.detalles?.map((detalle, index) => (
                  <div key={index} className="flex items-center space-x-4 p-4 bg-slate-50 rounded-xl">
                    <div className="w-16 h-16 bg-gradient-to-br from-purple-100 to-blue-100 rounded-xl flex items-center justify-center text-2xl flex-shrink-0">
                      📱
                    </div>
                    <div className="flex-1 min-w-0">
                      <h3 className="font-bold text-slate-800 mb-1">
                        {detalle.productoNombre}
                      </h3>
                      <p className="text-sm text-slate-500">
                        {detalle.productoMarca} • {detalle.productoCategoria}
                      </p>
                    </div>
                    <div className="text-right">
                      <div className="text-sm text-slate-500 mb-1">
                        {detalle.cantidad}x S/.{detalle.precioUnitario?.toFixed(2)}
                      </div>
                      <div className="text-lg font-bold text-purple-600">
                        S/.{detalle.subtotal?.toFixed(2)}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Notas */}
            {pedido.notas && (
              <div className="bg-amber-50 border border-amber-200 rounded-2xl p-6">
                <h3 className="text-lg font-bold text-amber-800 mb-3 flex items-center space-x-2">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                  <span>Notas del pedido</span>
                </h3>
                <p className="text-amber-900">{pedido.notas}</p>
              </div>
            )}
          </div>

          {/* Columna lateral */}
          <div className="lg:col-span-1 space-y-6">
            {/* Dirección */}
            <div className="bg-white rounded-2xl border border-slate-200 p-6">
              <h3 className="text-lg font-bold text-slate-800 mb-4 flex items-center space-x-2">
                <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                <span>Dirección de envío</span>
              </h3>
              <p className="text-slate-700">{pedido.direccionEnvio || 'No especificada'}</p>
            </div>

            {/* Resumen */}
            <div className="bg-white rounded-2xl border border-slate-200 p-6">
              <h3 className="text-lg font-bold text-slate-800 mb-4">Resumen</h3>
              <div className="space-y-3">
                <div className="flex justify-between text-slate-600">
                  <span>Subtotal</span>
                  <span className="font-semibold">S/.{(pedido.total * 0.85).toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-slate-600">
                  <span>Envío</span>
                  <span className="font-semibold">S/.{(pedido.total * 0.03).toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-slate-600">
                  <span>IGV (18%)</span>
                  <span className="font-semibold">S/.{(pedido.total * 0.15).toFixed(2)}</span>
                </div>
                <hr className="border-slate-200" />
                <div className="flex justify-between text-lg font-bold text-slate-800">
                  <span>Total</span>
                  <span className="text-purple-600">S/.{pedido.total?.toFixed(2)}</span>
                </div>
              </div>
            </div>

            {/* Acciones */}
            <div className="space-y-3">
              {puedeCancelar && (
                <button
                  onClick={handleCancelar}
                  className="w-full px-6 py-3 bg-red-50 text-red-600 font-semibold rounded-xl hover:bg-red-100 transition flex items-center justify-center space-x-2"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                  <span>Cancelar Pedido</span>
                </button>
              )}
              
              <button
                onClick={() => window.print()}
                className="w-full px-6 py-3 bg-slate-100 text-slate-700 font-semibold rounded-xl hover:bg-slate-200 transition flex items-center justify-center space-x-2"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                </svg>
                <span>Imprimir</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default OrderDetailPage;