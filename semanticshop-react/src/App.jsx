import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';

// Pages
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import RecommendationsPage from './pages/RecommendationsPage';
import ProductsPage from './pages/ProductsPage';
import CategoriesPage from './pages/CategoriesPage';
import CartPage from './pages/CartPage';
import OrdersPage from './pages/OrdersPage';
import OrderDetailPage from './pages/OrderDetailPage';

// Private Route Component
const PrivateRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? children : <Navigate to="/login" />;
};

function AppRoutes() {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      {/* Private Routes */}
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
            <DashboardPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/recommendations"
        element={
          <PrivateRoute>
            <RecommendationsPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/products"
        element={
          <PrivateRoute>
            <ProductsPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/categories"
        element={
          <PrivateRoute>
            <CategoriesPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/cart"
        element={
          <PrivateRoute>
            <CartPage />
          </PrivateRoute>
        }
      />

      <Route
  path="/orders"
  element={
    <PrivateRoute>
      <OrdersPage />
    </PrivateRoute>
  }
/>
<Route
  path="/orders/:id"
  element={
    <PrivateRoute>
      <OrderDetailPage />
    </PrivateRoute>
  }
/>

      {/* Placeholder routes - To be implemented */}
      <Route
        path="/favorites"
        element={
          <PrivateRoute>
            <div className="p-8 text-center">
              <h1 className="text-3xl font-bold">Favoritos</h1>
              <p className="text-slate-600 mt-2">Próximamente...</p>
            </div>
          </PrivateRoute>
        }
      />
      <Route
        path="/history"
        element={
          <PrivateRoute>
            <div className="p-8 text-center">
              <h1 className="text-3xl font-bold">Historial</h1>
              <p className="text-slate-600 mt-2">Próximamente...</p>
            </div>
          </PrivateRoute>
        }
      />
      <Route
        path="/settings"
        element={
          <PrivateRoute>
            <div className="p-8 text-center">
              <h1 className="text-3xl font-bold">Configuración</h1>
              <p className="text-slate-600 mt-2">Próximamente...</p>
            </div>
          </PrivateRoute>
        }
      />
      <Route
        path="/help"
        element={
          <PrivateRoute>
            <div className="p-8 text-center">
              <h1 className="text-3xl font-bold">Ayuda</h1>
              <p className="text-slate-600 mt-2">Próximamente...</p>
            </div>
          </PrivateRoute>
        }
      />

      {/* 404 */}
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
}

export default App;