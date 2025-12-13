import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../context/CartContext';
import orderService from '../../services/orderService';
import cartService from '../../services/cartService';

const CheckoutModal = ({ isOpen, onClose }) => {
  const navigate = useNavigate();
  const { cartItems, getTotal, clearCart } = useCart();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    direccionEnvio: '',
    notas: '',
  });
  
  // 🆕 Estado para manejar errores de imagen
  const [imageErrors, setImageErrors] = useState({});

  if (!isOpen) return null;

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.direccionEnvio.trim()) {
      alert('❌ Por favor ingresa una dirección de envío');
      return;
    }

    if (cartItems.length === 0) {
      alert('❌ El carrito está vacío');
      return;
    }

    setLoading(true);

    try {
      // PASO 1: Sincronizar carrito con backend
      console.log('📤 Sincronizando carrito con backend...');
      await cartService.sincronizarCarrito(cartItems);
      
      // PASO 2: Crear pedido
      console.log('📦 Creando pedido...');
      const response = await orderService.crearPedido(formData);
      
      if (response.success) {
        clearCart();
        alert('✅ ¡Pedido creado exitosamente!');
        onClose();
        navigate('/orders');
      }
    } catch (err) {
      console.error('Error creando pedido:', err);
      
      // Mensaje de error más específico
      const errorMsg = err.response?.data?.message || err.response?.data?.error || 'Error al crear el pedido';
      alert(`❌ ${errorMsg}`);
    } finally {
      setLoading(false);
    }
  };

  // 🆕 Funciones para manejo de imágenes
  const handleImageError = (itemId) => {
    setImageErrors(prev => ({ ...prev, [itemId]: true }));
  };

  const getImageUrl = (item) => {
    if (imageErrors[item.id] || !item.imagenUrl) {
      return getPlaceholderImage(item.tipo);
    }
    return item.imagenUrl;
  };

  const getPlaceholderImage = (tipo) => {
    const placeholders = {
      'Smartphone': 'https://via.placeholder.com/80/667eea/ffffff?text=Phone',
      'Laptop': 'https://via.placeholder.com/80/764ba2/ffffff?text=Laptop',
      'Tablet': 'https://via.placeholder.com/80/f093fb/ffffff?text=Tablet',
      'Monitor': 'https://via.placeholder.com/80/4facfe/ffffff?text=Monitor',
      'Accesorio': 'https://via.placeholder.com/80/00f2fe/ffffff?text=Acc',
    };
    return placeholders[tipo] || 'https://via.placeholder.com/80/cccccc/ffffff?text=Prod';
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Overlay */}
      <div 
        className="fixed inset-0 bg-black bg-opacity-50 transition-opacity"
        onClick={onClose}
      ></div>

      {/* Modal */}
      <div className="flex min-h-full items-center justify-center p-4">
        <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-3xl p-8 max-h-[90vh] overflow-y-auto">
          {/* Close button */}
          <button
            onClick={onClose}
            className="absolute top-4 right-4 p-2 text-slate-400 hover:text-slate-600 transition"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>

          {/* Header */}
          <div className="mb-6">
            <h2 className="text-3xl font-bold text-slate-800 mb-2">
              Finalizar Pedido
            </h2>
            <p className="text-slate-600">
              Completa los datos para confirmar tu compra
            </p>
          </div>

          {/* 🆕 Lista de productos con imágenes */}
          <div className="mb-6 bg-slate-50 rounded-xl p-4">
            <h3 className="font-semibold text-slate-800 mb-4">Productos en tu pedido:</h3>
            <div className="space-y-3 max-h-64 overflow-y-auto">
              {cartItems.map((item) => (
                <div key={item.id} className="flex items-center space-x-4 bg-white p-3 rounded-lg border border-slate-200">
                  {/* Imagen del producto */}
                  <div className="w-16 h-16 flex-shrink-0 bg-slate-50 rounded-lg overflow-hidden border border-slate-200">
                    <img
                      src={getImageUrl(item)}
                      alt={item.nombre}
                      onError={() => handleImageError(item.id)}
                      className="w-full h-full object-contain p-1"
                    />
                  </div>

                  {/* Info del producto */}
                  <div className="flex-1 min-w-0">
                    <h4 className="font-semibold text-slate-800 text-sm truncate">
                      {item.nombre}
                    </h4>
                    {item.marca && (
                      <p className="text-xs text-slate-500">{item.marca}</p>
                    )}
                    <p className="text-xs text-slate-600 mt-1">
                      Cantidad: {item.cantidad}
                    </p>
                  </div>

                  {/* Precio */}
                  <div className="text-right">
                    <p className="font-bold text-slate-800">
                      S/.{((item.precio || 0) * item.cantidad).toFixed(2)}
                    </p>
                    <p className="text-xs text-slate-500">
                      S/.{(item.precio || 0).toFixed(2)} c/u
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit}>
            {/* Resumen del total */}
            <div className="bg-gradient-to-r from-purple-50 to-blue-50 rounded-xl p-4 mb-6 border border-purple-200">
              <div className="flex items-center justify-between mb-2">
                <span className="text-slate-700 font-semibold">Total a pagar:</span>
                <span className="text-3xl font-bold text-purple-600">
                  S/.{getTotal().toFixed(2)}
                </span>
              </div>
              <p className="text-sm text-slate-600">
                {cartItems.length} producto{cartItems.length !== 1 ? 's' : ''} • Envío incluido
              </p>
            </div>

            {/* Dirección de envío */}
            <div className="mb-6">
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                Dirección de Envío *
              </label>
              <textarea
                name="direccionEnvio"
                value={formData.direccionEnvio}
                onChange={handleChange}
                required
                rows="3"
                placeholder="Calle, número, distrito, ciudad..."
                className="w-full px-4 py-3 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 resize-none"
              />
            </div>

            {/* Notas opcionales */}
            <div className="mb-6">
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                Notas del Pedido (Opcional)
              </label>
              <textarea
                name="notas"
                value={formData.notas}
                onChange={handleChange}
                rows="3"
                placeholder="Instrucciones especiales, horario de entrega, etc..."
                className="w-full px-4 py-3 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 resize-none"
              />
            </div>

            {/* Info */}
            <div className="bg-blue-50 border border-blue-200 rounded-xl p-4 mb-6">
              <div className="flex items-start space-x-3">
                <svg className="w-6 h-6 text-blue-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <div className="text-sm text-blue-800">
                  <p className="font-semibold mb-1">Información importante:</p>
                  <ul className="list-disc list-inside space-y-1">
                    <li>El pedido se creará con estado PENDIENTE</li>
                    <li>Recibirás un email de confirmación</li>
                    <li>Puedes cancelar mientras esté en PENDIENTE o PROCESANDO</li>
                    <li>El envío es GRATIS en compras mayores a S/.1000</li>
                  </ul>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="flex space-x-4">
              <button
                type="button"
                onClick={onClose}
                disabled={loading}
                className="flex-1 px-6 py-4 bg-slate-200 text-slate-700 font-semibold rounded-xl hover:bg-slate-300 transition disabled:opacity-50"
              >
                Cancelar
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex-1 px-6 py-4 bg-gradient-to-r from-purple-600 to-blue-500 text-white font-bold rounded-xl hover:shadow-lg transition disabled:opacity-50 flex items-center justify-center space-x-2"
              >
                {loading ? (
                  <>
                    <div className="animate-spin rounded-full h-5 w-5 border-2 border-white border-t-transparent"></div>
                    <span>Procesando...</span>
                  </>
                ) : (
                  <>
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7" />
                    </svg>
                    <span>Confirmar Pedido</span>
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CheckoutModal;