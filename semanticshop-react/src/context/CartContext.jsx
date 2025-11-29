import React, { createContext, useContext, useState, useEffect } from 'react';

const CartContext = createContext();

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart debe ser usado dentro de CartProvider');
  }
  return context;
};

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState([]);

  // Cargar carrito desde localStorage al iniciar
  useEffect(() => {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      try {
        setCartItems(JSON.parse(savedCart));
      } catch (error) {
        console.error('Error cargando carrito:', error);
      }
    }
  }, []);

  // Guardar carrito en localStorage cada vez que cambia
  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cartItems));
  }, [cartItems]);

  // Agregar producto al carrito
  const addToCart = (product, quantity = 1) => {
    setCartItems(prevItems => {
      const existingItem = prevItems.find(item => item.id === product.id);
      
      if (existingItem) {
        // Si el producto ya existe, aumentar cantidad
        return prevItems.map(item =>
          item.id === product.id
            ? { ...item, cantidad: item.cantidad + quantity }
            : item
        );
      } else {
        // Si es nuevo, agregarlo
        return [...prevItems, { ...product, cantidad: quantity }];
      }
    });
  };

  // Actualizar cantidad de un producto
  const updateQuantity = (productId, newQuantity) => {
    if (newQuantity < 1) {
      removeFromCart(productId);
      return;
    }

    setCartItems(prevItems =>
      prevItems.map(item =>
        item.id === productId
          ? { ...item, cantidad: Math.min(newQuantity, item.stock || 999) }
          : item
      )
    );
  };

  // Incrementar cantidad
  const incrementQuantity = (productId) => {
    setCartItems(prevItems =>
      prevItems.map(item =>
        item.id === productId && item.cantidad < (item.stock || 999)
          ? { ...item, cantidad: item.cantidad + 1 }
          : item
      )
    );
  };

  // Decrementar cantidad
  const decrementQuantity = (productId) => {
    setCartItems(prevItems => {
      const item = prevItems.find(i => i.id === productId);
      if (item && item.cantidad <= 1) {
        return prevItems.filter(i => i.id !== productId);
      }
      return prevItems.map(i =>
        i.id === productId
          ? { ...i, cantidad: i.cantidad - 1 }
          : i
      );
    });
  };

  // Eliminar producto del carrito
  const removeFromCart = (productId) => {
    setCartItems(prevItems => prevItems.filter(item => item.id !== productId));
  };

  // Limpiar todo el carrito
  const clearCart = () => {
    setCartItems([]);
  };

  // Verificar si un producto está en el carrito
  const isInCart = (productId) => {
    return cartItems.some(item => item.id === productId);
  };

  // Obtener cantidad de un producto en el carrito
  const getItemQuantity = (productId) => {
    const item = cartItems.find(item => item.id === productId);
    return item ? item.cantidad : 0;
  };

  // Calcular subtotal
  const getSubtotal = () => {
    return cartItems.reduce((sum, item) => sum + (item.precio || 0) * item.cantidad, 0);
  };

  // Calcular envío
  const getShipping = () => {
    const subtotal = getSubtotal();
    return subtotal > 1000 ? 0 : 29.99;
  };

  // Calcular impuesto (IGV 18%)
  const getTax = () => {
    return getSubtotal() * 0.18;
  };

  // Calcular total
  const getTotal = () => {
    return getSubtotal() + getShipping() + getTax();
  };

  // Obtener cantidad total de items
  const getTotalItems = () => {
    return cartItems.reduce((sum, item) => sum + item.cantidad, 0);
  };

  const value = {
    cartItems,
    addToCart,
    updateQuantity,
    incrementQuantity,
    decrementQuantity,
    removeFromCart,
    clearCart,
    isInCart,
    getItemQuantity,
    getSubtotal,
    getShipping,
    getTax,
    getTotal,
    getTotalItems,
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};