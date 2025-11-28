#!/bin/bash

# Script de prueba para SemanticShop API
# Asegúrate de que la aplicación esté corriendo en localhost:8080

BASE_URL="http://localhost:8080/api"

echo "=========================================="
echo "  SemanticShop - Tests de API"
echo "=========================================="
echo ""

# Verificar que el servidor esté corriendo
echo "1. Verificando servidor..."
if curl -s "$BASE_URL/ontology/info" > /dev/null; then
    echo "✓ Servidor corriendo correctamente"
else
    echo "✗ Error: El servidor no está corriendo"
    echo "  Ejecuta: mvn spring-boot:run"
    exit 1
fi
echo ""

# Test de consistencia
echo "2. Verificando consistencia de la ontología..."
curl -s "$BASE_URL/ontology/consistencia" | python3 -m json.tool
echo ""

# Estadísticas
echo "3. Estadísticas de la ontología..."
curl -s "$BASE_URL/ontology/estadisticas" | python3 -m json.tool
echo ""

# Listar productos
echo "4. Listando todos los productos..."
curl -s "$BASE_URL/productos" | python3 -m json.tool | head -50
echo ""

# Obtener un producto específico
echo "5. Obteniendo detalles del iPhone15..."
curl -s "$BASE_URL/productos/iPhone15" | python3 -m json.tool
echo ""

# Productos compatibles
echo "6. Productos compatibles con iPhone15..."
curl -s "$BASE_URL/productos/iPhone15/compatibles" | python3 -m json.tool
echo ""

# Productos incompatibles
echo "7. Productos incompatibles con iPhone15..."
curl -s "$BASE_URL/productos/iPhone15/incompatibles" | python3 -m json.tool
echo ""

# Listar clientes
echo "8. Listando todos los clientes..."
curl -s "$BASE_URL/recomendaciones/clientes" | python3 -m json.tool
echo ""

# Recomendaciones para un cliente
echo "9. Recomendaciones para ClienteJuan..."
curl -s "$BASE_URL/recomendaciones/cliente/ClienteJuan" | python3 -m json.tool
echo ""

# Clientes Premium
echo "10. Clientes Premium (inferidos por HermiT)..."
curl -s "$BASE_URL/analisis/clientes/premium" | python3 -m json.tool
echo ""

# Productos más vendidos
echo "11. Productos más vendidos..."
curl -s "$BASE_URL/analisis/productos/mas-vendidos" | python3 -m json.tool
echo ""

# Ventas por categoría
echo "12. Ventas por categoría..."
curl -s "$BASE_URL/analisis/ventas/categoria" | python3 -m json.tool
echo ""

echo "=========================================="
echo "  Tests completados"
echo "=========================================="
echo ""
echo "Para más información, visita:"
echo "  Swagger UI: http://localhost:8080/swagger-ui.html"
echo ""
