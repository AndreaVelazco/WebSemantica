import React, { useState, useEffect } from 'react';
import searchService from '../../services/searchService';

const AdvancedFilters = ({ onFilterChange, activeFilters }) => {
  const [categorias, setCategorias] = useState([]);
  const [marcas, setMarcas] = useState([]);
  const [rangoPrecios, setRangoPrecios] = useState({ precioMin: 0, precioMax: 10000 });
  const [showFilters, setShowFilters] = useState(false);

  useEffect(() => {
    cargarDatosFiltros();
  }, []);

  const cargarDatosFiltros = async () => {
    try {
      const [catData, marcData, rangoData] = await Promise.all([
        searchService.obtenerCategorias(),
        searchService.obtenerMarcas(),
        searchService.obtenerRangoPrecios(),
      ]);

      setCategorias(catData.categorias || []);
      setMarcas(marcData.marcas || []);
      setRangoPrecios({
        precioMin: parseFloat(rangoData.precioMin) || 0,
        precioMax: parseFloat(rangoData.precioMax) || 10000,
      });
    } catch (error) {
      console.error('Error cargando datos de filtros:', error);
    }
  };

  const handleFilterChange = (filterName, value) => {
    onFilterChange({ ...activeFilters, [filterName]: value });
  };

  const handlePriceChange = (type, value) => {
    const numValue = parseFloat(value) || 0;
    onFilterChange({
      ...activeFilters,
      [type]: numValue,
    });
  };

  const limpiarFiltros = () => {
    onFilterChange({
      q: '',
      categoria: '',
      marca: '',
      precioMin: null,
      precioMax: null,
      disponible: false,
      ordenarPor: 'nombre',
      direccion: 'asc',
    });
  };

  const contarFiltrosActivos = () => {
    let count = 0;
    if (activeFilters.q) count++;
    if (activeFilters.categoria) count++;
    if (activeFilters.marca) count++;
    if (activeFilters.precioMin) count++;
    if (activeFilters.precioMax) count++;
    if (activeFilters.disponible) count++;
    return count;
  };

  const filtrosActivos = contarFiltrosActivos();

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
      {/* Header de Filtros */}
      <div className="p-4 border-b border-slate-200 flex items-center justify-between bg-gradient-to-r from-purple-50 to-blue-50">
        <div className="flex items-center space-x-3">
          <svg className="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
          </svg>
          <h3 className="font-bold text-slate-800">Filtros Avanzados</h3>
          {filtrosActivos > 0 && (
            <span className="px-2 py-1 bg-purple-500 text-white text-xs font-bold rounded-full">
              {filtrosActivos}
            </span>
          )}
        </div>
        <div className="flex items-center space-x-2">
          {filtrosActivos > 0 && (
            <button
              onClick={limpiarFiltros}
              className="text-sm text-purple-600 hover:text-purple-700 font-semibold"
            >
              Limpiar todo
            </button>
          )}
          <button
            onClick={() => setShowFilters(!showFilters)}
            className="p-2 hover:bg-white rounded-lg transition"
          >
            <svg
              className={`w-5 h-5 text-slate-600 transition-transform ${showFilters ? 'rotate-180' : ''}`}
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7" />
            </svg>
          </button>
        </div>
      </div>

      {/* Contenido de Filtros */}
      {showFilters && (
        <div className="p-6">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {/* Búsqueda por texto */}
            <div className="lg:col-span-2">
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                🔍 Buscar productos
              </label>
              <input
                type="text"
                value={activeFilters.q || ''}
                onChange={(e) => handleFilterChange('q', e.target.value)}
                placeholder="Nombre, marca, categoría..."
                className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
              />
            </div>

            {/* Categoría */}
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                📂 Categoría
              </label>
              <select
                value={activeFilters.categoria || ''}
                onChange={(e) => handleFilterChange('categoria', e.target.value)}
                className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
              >
                <option value="">Todas</option>
                {categorias.map((cat) => (
                  <option key={cat} value={cat}>
                    {cat}
                  </option>
                ))}
              </select>
            </div>

            {/* Marca */}
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                🏷️ Marca
              </label>
              <select
                value={activeFilters.marca || ''}
                onChange={(e) => handleFilterChange('marca', e.target.value)}
                className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
              >
                <option value="">Todas</option>
                {marcas.map((marca) => (
                  <option key={marca} value={marca}>
                    {marca}
                  </option>
                ))}
              </select>
            </div>

            {/* Precio Mínimo */}
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                💰 Precio Mínimo
              </label>
              <input
                type="number"
                value={activeFilters.precioMin || ''}
                onChange={(e) => handlePriceChange('precioMin', e.target.value)}
                placeholder={`Desde S/.${rangoPrecios.precioMin}`}
                className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
              />
            </div>

            {/* Precio Máximo */}
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                💵 Precio Máximo
              </label>
              <input
                type="number"
                value={activeFilters.precioMax || ''}
                onChange={(e) => handlePriceChange('precioMax', e.target.value)}
                placeholder={`Hasta S/.${rangoPrecios.precioMax}`}
                className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
              />
            </div>

            {/* Ordenar por */}
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                📊 Ordenar por
              </label>
              <select
                value={activeFilters.ordenarPor || 'nombre'}
                onChange={(e) => handleFilterChange('ordenarPor', e.target.value)}
                className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
              >
                <option value="nombre">Nombre</option>
                <option value="precio">Precio</option>
                <option value="stock">Popularidad</option>
              </select>
            </div>

            {/* Dirección */}
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                🔄 Dirección
              </label>
              <select
                value={activeFilters.direccion || 'asc'}
                onChange={(e) => handleFilterChange('direccion', e.target.value)}
                className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
              >
                <option value="asc">Ascendente (A-Z / Menor-Mayor)</option>
                <option value="desc">Descendente (Z-A / Mayor-Menor)</option>
              </select>
            </div>
          </div>

          {/* Disponibilidad */}
          <div className="mt-6 pt-6 border-t border-slate-200">
            <label className="flex items-center space-x-3 cursor-pointer">
              <input
                type="checkbox"
                checked={activeFilters.disponible || false}
                onChange={(e) => handleFilterChange('disponible', e.target.checked)}
                className="w-5 h-5 text-purple-600 border-slate-300 rounded focus:ring-2 focus:ring-purple-500"
              />
              <span className="text-sm font-semibold text-slate-700">
                ✓ Solo productos disponibles en stock
              </span>
            </label>
          </div>

          {/* Info de rango de precios */}
          <div className="mt-4 p-4 bg-blue-50 border border-blue-200 rounded-xl">
            <p className="text-sm text-blue-700">
              💡 <strong>Rango de precios disponibles:</strong> S/.{rangoPrecios.precioMin.toFixed(2)} - S/.{rangoPrecios.precioMax.toFixed(2)}
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdvancedFilters;