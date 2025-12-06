import React from 'react';
import { useNavigate } from 'react-router-dom';

const OrderCard = ({ pedido, onCancelar }) => {
  const navigate = useNavigate();

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

  const badge = getEstadoBadge(pedido.estado);

  const formatearFecha = (fecha) => {
    return new Date(fecha).toLocaleDateString('es-ES', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const puedeCancelar = pedido.estado === 'PENDIENTE' || pedido.estado === 'PROCESANDO';

  return (
    <div className="bg-white rounded-2xl border border-slate-200 p-6 hover:shadow-lg transition-all duration-300">
      {/* Header */}
      <div className="flex items-start justify-between mb-4">
        <div>
          <div className="flex items-center space-x-3 mb-2">
            <h3 className="text-lg font-bold text-slate-800">
              Pedido #{pedido.id}
            </h3>
            <span className={`px-3 py-1 rounded-full text-xs font-bold border ${badge.color}`}>
              {badge.icon} {pedido.estado}
            </span>
          </div>
          <p className="text-sm text-slate-500">
            📅 {formatearFecha(pedido.fechaPedido)}
          </p>
        </div>
        
        <div className="text-right">
          <div className="text-2xl font-bold text-purple-600">
            S/.{pedido.total?.toFixed(2)}
          </div>
          <p className="text-xs text-slate-500">
            {pedido.detalles?.length || 0} producto{pedido.detalles?.length !== 1 ? 's' : ''}
          </p>
        </div>
      </div>

      {/* Productos preview */}
      <div className="mb-4">
        <div className="space-y-2">
          {pedido.detalles?.slice(0, 2).map((detalle, index) => (
            <div key={index} className="flex items-center space-x-3 p-2 bg-slate-50 rounded-lg">
              <div className="w-10 h-10 bg-gradient-to-br from-purple-100 to-blue-100 rounded-lg flex items-center justify-center text-xl">
                📱
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-semibold text-slate-800 truncate">
                  {detalle.productoNombre}
                </p>
                <p className="text-xs text-slate-500">
                  {detalle.cantidad}x S/.{detalle.precioUnitario?.toFixed(2)}
                </p>
              </div>
              <div className="text-sm font-bold text-slate-700">
                S/.{detalle.subtotal?.toFixed(2)}
              </div>
            </div>
          ))}
          {pedido.detalles?.length > 2 && (
            <p className="text-xs text-slate-500 text-center py-1">
              + {pedido.detalles.length - 2} producto{pedido.detalles.length - 2 !== 1 ? 's' : ''} más
            </p>
          )}
        </div>
      </div>

      {/* Dirección de envío */}
      {pedido.direccionEnvio && (
        <div className="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-xl">
          <div className="flex items-start space-x-2">
            <svg className="w-5 h-5 text-blue-600 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            <div>
              <p className="text-xs font-semibold text-blue-700 mb-1">Dirección de envío</p>
              <p className="text-sm text-blue-800">{pedido.direccionEnvio}</p>
            </div>
          </div>
        </div>
      )}

      {/* Notas */}
      {pedido.notas && (
        <div className="mb-4 p-3 bg-amber-50 border border-amber-200 rounded-xl">
          <p className="text-xs font-semibold text-amber-700 mb-1">📝 Notas</p>
          <p className="text-sm text-amber-800">{pedido.notas}</p>
        </div>
      )}

      {/* Actions */}
      <div className="flex items-center space-x-3 pt-4 border-t border-slate-200">
        <button
          onClick={() => navigate(`/orders/${pedido.id}`)}
          className="flex-1 px-4 py-2.5 bg-slate-100 text-slate-700 font-semibold rounded-xl hover:bg-slate-200 transition flex items-center justify-center space-x-2"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
          </svg>
          <span>Ver Detalle</span>
        </button>

        {puedeCancelar && onCancelar && (
          <button
            onClick={() => onCancelar(pedido.id)}
            className="px-4 py-2.5 bg-red-50 text-red-600 font-semibold rounded-xl hover:bg-red-100 transition flex items-center space-x-2"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
            <span>Cancelar</span>
          </button>
        )}
      </div>

      {/* Timeline visual */}
      <div className="mt-4 pt-4 border-t border-slate-200">
        <div className="flex items-center justify-between text-xs">
          <div className={`flex items-center space-x-1 ${['PENDIENTE', 'PROCESANDO', 'ENVIADO', 'ENTREGADO'].includes(pedido.estado) ? 'text-green-600 font-semibold' : 'text-slate-400'}`}>
            <div className={`w-2 h-2 rounded-full ${['PENDIENTE', 'PROCESANDO', 'ENVIADO', 'ENTREGADO'].includes(pedido.estado) ? 'bg-green-600' : 'bg-slate-300'}`}></div>
            <span>Pendiente</span>
          </div>
          <div className={`flex-1 h-0.5 mx-2 ${['PROCESANDO', 'ENVIADO', 'ENTREGADO'].includes(pedido.estado) ? 'bg-green-600' : 'bg-slate-300'}`}></div>
          <div className={`flex items-center space-x-1 ${['PROCESANDO', 'ENVIADO', 'ENTREGADO'].includes(pedido.estado) ? 'text-green-600 font-semibold' : 'text-slate-400'}`}>
            <div className={`w-2 h-2 rounded-full ${['PROCESANDO', 'ENVIADO', 'ENTREGADO'].includes(pedido.estado) ? 'bg-green-600' : 'bg-slate-300'}`}></div>
            <span>Procesando</span>
          </div>
          <div className={`flex-1 h-0.5 mx-2 ${['ENVIADO', 'ENTREGADO'].includes(pedido.estado) ? 'bg-green-600' : 'bg-slate-300'}`}></div>
          <div className={`flex items-center space-x-1 ${['ENVIADO', 'ENTREGADO'].includes(pedido.estado) ? 'text-green-600 font-semibold' : 'text-slate-400'}`}>
            <div className={`w-2 h-2 rounded-full ${['ENVIADO', 'ENTREGADO'].includes(pedido.estado) ? 'bg-green-600' : 'bg-slate-300'}`}></div>
            <span>Enviado</span>
          </div>
          <div className={`flex-1 h-0.5 mx-2 ${pedido.estado === 'ENTREGADO' ? 'bg-green-600' : 'bg-slate-300'}`}></div>
          <div className={`flex items-center space-x-1 ${pedido.estado === 'ENTREGADO' ? 'text-green-600 font-semibold' : 'text-slate-400'}`}>
            <div className={`w-2 h-2 rounded-full ${pedido.estado === 'ENTREGADO' ? 'bg-green-600' : 'bg-slate-300'}`}></div>
            <span>Entregado</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderCard;