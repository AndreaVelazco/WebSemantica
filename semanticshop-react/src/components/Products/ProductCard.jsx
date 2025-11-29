import React from 'react';
import { useCart } from '../../context/CartContext';

const ProductCard = ({ product }) => {
  const { addToCart, isInCart, getItemQuantity } = useCart();
  const [showNotification, setShowNotification] = React.useState(false);

  const handleAddToCart = (e) => {
    e.preventDefault();
    addToCart(product, 1);
    
    // Mostrar notificación
    setShowNotification(true);
    setTimeout(() => setShowNotification(false), 2000);
  };

  const inCart = isInCart(product.id);
  const quantity = getItemQuantity(product.id);

  return (
    <div className="bg-white rounded-2xl border border-slate-200 overflow-hidden hover:shadow-lg transition-all duration-300 card-hover relative">
      {/* Notification */}
      {showNotification && (
        <div className="absolute top-4 right-4 z-10 bg-green-500 text-white px-4 py-2 rounded-lg shadow-lg animate-bounce">
          ✓ Agregado
        </div>
      )}

      {/* Badge Stock */}
      {product.stock && product.stock > 0 && (
        <div className="absolute top-4 left-4 z-10">
          <span className="px-3 py-1 bg-green-500 text-white text-xs font-bold rounded-full">
            En Stock
          </span>
        </div>
      )}

      {/* Badge en Carrito */}
      {inCart && (
        <div className="absolute top-4 right-4 z-10">
          <span className="px-3 py-1 bg-purple-500 text-white text-xs font-bold rounded-full">
            {quantity} en carrito
          </span>
        </div>
      )}

      {/* Imagen */}
      <div className="h-48 bg-gradient-to-br from-purple-100 to-blue-100 flex items-center justify-center">
        {product.imagen ? (
          <img 
            src={product.imagen} 
            alt={product.nombre}
            className="w-full h-full object-cover"
          />
        ) : (
          <div className="text-6xl">
            {product.tipo === 'Smartphone'}
            {product.tipo === 'Laptop'}
            {product.tipo === 'Tablet' }
            {product.tipo === 'Accesorio'}
            {!['Smartphone', 'Laptop', 'Tablet', 'Accesorio'].includes(product.tipo)}
          </div>
        )}
      </div>

      {/* Contenido */}
      <div className="p-5">
        {/* Marca */}
        {product.marca && (
          <p className="text-xs font-semibold text-purple-600 uppercase mb-1">
            {product.marca}
          </p>
        )}

        {/* Nombre */}
        <h3 className="text-lg font-bold text-slate-800 mb-2 line-clamp-2">
          {product.nombre || 'Producto sin nombre'}
        </h3>

        {/* Descripción */}
        {product.descripcion && (
          <p className="text-sm text-slate-600 mb-3 line-clamp-2">
            {product.descripcion}
          </p>
        )}

        {/* Especificaciones */}
        <div className="flex flex-wrap gap-2 mb-4">
          {product.sistemaOperativo && (
            <span className="px-2 py-1 bg-blue-50 text-blue-600 text-xs rounded-lg">
              {product.sistemaOperativo}
            </span>
          )}
          {product.memoriaRam && (
            <span className="px-2 py-1 bg-green-50 text-green-600 text-xs rounded-lg">
              {product.memoriaRam}
            </span>
          )}
          {product.almacenamiento && (
            <span className="px-2 py-1 bg-amber-50 text-amber-600 text-xs rounded-lg">
              {product.almacenamiento}
            </span>
          )}
        </div>

        {/* Precio y Botón */}
        <div className="flex items-center justify-between mt-4 pt-4 border-t border-slate-200">
          <div>
            {product.precio ? (
              <div className="text-2xl font-bold text-purple-600">
                S/.{product.precio.toFixed(2)}
              </div>
            ) : (
              <div className="text-sm text-slate-500">Precio no disponible</div>
            )}
          </div>

          <button
            onClick={handleAddToCart}
            disabled={!product.precio || (product.stock !== undefined && product.stock <= 0)}
            className={`px-4 py-2 rounded-xl font-semibold transition-all duration-300 flex items-center space-x-2 ${
              inCart
                ? 'bg-green-500 text-white hover:bg-green-600'
                : 'bg-gradient-to-r from-purple-600 to-blue-500 text-white hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed'
            }`}
          >
            {inCart ? (
              <>
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7" />
                </svg>
                <span>En carrito</span>
              </>
            ) : (
              <>
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                <span>Agregar</span>
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductCard;