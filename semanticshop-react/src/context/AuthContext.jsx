import React, { createContext, useState, useContext, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadUser();
  }, []);

  const loadUser = async () => {
    const currentUser = authService.getCurrentUser();
    if (currentUser && currentUser.token) {
      try {
        // Cargar perfil completo desde el backend
        const profile = await authService.getProfile();
        const completeUser = { ...currentUser, ...profile };
        setUser(completeUser);
        localStorage.setItem('user', JSON.stringify(completeUser));
      } catch (error) {
        console.error('Error cargando perfil:', error);
        setUser(currentUser); // Usar datos básicos si falla
      }
    }
    setLoading(false);
  };

  const login = async (credentials) => {
    const userData = await authService.login(credentials);
    
    // Cargar perfil completo después del login
    try {
      const profile = await authService.getProfile();
      const completeUser = { ...userData, ...profile };
      setUser(completeUser);
      localStorage.setItem('user', JSON.stringify(completeUser));
      return completeUser;
    } catch (error) {
      console.error('Error cargando perfil completo:', error);
      setUser(userData);
      return userData;
    }
  };

  const register = async (userData) => {
    const newUser = await authService.register(userData);
    
    // Cargar perfil completo después del registro
    try {
      const profile = await authService.getProfile();
      const completeUser = { ...newUser, ...profile };
      setUser(completeUser);
      localStorage.setItem('user', JSON.stringify(completeUser));
      return completeUser;
    } catch (error) {
      console.error('Error cargando perfil completo:', error);
      setUser(newUser);
      return newUser;
    }
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const updateUser = (userData) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const refreshProfile = async () => {
    try {
      const profile = await authService.getProfile();
      const completeUser = { ...user, ...profile };
      setUser(completeUser);
      localStorage.setItem('user', JSON.stringify(completeUser));
      return completeUser;
    } catch (error) {
      console.error('Error refrescando perfil:', error);
      throw error;
    }
  };

  const value = {
    user,
    login,
    register,
    logout,
    updateUser,
    refreshProfile,
    isAuthenticated: authService.isAuthenticated(),
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;