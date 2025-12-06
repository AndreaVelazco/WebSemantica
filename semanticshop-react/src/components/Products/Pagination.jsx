import React from 'react';

const Pagination = ({ paginacion, onPageChange }) => {
  if (!paginacion || paginacion.totalPaginas <= 1) return null;

  const { paginaActual, totalPaginas, esPrimeraPagina, esUltimaPagina } = paginacion;

  const generarPaginas = () => {
    const paginas = [];
    const maxPaginas = 5;
    let inicio = Math.max(0, paginaActual - Math.floor(maxPaginas / 2));
    let fin = Math.min(totalPaginas, inicio + maxPaginas);

    if (fin - inicio < maxPaginas) {
      inicio = Math.max(0, fin - maxPaginas);
    }

    for (let i = inicio; i < fin; i++) {
      paginas.push(i);
    }

    return paginas;
  };

  const paginas = generarPaginas();

  return (
    <div className="flex items-center justify-center space-x-2 mt-8">
      {/* Botón Primera */}
      <button
        onClick={() => onPageChange(0)}
        disabled={esPrimeraPagina}
        className="px-3 py-2 border border-slate-200 rounded-lg hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition"
        title="Primera página"
      >
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
        </svg>
      </button>

      {/* Botón Anterior */}
      <button
        onClick={() => onPageChange(paginaActual - 1)}
        disabled={esPrimeraPagina}
        className="px-4 py-2 border border-slate-200 rounded-lg hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition flex items-center space-x-2"
      >
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7" />
        </svg>
        <span>Anterior</span>
      </button>

      {/* Números de página */}
      <div className="flex items-center space-x-1">
        {paginas.map((pagina) => (
          <button
            key={pagina}
            onClick={() => onPageChange(pagina)}
            className={`w-10 h-10 rounded-lg font-semibold transition ${
              pagina === paginaActual
                ? 'bg-gradient-to-r from-purple-600 to-blue-500 text-white shadow-lg'
                : 'border border-slate-200 hover:bg-slate-50 text-slate-700'
            }`}
          >
            {pagina + 1}
          </button>
        ))}
      </div>

      {/* Botón Siguiente */}
      <button
        onClick={() => onPageChange(paginaActual + 1)}
        disabled={esUltimaPagina}
        className="px-4 py-2 border border-slate-200 rounded-lg hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition flex items-center space-x-2"
      >
        <span>Siguiente</span>
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>

      {/* Botón Última */}
      <button
        onClick={() => onPageChange(totalPaginas - 1)}
        disabled={esUltimaPagina}
        className="px-3 py-2 border border-slate-200 rounded-lg hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition"
        title="Última página"
      >
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 5l7 7-7 7M5 5l7 7-7 7" />
        </svg>
      </button>
    </div>
  );
};

export default Pagination;