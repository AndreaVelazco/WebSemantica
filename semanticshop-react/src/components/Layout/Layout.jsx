import React from 'react';
import Header from './Header';
import Sidebar from './Sidebar';

const Layout = ({ children, showSidebar = true }) => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100">
      <Header />
      
      <div className="flex">
        {/* Sidebar - Solo mostrar si showSidebar es true */}
        {showSidebar && <Sidebar />}
        
        {/* Main Content */}
        <main className={`flex-1 ${showSidebar ? 'ml-0' : ''}`}>
          <div className="p-6">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
};

export default Layout;