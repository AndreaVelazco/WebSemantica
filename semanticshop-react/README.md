# 🛍️ SemanticShop Frontend - React

Aplicación de e-commerce con sistema de recomendaciones inteligente basado en ontologías OWL.

## 🚀 Inicio Rápido

### 1. Instalar dependencias
```bash
npm install
```

### 2. Configurar backend
Asegúrate de que el backend esté corriendo en `http://localhost:8080`

### 3. Iniciar aplicación
```bash
npm start
```

La aplicación se abrirá en `http://localhost:3000`

## 📁 Estructura del Proyecto

```
src/
├── components/
│   ├── Layout/         # Header, Footer, Layout
│   ├── Products/       # ProductCard, ProductGrid
│   └── Auth/          # LoginForm, RegisterForm
├── pages/
│   ├── HomePage.jsx
│   ├── LoginPage.jsx
│   ├── RegisterPage.jsx
│   ├── DashboardPage.jsx
│   └── RecommendationsPage.jsx
├── services/
│   ├── authService.js
│   ├── productService.js
│   └── recommendationService.js
├── context/
│   └── AuthContext.jsx
├── App.jsx
└── index.js
```

## 🎨 Características

- ✅ Autenticación con JWT
- ✅ Recomendaciones personalizadas
- ✅ Catálogo de productos
- ✅ Diseño responsive
- ✅ Tema morado/azul moderno
- ✅ Integración con ontología OWL

## 🔑 Usuarios de Prueba

```
Usuario: juan_perez
Password: password123
```

## 📦 Tecnologías

- React 18
- React Router v6
- Axios
- Tailwind CSS
- Context API

## 🛠️ Configuración

El archivo `src/services/authService.js` contiene la URL del backend:
```javascript
const API_URL = 'http://localhost:8080/api/auth';
```

Modifica esto si tu backend está en otra URL.

## 📝 Notas

- El proyecto está preconfigurado para conectarse al backend de SemanticShop
- Los tokens JWT se guardan en localStorage
- El interceptor de Axios agrega automáticamente el token a las peticiones

