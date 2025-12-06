import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import searchService from '../../services/searchService';

const SearchBar = () => {
  const [query, setQuery] = useState('');
  const [sugerencias, setSugerencias] = useState([]);
  const [showSugerencias, setShowSugerencias] = useState(false);
  const [loading, setLoading] = useState(false);
  const searchRef = useRef(null);
  const navigate = useNavigate();

  // Cerrar sugerencias al hacer click fuera
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setShowSugerencias(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Obtener sugerencias cuando el usuario escribe
  useEffect(() => {
    const obtenerSugerencias = async () => {
      if (query.length < 2) {
        setSugerencias([]);
        return;
      }

      setLoading(true);
      try {
        const response = await searchService.obtenerSugerencias(query, 5);
        if (response.success) {
          setSugerencias(response.sugerencias || []);
          setShowSugerencias(true);
        }
      } catch (error) {
        console.error('Error obteniendo sugerencias:', error);
        setSugerencias([]);
      } finally {
        setLoading(false);
      }
    };

    // Debounce: esperar 300ms después de que el usuario deje de escribir
    const timeoutId = setTimeout(obtenerSugerencias, 300);
    return () => clearTimeout(timeoutId);
  }, [query]);

  const handleSearch = (searchQuery = query) => {
    if (!searchQuery.trim()) return;
    
    setShowSugerencias(false);
    navigate(`/products?q=${encodeURIComponent(searchQuery.trim())}`);
    setQuery('');
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
    if (e.key === 'Escape') {
      setShowSugerencias(false);
    }
  };

  const handleSugerenciaClick = (sugerencia) => {
    handleSearch(sugerencia);
  };

  return (
    <div className="relative w-full" ref={searchRef}>
      <div className="relative">
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyPress={handleKeyPress}
          onFocus={() => query.length >= 2 && setSugerencias.length > 0 && setShowSugerencias(true)}
          placeholder="Buscar productos, categorías..."
          className="w-full px-4 py-2.5 pl-10 pr-10 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 focus:bg-white transition"
        />
        
        {/* Icono de búsqueda */}
        <svg
          className="w-5 h-5 text-slate-400 absolute left-3 top-1/2 transform -translate-y-1/2"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>

        {/* Loading spinner o botón de búsqueda */}
        {loading ? (
          <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
            <div className="animate-spin rounded-full h-5 w-5 border-2 border-purple-600 border-t-transparent"></div>
          </div>
        ) : query.length > 0 ? (
          <button
            onClick={() => handleSearch()}
            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-purple-600 hover:text-purple-700"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
            </svg>
          </button>
        ) : null}
      </div>

      {/* Dropdown de sugerencias */}
      {showSugerencias && sugerencias.length > 0 && (
        <div className="absolute z-50 w-full mt-2 bg-white rounded-xl shadow-lg border border-slate-200 overflow-hidden">
          <div className="p-2">
            <p className="px-3 py-2 text-xs font-semibold text-slate-500 uppercase">
              Sugerencias
            </p>
            {sugerencias.map((sugerencia, index) => (
              <button
                key={index}
                onClick={() => handleSugerenciaClick(sugerencia)}
                className="w-full text-left px-3 py-2.5 hover:bg-slate-50 rounded-lg transition flex items-center space-x-3 group"
              >
                <svg className="w-4 h-4 text-slate-400 group-hover:text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
                <span className="text-sm text-slate-700 group-hover:text-slate-900 font-medium">
                  {sugerencia}
                </span>
              </button>
            ))}
          </div>
          
          {/* Footer */}
          <div className="border-t border-slate-200 p-2 bg-slate-50">
            <button
              onClick={() => handleSearch()}
              className="w-full text-left px-3 py-2 text-sm text-purple-600 hover:text-purple-700 font-semibold flex items-center space-x-2"
            >
              <span>Buscar "{query}"</span>
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
              </svg>
            </button>
          </div>
        </div>
      )}

      {/* Estado vacío */}
      {showSugerencias && !loading && query.length >= 2 && sugerencias.length === 0 && (
        <div className="absolute z-50 w-full mt-2 bg-white rounded-xl shadow-lg border border-slate-200 p-4 text-center">
          <p className="text-sm text-slate-500">No se encontraron sugerencias</p>
        </div>
      )}
    </div>
  );
};

export default SearchBar;