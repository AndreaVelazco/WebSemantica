import React from 'react';
import { Link } from 'react-router-dom';
import Layout from '../components/Layout/Layout';
import { useCart } from '../context/CartContext';

const CartPage = () => {
  const {
    cartItems,
    incrementQuantity,
    decrementQuantity,
    updateQuantity,
    removeFromCart,
    clearCart,
    getSubtotal,
    getShipping,
    getTax,
    getTotal,
  } = useCart();

  const subtotal = getSubtotal();
  const shipping = getShipping();
  const tax = getTax();
  const total = getTotal();

  const [promoCode, setPromoCode] = React.useState('');
  const [discount, setDiscount] = React.useState(0);
  const [promoApplied, setPromoApplied] = React.useState(false);

  const handleApplyPromo = () => {
    // Códigos de descuento de ejemplo
    const promoCodes = {
      'DESCUENTO10': 0.10,  // 10%
      'DESCUENTO20': 0.20,  // 20%
      'BIENVENIDO': 0.15,   // 15%
    };

    const discountValue = promoCodes[promoCode.toUpperCase()];
    
    if (discountValue) {
      setDiscount(subtotal * discountValue);
      setPromoApplied(true);
    } else {
      alert('Código de descuento inválido');
      setDiscount(0);
      setPromoApplied(false);
    }
  };

  const finalTotal = total - discount;

  return (
    <Layout>
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8 flex items-center justify-between">
          <div>
            <h1 className="text-4xl font-bold text-slate-800 mb-2">
              Carrito de Compras
            </h1>
            <p className="text-slate-600">
              {cartItems.length} {cartItems.length === 1 ? 'producto' : 'productos'} en tu carrito
            </p>
          </div>
          
          {cartItems.length > 0 && (
            <button
              onClick={() => {
                if (window.confirm('¿Estás seguro de vaciar el carrito?')) {
                  clearCart();
                }
              }}
              className="px-4 py-2 text-red-600 hover:bg-red-50 rounded-xl transition flex items-center space-x-2"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
              <span>Vaciar Carrito</span>
            </button>
          )}
        </div>

        {cartItems.length === 0 ? (
          /* Empty Cart */
          <div className="text-center py-20">
            <div className="w-32 h-32 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <svg className="w-16 h-16 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-slate-800 mb-3">Tu carrito está vacío</h2>
            <p className="text-slate-600 mb-8">¡Agrega productos para comenzar a comprar!</p>
            <Link
              to="/products"
              className="inline-block px-8 py-3 bg-gradient-to-r from-purple-600 to-blue-500 text-white font-semibold rounded-xl hover:shadow-lg transition"
            >
              Explorar Productos
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Cart Items */}
            <div className="lg:col-span-2 space-y-4">
              {cartItems.map((item) => (
                <div key={item.id} className="bg-white rounded-2xl border border-slate-200 p-6 hover:shadow-md transition">
                  <div className="flex items-center space-x-6">
                    {/* Image */}
                    <div className="w-24 h-24 bg-gradient-to-br from-purple-100 to-blue-100 rounded-xl flex items-center justify-center text-4xl flex-shrink-0">
                      {item.imagen ? (
                        <img src={item.imagen} alt={item.nombre} className="w-full h-full object-cover rounded-xl" />
                      ) : (
                        <span>
                          {item.tipo === 'Smartphone'}
                          {item.tipo === 'Laptop' }
                          {item.tipo === 'Tablet'}
                          {item.tipo === 'Accesorio'}
                          {!['Smartphone', 'Laptop', 'Tablet', 'Accesorio'].includes(item.tipo)}
                        </span>
                      )}
                    </div>

                    {/* Details */}
                    <div className="flex-1 min-w-0">
                      <h3 className="text-lg font-bold text-slate-800 mb-1">{item.nombre}</h3>
                      <p className="text-sm text-slate-500 mb-2">{item.marca}</p>
                      <div className="flex items-center space-x-2">
                        <p className="text-xs text-green-600 font-semibold">
                          ✓ En stock
                        </p>
                        {item.sistemaOperativo && (
                          <span className="px-2 py-1 bg-blue-50 text-blue-600 text-xs rounded">
                            {item.sistemaOperativo}
                          </span>
                        )}
                      </div>
                    </div>

                    {/* Quantity Controls */}
                    <div className="flex items-center space-x-3">
                      <button
                        onClick={() => decrementQuantity(item.id)}
                        className="w-8 h-8 bg-slate-100 rounded-lg hover:bg-slate-200 transition flex items-center justify-center"
                      >
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M20 12H4" />
                        </svg>
                      </button>
                      <input
                        type="number"
                        min="1"
                        max={item.stock || 999}
                        value={item.cantidad}
                        onChange={(e) => updateQuantity(item.id, parseInt(e.target.value) || 1)}
                        className="w-16 text-center font-semibold border border-slate-200 rounded-lg py-1"
                      />
                      <button
                        onClick={() => incrementQuantity(item.id)}
                        disabled={item.cantidad >= (item.stock || 999)}
                        className="w-8 h-8 bg-slate-100 rounded-lg hover:bg-slate-200 transition flex items-center justify-center disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4" />
                        </svg>
                      </button>
                    </div>

                    {/* Price */}
                    <div className="text-right">
                      <div className="text-2xl font-bold text-purple-600">
                        S/.{((item.precio || 0) * item.cantidad).toFixed(2)}
                      </div>
                      <div className="text-sm text-slate-500">
                        S/.{(item.precio || 0).toFixed(2)} c/u
                      </div>
                    </div>

                    {/* Remove Button */}
                    <button
                      onClick={() => {
                        if (window.confirm(`¿Eliminar ${item.nombre} del carrito?`)) {
                          removeFromCart(item.id);
                        }
                      }}
                      className="p-2 text-red-500 hover:bg-red-50 rounded-lg transition"
                    >
                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                </div>
              ))}

              {/* Continue Shopping */}
              <Link
                to="/products"
                className="flex items-center space-x-2 text-purple-600 hover:text-purple-700 font-semibold transition"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7" />
                </svg>
                <span>Continuar Comprando</span>
              </Link>
            </div>

            {/* Order Summary */}
            <div className="lg:col-span-1">
              <div className="bg-white rounded-2xl border border-slate-200 p-6 sticky top-6">
                <h2 className="text-xl font-bold text-slate-800 mb-6">Resumen del Pedido</h2>

                <div className="space-y-4 mb-6">
                  <div className="flex justify-between text-slate-600">
                    <span>Subtotal</span>
                    <span className="font-semibold">S/.{subtotal.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>Envío</span>
                    <span className="font-semibold">
                      {shipping === 0 ? (
                        <span className="text-green-600">GRATIS</span>
                      ) : (
                        `$${shipping.toFixed(2)}`
                      )}
                    </span>
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>IGV (18%)</span>
                    <span className="font-semibold">S/.{tax.toFixed(2)}</span>
                  </div>
                  
                  {discount > 0 && (
                    <div className="flex justify-between text-green-600">
                      <span>Descuento</span>
                      <span className="font-semibold">-S/.{discount.toFixed(2)}</span>
                    </div>
                  )}
                  
                  {shipping > 0 && (
                    <div className="bg-blue-50 border border-blue-200 rounded-xl p-3 text-sm text-blue-700">
                      💡 Compra S/.{(1000 - subtotal).toFixed(2)} más para envío gratis
                    </div>
                  )}

                  <hr className="border-slate-200" />

                  <div className="flex justify-between text-lg">
                    <span className="font-bold text-slate-800">Total</span>
                    <span className="font-bold text-purple-600 text-2xl">S/.{finalTotal.toFixed(2)}</span>
                  </div>
                </div>

                {/* Checkout Button */}
                <button className="w-full py-4 bg-gradient-to-r from-purple-600 to-blue-500 text-white font-bold rounded-xl hover:shadow-lg transition mb-4">
                  Proceder al Pago
                </button>

                {/* Payment Methods */}
                <div className="text-center mb-4">
                  <p className="text-xs text-slate-500 mb-2">Aceptamos</p>
                  <div className="flex items-center justify-center space-x-3">
                    <div className="w-12 h-8 bg-slate-100 rounded flex items-center justify-center text-xs font-semibold">
                      VISA
                    </div>
                    <div className="w-12 h-8 bg-slate-100 rounded flex items-center justify-center text-xs font-semibold">
                      MC
                    </div>
                    <div className="w-12 h-8 bg-slate-100 rounded flex items-center justify-center text-xs font-semibold">
                      AMEX
                    </div>
                  </div>
                </div>

                {/* Guarantees */}
                <div className="space-y-3 pt-4 border-t border-slate-200">
                  <div className="flex items-center space-x-2 text-sm text-slate-600">
                    <svg className="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span>Compra 100% segura</span>
                  </div>
                  <div className="flex items-center space-x-2 text-sm text-slate-600">
                    <svg className="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span>Devoluciones gratis</span>
                  </div>
                  <div className="flex items-center space-x-2 text-sm text-slate-600">
                    <svg className="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span>Garantía de 1 año</span>
                  </div>
                </div>
              </div>

              {/* Promo Code */}
              <div className="bg-gradient-to-br from-purple-50 to-blue-50 rounded-2xl border border-purple-200 p-6 mt-6">
                <h3 className="font-bold text-slate-800 mb-3">¿Tienes un código de descuento?</h3>
                <div className="flex space-x-2 mb-2">
                  <input
                    type="text"
                    value={promoCode}
                    onChange={(e) => setPromoCode(e.target.value)}
                    placeholder="Código"
                    className="flex-1 px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
                  />
                  <button 
                    onClick={handleApplyPromo}
                    className="px-6 py-2 bg-purple-600 text-white font-semibold rounded-xl hover:bg-purple-700 transition"
                  >
                    Aplicar
                  </button>
                </div>
                {promoApplied && (
                  <p className="text-sm text-green-600 font-semibold">✓ Código aplicado correctamente</p>
                )}
                <p className="text-xs text-slate-500 mt-2">
                  Prueba: DESCUENTO10, DESCUENTO20, BIENVENIDO
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    </Layout>
  );
};

export default CartPage;