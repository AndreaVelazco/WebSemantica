import React from 'react';
import { Link } from 'react-router-dom';
import Layout from '../components/Layout/Layout';

const CategoriesPage = () => {
  const categories = [
    {
      id: 1,
      name: 'Smartphones',
      description: 'Los últimos modelos de teléfonos inteligentes',
      icon: '📱',
      count: 45,
      color: 'from-blue-500 to-blue-600',
      bgColor: 'bg-blue-50',
      textColor: 'text-blue-600',
    },
    {
      id: 2,
      name: 'Laptops',
      description: 'Computadoras portátiles para trabajo y entretenimiento',
      icon: '💻',
      count: 32,
      color: 'from-purple-500 to-purple-600',
      bgColor: 'bg-purple-50',
      textColor: 'text-purple-600',
    },
    {
      id: 3,
      name: 'Tablets',
      description: 'Dispositivos versátiles para productividad',
      icon: '📟',
      count: 28,
      color: 'from-pink-500 to-pink-600',
      bgColor: 'bg-pink-50',
      textColor: 'text-pink-600',
    },
    {
      id: 4,
      name: 'Accesorios',
      description: 'Complementos para tus dispositivos',
      icon: '🎧',
      count: 124,
      color: 'from-amber-500 to-amber-600',
      bgColor: 'bg-amber-50',
      textColor: 'text-amber-600',
    },
    {
      
      id: 5,
      name: 'Audífonos',
      description: 'Sonido de alta calidad para tus oídos',
      icon: '🎵',
      count: 56,
      color: 'from-green-500 to-green-600',
      bgColor: 'bg-green-50',
      textColor: 'text-green-600',
    },
    {
      id: 6,
      name: 'Cargadores',
      description: 'Mantén tus dispositivos siempre cargados',
      icon: '🔌',
      count: 89,
      color: 'from-red-500 to-red-600',
      bgColor: 'bg-red-50',
      textColor: 'text-red-600',
    },
  ];

  const brands = [
    { name: 'Apple', logo: '🍎', products: 42 },
    { name: 'Samsung', logo: '📱', products: 38 },
    { name: 'Google', logo: '🔍', products: 15 },
    { name: 'Dell', logo: '💻', products: 24 },
    { name: 'Sony', logo: '🎮', products: 31 },
    { name: 'Microsoft', logo: '🪟', products: 19 },
  ];

  return (
    <Layout>
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-slate-800 mb-2">
            Explorar Categorías
          </h1>
          <p className="text-slate-600">
            Descubre productos organizados por categoría
          </p>
        </div>

        {/* Stats Banner */}
        <div className="bg-gradient-to-r from-purple-600 to-blue-500 rounded-2xl p-8 mb-8 text-white">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            <div>
              <div className="text-4xl font-bold mb-1">6</div>
              <div className="text-sm text-white/80">Categorías</div>
            </div>
            <div>
              <div className="text-4xl font-bold mb-1">374</div>
              <div className="text-sm text-white/80">Productos</div>
            </div>
            <div>
              <div className="text-4xl font-bold mb-1">12</div>
              <div className="text-sm text-white/80">Marcas</div>
            </div>
            <div>
              <div className="text-4xl font-bold mb-1">98%</div>
              <div className="text-sm text-white/80">Disponibilidad</div>
            </div>
          </div>
        </div>

        {/* Categories Grid */}
        <div className="mb-12">
          <h2 className="text-2xl font-bold text-slate-800 mb-6">Todas las Categorías</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {categories.map((category) => (
              <Link
                key={category.id}
                to={`/products?category=${category.name}`}
                className="card-hover bg-white rounded-2xl p-6 border border-slate-200 hover:border-purple-300 transition"
              >
                <div className={`w-16 h-16 ${category.bgColor} rounded-2xl flex items-center justify-center text-3xl mb-4`}>
                  {category.icon}
                </div>
                <h3 className="text-xl font-bold text-slate-800 mb-2">{category.name}</h3>
                <p className="text-sm text-slate-600 mb-4">{category.description}</p>
                <div className="flex items-center justify-between">
                  <span className={`text-sm font-semibold ${category.textColor}`}>
                    {category.count} productos
                  </span>
                  <svg className="w-5 h-5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7" />
                  </svg>
                </div>
              </Link>
            ))}
          </div>
        </div>

        {/* Brands Section */}
        <div className="mb-12">
          <h2 className="text-2xl font-bold text-slate-800 mb-6">Comprar por Marca</h2>
          <div className="bg-white rounded-2xl border border-slate-200 p-6">
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
              {brands.map((brand, index) => (
                <Link
                  key={index}
                  to={`/products?brand=${brand.name}`}
                  className="flex flex-col items-center p-4 rounded-xl hover:bg-slate-50 transition"
                >
                  <div className="text-4xl mb-2">{brand.logo}</div>
                  <div className="text-sm font-semibold text-slate-800 mb-1">{brand.name}</div>
                  <div className="text-xs text-slate-500">{brand.products} productos</div>
                </Link>
              ))}
            </div>
          </div>
        </div>

        {/* Featured Categories */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-12">
          {/* Category Banner 1 */}
          <div className="bg-gradient-to-br from-blue-500 to-purple-600 rounded-2xl p-8 text-white relative overflow-hidden">
            <div className="relative z-10">
              <h3 className="text-2xl font-bold mb-2">Ofertas en Smartphones</h3>
              <p className="text-white/90 mb-4">Hasta 30% de descuento</p>
              <Link
                to="/products?category=Smartphone&sale=true"
                className="inline-block px-6 py-3 bg-white text-purple-600 font-semibold rounded-xl hover:bg-white/90 transition"
              >
                Ver Ofertas
              </Link>
            </div>
            <div className="absolute right-0 bottom-0 text-9xl opacity-10">📱</div>
          </div>

          {/* Category Banner 2 */}
          <div className="bg-gradient-to-br from-amber-500 to-pink-600 rounded-2xl p-8 text-white relative overflow-hidden">
            <div className="relative z-10">
              <h3 className="text-2xl font-bold mb-2">Nuevos Accesorios</h3>
              <p className="text-white/90 mb-4">Recién llegados</p>
              <Link
                to="/products?category=Accesorio&new=true"
                className="inline-block px-6 py-3 bg-white text-pink-600 font-semibold rounded-xl hover:bg-white/90 transition"
              >
                Explorar
              </Link>
            </div>
            <div className="absolute right-0 bottom-0 text-9xl opacity-10">🎧</div>
          </div>
        </div>

        {/* CTA Section */}
        <div className="bg-white rounded-2xl border border-slate-200 p-8 text-center">
          <h3 className="text-2xl font-bold text-slate-800 mb-3">
            ¿No encuentras lo que buscas?
          </h3>
          <p className="text-slate-600 mb-6">
            Contáctanos y te ayudaremos a encontrar el producto perfecto
          </p>
          <Link
            to="/help"
            className="inline-block px-8 py-3 bg-gradient-to-r from-purple-600 to-blue-500 text-white font-semibold rounded-xl hover:shadow-lg transition"
          >
            Contactar Soporte
          </Link>
        </div>
      </div>
    </Layout>
  );
};

export default CategoriesPage;