import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import SearchBar from './SearchBar';

const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [showUserMenu, setShowUserMenu] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-40">
      <div className="px-6 py-4 flex items-center justify-between">
        {/* Logo */}
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 bg-gradient-to-br from-purple-600 to-blue-500 rounded-lg flex items-center justify-center">
            <span className="text-white font-bold text-lg">SB</span>
          </div>
          <div>
            <h1 className="text-xl font-bold gradient-text">SmartBuy</h1>
            <p className="text-xs text-slate-500">E-commerce Inteligente</p>
          </div>
        </div>

        {/* Search Bar - Centro */}
        <div className="hidden md:flex flex-1 max-w-xl mx-8">
          <SearchBar />
        </div>

        {/* User Menu */}
        <div className="flex items-center space-x-4">
          {/* Notifications */}
          <button className="relative p-2 text-slate-600 hover:bg-slate-100 rounded-lg transition">
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
            <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
          </button>

          {/* User Dropdown */}
          <div className="relative">
            <button
              onClick={() => setShowUserMenu(!showUserMenu)}
              className="flex items-center space-x-3 p-2 hover:bg-slate-100 rounded-xl transition"
            >
              <div className="w-10 h-10 bg-gradient-to-br from-purple-400 to-blue-400 rounded-full flex items-center justify-center">
                <span className="text-white font-bold text-sm">
                  {user?.nombreCompleto?.charAt(0) || user?.username?.charAt(0) || 'U'}
                </span>
              </div>
              <div className="hidden md:block text-left">
                <p className="text-sm font-semibold text-slate-800">
                  {user?.nombreCompleto || user?.username}
                </p>
                <p className="text-xs text-slate-500">{user?.email}</p>
              </div>
              <svg 
                className={`w-4 h-4 text-slate-600 transition-transform ${showUserMenu ? 'rotate-180' : ''}`} 
                fill="none" 
                stroke="currentColor" 
                viewBox="0 0 24 24"
              >
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>

            {/* Dropdown Menu */}
            {showUserMenu && (
              <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-slate-200 py-2 z-50">
                <div className="px-4 py-3 border-b border-slate-200">
                  <p className="text-sm font-semibold text-slate-800">{user?.nombreCompleto}</p>
                  <p className="text-xs text-slate-500">{user?.email}</p>
                </div>
                <button className="w-full text-left px-4 py-2 text-sm text-slate-700 hover:bg-slate-100 transition flex items-center space-x-2">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                  <span>Mi Perfil</span>
                </button>
                <button className="w-full text-left px-4 py-2 text-sm text-slate-700 hover:bg-slate-100 transition flex items-center space-x-2">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                  <span>Configuración</span>
                </button>
                <hr className="my-2 border-slate-200" />
                <button
                  onClick={handleLogout}
                  className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition flex items-center space-x-2"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                  </svg>
                  <span>Cerrar Sesión</span>
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Search Bar Mobile */}
      <div className="md:hidden px-4 pb-4">
        <div className="relative">
          <input
            type="text"
            placeholder="Buscar productos..."
            className="w-full px-4 py-2.5 pl-10 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
          />
          <svg 
            className="w-5 h-5 text-slate-400 absolute left-3 top-1/2 transform -translate-y-1/2" 
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
      </div>
    </header>
  );
};

export default Header;