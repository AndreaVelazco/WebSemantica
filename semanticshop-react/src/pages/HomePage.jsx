import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import ProductGrid from '../components/Products/ProductGrid';
import productService from '../services/productService';

const HomePage = () => {
  const { isAuthenticated } = useAuth();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const data = await productService.getAllProducts();
      setProducts(data.slice(0, 8));
    } catch (error) {
      console.error('Error cargando productos:', error);
    } finally {
      setLoading(false);
    }
  };

  // Si el usuario está autenticado, redirigir al dashboard
  if (isAuthenticated) {
    window.location.href = '/dashboard';
    return null;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100">
      {/* Header para página pública */}
      <header className="bg-white/95 backdrop-blur-sm shadow-md sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
          {/* Logo */}
          <div className="flex items-center space-x-3">
            <div className="w-12 h-12 bg-gradient-to-br from-purple-600 to-blue-500 rounded-xl flex items-center justify-center">
              <span className="text-white text-xl font-bold">SB</span>
            </div>
            <div>
              <h1 className="text-2xl font-bold gradient-text">Tech Boutique</h1>
              <p className="text-xs text-slate-500">Tecnologia y estilo</p>
            </div>
          </div>

          {/* Navigation */}
          <nav className="hidden md:flex items-center space-x-8">
            <a href="#features" className="text-slate-600 hover:text-purple-600 font-medium transition">
              Características
            </a>
            <a href="#products" className="text-slate-600 hover:text-purple-600 font-medium transition">
              Productos
            </a>
            <a href="#unete" className="text-slate-600 hover:text-purple-600 font-medium transition">
              Unete
            </a>
          </nav>

          {/* Auth Buttons */}
          <div className="flex items-center space-x-4">
            <Link
              to="/login"
              className="text-purple-600 hover:text-purple-700 font-semibold transition"
            >
              Iniciar Sesión
            </Link>
            <Link
              to="/register"
              className="px-6 py-2.5 bg-gradient-to-r from-purple-600 to-blue-500 text-white font-semibold rounded-xl hover:shadow-lg transition transform hover:scale-105"
            >
              Registrarse
            </Link>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-r from-slate-900 via-purple-900 to-slate-900 text-white py-24">
        {/* Decorative elements */}
        <div className="absolute top-0 left-0 w-96 h-96 bg-purple-600 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob"></div>
        <div className="absolute top-0 right-0 w-96 h-96 bg-blue-600 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-2000"></div>
        <div className="absolute bottom-0 left-1/2 w-96 h-96 bg-pink-600 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-4000"></div>

        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-5xl md:text-7xl font-bold mb-6 leading-tight">
            Compras <span className="text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-blue-400">Inteligentes</span>
            <br />
          </h2>
          <p className="text-xl text-slate-300 mb-10 max-w-3xl mx-auto">
            Descubre productos recomendados especialmente para ti
          </p>
          <div className="flex items-center justify-center space-x-4">
            <Link
              to="/register"
              className="px-8 py-4 bg-gradient-to-r from-purple-600 to-blue-500 text-white font-bold rounded-xl hover:shadow-2xl transition transform hover:scale-105 text-lg"
            >
              Comenzar
            </Link>
            <a
              href="#products"
              className="px-8 py-4 bg-white/10 backdrop-blur-sm text-white font-bold rounded-xl hover:bg-white/20 transition border border-white/20"
            >
              Ver Productos
            </a>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-3 gap-8 mt-16 max-w-3xl mx-auto">
            <div>
              <div className="text-4xl font-bold text-purple-400">500+</div>
              <div className="text-sm text-slate-400 mt-1">Productos</div>
            </div>
            <div>
              <div className="text-4xl font-bold text-blue-400">10K+</div>
              <div className="text-sm text-slate-400 mt-1">Usuarios</div>
            </div>
            <div>
              <div className="text-4xl font-bold text-pink-400">98%</div>
              <div className="text-sm text-slate-400 mt-1">Satisfacción</div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <h3 className="text-4xl font-bold text-slate-800 mb-4">
              ¿Por qué Tech Boutique?
            </h3>
            <p className="text-slate-600 max-w-2xl mx-auto">
              Utilizamos tecnología de vanguardia para ofrecerte la mejor experiencia de compra
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {/* Feature 1 */}
            <div className="card-hover bg-gradient-to-br from-purple-50 to-blue-50 rounded-2xl p-8 border border-purple-100">
              <div className="w-16 h-16 bg-gradient-to-br from-purple-600 to-blue-500 rounded-2xl flex items-center justify-center mb-6">
                <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
                </svg>
              </div>
              <h4 className="text-2xl font-bold text-slate-800 mb-3">Recomendaciones IA</h4>
              <p className="text-slate-600">
                Te ofrecemos productos perfectos para ti, para que estes actualizado
                con productos de calidad y que van con tu estilo
              </p>
            </div>

            {/* Feature 2 */}
            <div className="card-hover bg-gradient-to-br from-blue-50 to-purple-50 rounded-2xl p-8 border border-blue-100">
              <div className="w-16 h-16 bg-gradient-to-br from-blue-600 to-purple-500 rounded-2xl flex items-center justify-center mb-6">
                <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                </svg>
              </div>
              <h4 className="text-2xl font-bold text-slate-800 mb-3">Compra Segura</h4>
              <p className="text-slate-600">
                Sistema de verificación de compatibilidad que previene compras incompatibles
                antes de que las realices
              </p>
            </div>

            {/* Feature 3 */}
            <div className="card-hover bg-gradient-to-br from-pink-50 to-purple-50 rounded-2xl p-8 border border-pink-100">
              <div className="w-16 h-16 bg-gradient-to-br from-pink-600 to-purple-500 rounded-2xl flex items-center justify-center mb-6">
                <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
              <h4 className="text-2xl font-bold text-slate-800 mb-3">Rápido y Eficiente</h4>
              <p className="text-slate-600">
                Encuentra lo que necesitas en segundos
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Products Section */}
      <section id="products" className="py-20 bg-slate-50">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-12">
            <h3 className="text-4xl font-bold text-slate-800 mb-4">
              Productos Destacados
            </h3>
            <p className="text-slate-600">
              Explora nuestra selección de productos mas populares del condado
            </p>
          </div>

          {loading ? (
            <div className="text-center py-12">
              <div className="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-600 border-t-transparent"></div>
              <p className="text-slate-600 mt-4 text-lg">Cargando productos...</p>
            </div>
          ) : (
            <ProductGrid products={products} />
          )}

          <div className="text-center mt-12">
            <Link
              to="/register"
              className="inline-block px-8 py-4 bg-gradient-to-r from-purple-600 to-blue-500 text-white font-bold rounded-xl hover:shadow-lg transition transform hover:scale-105"
            >
              Ver Todos los Productos
            </Link>
          </div>
        </div>
      </section>


      {/* CTA Section */}
      <section id="unete" className="py-20 bg-white">
        <div className="max-w-4xl mx-auto px-6 text-center">
          <h3 className="text-4xl font-bold text-slate-800 mb-6">
            ¿Listo para comenzar?
          </h3>
          <p className="text-xl text-slate-600 mb-8">
            Únete a miles de usuarios que ya disfrutan de compras inteligentes
          </p>
          <Link
            to="/register"
            className="inline-block px-10 py-4 bg-gradient-to-r from-purple-600 to-blue-500 text-white font-bold rounded-xl hover:shadow-2xl transition transform hover:scale-105 text-lg"
          >
            Crear Cuenta Gratis
          </Link>
          <p className="text-sm text-slate-500 mt-4">
            No se requiere tarjeta de crédito
          </p>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-slate-900 text-white py-12">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
            <div>
              <div className="flex items-center space-x-3 mb-4">
                <div className="w-10 h-10 bg-gradient-to-br from-purple-600 to-blue-500 rounded-lg flex items-center justify-center">
                  <span className="text-white font-bold">AV</span>
                </div>
                <span className="text-xl font-bold">Tech Boutique</span>
              </div>
              <p className="text-slate-400 text-sm">
                Tienda tecnologica e inteligente con productos A1
              </p>
            </div>
            <div>
              <h4 className="font-bold mb-4">Productos</h4>
              <ul className="space-y-2 text-slate-400 text-sm">
                <li><a href="#" className="hover:text-white transition">Todos</a></li>
                <li><a href="#" className="hover:text-white transition">Nuevos</a></li>
                <li><a href="#" className="hover:text-white transition">Ofertas</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-bold mb-4">Ayuda</h4>
              <ul className="space-y-2 text-slate-400 text-sm">
                <li><a href="#" className="hover:text-white transition">Soporte</a></li>
                <li><a href="#" className="hover:text-white transition">FAQ</a></li>
                <li><a href="#" className="hover:text-white transition">Contacto</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-bold mb-4">Legal</h4>
              <ul className="space-y-2 text-slate-400 text-sm">
                <li><a href="#" className="hover:text-white transition">Privacidad</a></li>
                <li><a href="#" className="hover:text-white transition">Términos</a></li>
                <li><a href="#" className="hover:text-white transition">Cookies</a></li>
              </ul>
            </div>
          </div>
          <hr className="border-slate-800 mb-8" />
          <div className="text-center text-slate-400 text-sm">
            <p>&copy; 2025 Tech Boutique. Todos los derechos reservados.</p>
          </div>
        </div>
      </footer>

      <style jsx>{`
        @keyframes blob {
          0% { transform: translate(0px, 0px) scale(1); }
          33% { transform: translate(30px, -50px) scale(1.1); }
          66% { transform: translate(-20px, 20px) scale(0.9); }
          100% { transform: translate(0px, 0px) scale(1); }
        }
        .animate-blob {
          animation: blob 7s infinite;
        }
        .animation-delay-2000 {
          animation-delay: 2s;
        }
        .animation-delay-4000 {
          animation-delay: 4s;
        }
      `}</style>
    </div>
  );
};

export default HomePage;