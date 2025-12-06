import React, { useEffect, useState } from 'react';
import Layout from '../components/Layout/Layout';
import ProductGrid from '../components/Products/ProductGrid';
import AdvancedFilters from '../components/Products/AdvancedFilters';
import Pagination from '../components/Products/Pagination';
import searchService from '../services/searchService';

const ProductsPage = () => {
  const [searchResults, setSearchResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [filtros, setFiltros] = useState({
    q: '',
    categoria: '',
    marca: '',
    precioMin: null,
    precioMax: null,
    disponible: false,
    ordenarPor: 'nombre',
    direccion: 'asc',
    pagina: 0,
    tamanio: 12,
  });

  useEffect(() => {
    buscarProductos();
  }, [filtros]);

  const buscarProductos = async () => {
    setLoading(true);
    setError('');

    try {
      // Filtrar valores null/undefined para no enviarlos
      const params = Object.entries(filtros).reduce((acc, [key, value]) => {
        if (value !== null && value !== undefined && value !== '') {
          acc[key] = value;
        }
        return acc;
      }, {});

      const response = await searchService.buscarProductos(params);
      
      if (response.success) {
        setSearchResults(response.datos);
      } else {
        setError('Error al buscar productos');
      }
    } catch (err) {
      console.error('Error en búsqueda:', err);
      setError('Error al conectar con el servidor');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (newFilters) => {
    setFiltros({
      ...newFilters,
      pagina: 0, // Resetear a primera página cuando cambien filtros
      tamanio: filtros.tamanio,
    });
  };

  const handlePageChange = (newPage) => {
    setFiltros({ ...filtros, pagina: newPage });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handlePageSizeChange = (newSize) => {
    setFiltros({ ...filtros, tamanio: newSize, pagina: 0 });
  };

  const limpiarFiltrosActivos = (filterName) => {
    setFiltros({ ...filtros, [filterName]: filterName === 'disponible' ? false : '' });
  };

  return (
    <Layout>
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-slate-800 mb-2">
            Búsqueda Avanzada de Productos
          </h1>
          <p className="text-slate-600">
            Encuentra exactamente lo que buscas con nuestros filtros inteligentes
          </p>
        </div>

        {/* Filtros Avanzados */}
        <div className="mb-8">
          <AdvancedFilters onFilterChange={handleFilterChange} activeFilters={filtros} />
        </div>

        {/* Filtros Activos */}
        {searchResults && searchResults.filtrosAplicados && (
          <div className="mb-6 bg-white rounded-xl border border-slate-200 p-4">
            <div className="flex items-center flex-wrap gap-2">
              <span className="text-sm font-semibold text-slate-700">Filtros activos:</span>
              
              {searchResults.filtrosAplicados.busqueda && (
                <span className="px-3 py-1.5 bg-purple-100 text-purple-700 text-sm rounded-full flex items-center space-x-2">
                  <span>🔍 "{searchResults.filtrosAplicados.busqueda}"</span>
                  <button onClick={() => limpiarFiltrosActivos('q')} className="hover:text-purple-900">
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </span>
              )}

              {searchResults.filtrosAplicados.categoria && (
                <span className="px-3 py-1.5 bg-blue-100 text-blue-700 text-sm rounded-full flex items-center space-x-2">
                  <span>📂 {searchResults.filtrosAplicados.categoria}</span>
                  <button onClick={() => limpiarFiltrosActivos('categoria')} className="hover:text-blue-900">
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </span>
              )}

              {searchResults.filtrosAplicados.marca && (
                <span className="px-3 py-1.5 bg-green-100 text-green-700 text-sm rounded-full flex items-center space-x-2">
                  <span>🏷️ {searchResults.filtrosAplicados.marca}</span>
                  <button onClick={() => limpiarFiltrosActivos('marca')} className="hover:text-green-900">
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </span>
              )}

              {(searchResults.filtrosAplicados.precioMin || searchResults.filtrosAplicados.precioMax) && (
                <span className="px-3 py-1.5 bg-amber-100 text-amber-700 text-sm rounded-full flex items-center space-x-2">
                  <span>
                    💰 S/.{searchResults.filtrosAplicados.precioMin || 0} - S/.{searchResults.filtrosAplicados.precioMax || '∞'}
                  </span>
                  <button onClick={() => {
                    limpiarFiltrosActivos('precioMin');
                    limpiarFiltrosActivos('precioMax');
                  }} className="hover:text-amber-900">
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </span>
              )}

              {searchResults.filtrosAplicados.disponible && (
                <span className="px-3 py-1.5 bg-green-100 text-green-700 text-sm rounded-full flex items-center space-x-2">
                  <span>✓ Solo disponibles</span>
                  <button onClick={() => limpiarFiltrosActivos('disponible')} className="hover:text-green-900">
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </span>
              )}
            </div>
          </div>
        )}

        {/* Resultados y Controles */}
        {searchResults && (
          <div className="mb-6 flex items-center justify-between bg-white rounded-xl border border-slate-200 p-4">
            <div>
              <p className="text-slate-600">
                Mostrando{' '}
                <span className="font-bold text-slate-800">
                  {searchResults.paginacion.totalElementos}
                </span>{' '}
                resultado{searchResults.paginacion.totalElementos !== 1 ? 's' : ''}
                {searchResults.ordenamiento && (
                  <span className="text-sm text-slate-500 ml-2">
                    • Ordenado por {searchResults.ordenamiento.campo} ({searchResults.ordenamiento.direccion})
                  </span>
                )}
              </p>
            </div>

            {/* Tamaño de página */}
            <div className="flex items-center space-x-3">
              <span className="text-sm text-slate-600">Mostrar:</span>
              <select
                value={filtros.tamanio}
                onChange={(e) => handlePageSizeChange(parseInt(e.target.value))}
                className="px-3 py-1.5 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 text-sm"
              >
                <option value="8">8</option>
                <option value="12">12</option>
                <option value="24">24</option>
                <option value="48">48</option>
              </select>
              <span className="text-sm text-slate-600">por página</span>
            </div>
          </div>
        )}

        {/* Loading State */}
        {loading && (
          <div className="text-center py-20">
            <div className="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-600 border-t-transparent mb-4"></div>
            <p className="text-slate-600 text-lg font-semibold">Buscando productos...</p>
          </div>
        )}

        {/* Error State */}
        {error && (
          <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-center">
            <svg className="w-12 h-12 text-red-500 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <p className="text-red-700 font-semibold mb-2">Error al buscar productos</p>
            <p className="text-red-600 text-sm">{error}</p>
          </div>
        )}

        {/* Products Grid */}
        {!loading && !error && searchResults && (
          <>
            {searchResults.productos && searchResults.productos.length > 0 ? (
              <>
                <ProductGrid products={searchResults.productos} />
                
                {/* Paginación */}
                <Pagination
                  paginacion={searchResults.paginacion}
                  onPageChange={handlePageChange}
                />

                {/* Info de página */}
                {searchResults.paginacion && (
                  <div className="mt-6 text-center text-sm text-slate-500">
                    Página {searchResults.paginacion.paginaActual + 1} de {searchResults.paginacion.totalPaginas}
                  </div>
                )}
              </>
            ) : (
              <div className="text-center py-20">
                <svg className="w-24 h-24 text-slate-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <h3 className="text-2xl font-bold text-slate-800 mb-2">
                  No se encontraron productos
                </h3>
                <p className="text-slate-600 mb-6">
                  Intenta ajustar tus filtros de búsqueda
                </p>
                <button
                  onClick={() => handleFilterChange({
                    q: '',
                    categoria: '',
                    marca: '',
                    precioMin: null,
                    precioMax: null,
                    disponible: false,
                    ordenarPor: 'nombre',
                    direccion: 'asc',
                  })}
                  className="px-6 py-3 bg-gradient-to-r from-purple-600 to-blue-500 text-white font-semibold rounded-xl hover:shadow-lg transition"
                >
                  Limpiar todos los filtros
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </Layout>
  );
};

export default ProductsPage;