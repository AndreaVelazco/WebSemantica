import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../context/CartContext';

const ProductCard = ({ product }) => {
  const navigate = useNavigate();
  const { addToCart } = useCart();
  const [imageError, setImageError] = useState(false);
  const [imageLoading, setImageLoading] = useState(true);

  const handleAddToCart = (e) => {
    e.stopPropagation();
    addToCart(product);
  };

  const handleCardClick = () => {
    navigate(`/products/${product.id}`);
  };

  // URL de imagen con fallback
  const getImageUrl = () => {
    if (imageError || !product.imagenUrl) {
      return getPlaceholderImage(product.tipo);
    }
    return product.imagenUrl;
  };

  // Imagen placeholder según tipo de producto
  const getPlaceholderImage = (tipo) => {
    const placeholders = {
      'Smartphone': '/images/productos/smartphone-placeholder.jpg',
      'Laptop': '/images/productos/laptop-placeholder.jpg',
      'Tablet': '/images/productos/tablet-placeholder.jpg',
      'Monitor': '/images/productos/monitor-placeholder.jpg',
      'Accesorio': '/images/productos/accesorio-placeholder.jpg',
    };
    return placeholders[tipo] || '/images/productos/producto-placeholder.jpg';
  };

  return (
    <div
      onClick={handleCardClick}
      className="group bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden cursor-pointer border border-slate-100"
    >
      {/* Imagen del producto */}
      <div className="relative h-64 bg-gradient-to-br from-slate-50 to-slate-100 overflow-hidden">
        {imageLoading && (
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="animate-spin rounded-full h-12 w-12 border-4 border-purple-600 border-t-transparent"></div>
          </div>
        )}
        
        <img
          src={getImageUrl()}
          alt={product.nombre}
          onLoad={() => setImageLoading(false)}
          onError={() => {
            setImageError(true);
            setImageLoading(false);
          }}
          className={`w-full h-full object-contain p-4 group-hover:scale-110 transition-transform duration-500 ${
            imageLoading ? 'opacity-0' : 'opacity-100'
          }`}
        />
        
        {/* Badge de stock */}
        {product.stock > 0 ? (
          <div className="absolute top-4 right-4 px-3 py-1 bg-green-500 text-white text-xs font-bold rounded-full shadow-lg">
            {product.stock} disponibles
          </div>
        ) : (
          <div className="absolute top-4 right-4 px-3 py-1 bg-red-500 text-white text-xs font-bold rounded-full shadow-lg">
            Agotado
          </div>
        )}

        {/* Badge de categoría */}
        {product.categoria && (
          <div className="absolute top-4 left-4 px-3 py-1 bg-purple-500/90 backdrop-blur-sm text-white text-xs font-semibold rounded-full">
            {product.categoria}
          </div>
        )}
      </div>

      {/* Información del producto */}
      <div className="p-6">
        {/* Marca */}
        {product.marca && (
          <p className="text-xs font-semibold text-purple-600 mb-2 uppercase tracking-wide">
            {product.marca}
          </p>
        )}

        {/* Nombre */}
        <h3 className="text-lg font-bold text-slate-800 mb-2 line-clamp-2 group-hover:text-purple-600 transition-colors">
          {product.nombre}
        </h3>

        {/* Descripción */}
        {product.descripcion && (
          <p className="text-sm text-slate-600 mb-4 line-clamp-2">
            {product.descripcion}
          </p>
        )}

        {/* Precio */}
        <div className="flex items-center justify-between mb-4">
          <div>
            <p className="text-2xl font-bold text-slate-900">
              S/.{product.precio?.toFixed(2)}
            </p>
          </div>
        </div>

        {/* Botón agregar al carrito */}
        <button
          onClick={handleAddToCart}
          disabled={product.stock === 0}
          className={`w-full py-3 rounded-xl font-semibold transition-all duration-300 ${
            product.stock > 0
              ? 'bg-gradient-to-r from-purple-600 to-blue-500 text-white hover:shadow-lg hover:scale-105'
              : 'bg-slate-200 text-slate-400 cursor-not-allowed'
          }`}
        >
          {product.stock > 0 ? (
            <span className="flex items-center justify-center space-x-2">
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              <span>Agregar al carrito</span>
            </span>
          ) : (
            'No disponible'
          )}
        </button>
      </div>
    </div>
  );
};

export default ProductCard;