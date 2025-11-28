# 🛒 SemanticShop - Sistema de Comercio Electrónico con Razonamiento Semántico

Sistema inteligente de e-commerce que utiliza ontologías OWL y el razonador HermiT para proporcionar recomendaciones personalizadas y detección automática de compatibilidades entre productos.

## 🎯 Características Principales

- ✅ **Razonamiento Semántico**: Utiliza HermiT para inferir relaciones y clasificaciones
- ✅ **Recomendaciones Inteligentes**: Basadas en preferencias de clientes y reglas SWRL
- ✅ **Detección de Incompatibilidades**: Identifica automáticamente productos incompatibles
- ✅ **Clasificación Automática**: Clientes Premium/Nuevos inferidos por el razonador
- ✅ **Consultas SPARQL**: Análisis avanzado de ventas y comportamiento
- ✅ **Validación de Consistencia**: Verificación lógica del catálogo de productos
- ✅ **API REST Completa**: Documentada con Swagger/OpenAPI

## 🏗️ Arquitectura

```
SemanticShop
├── Backend: Spring Boot 3.2.0 + Java 17
├── Ontología: OWL DL con reglas SWRL
├── Razonador: HermiT 1.4.5
├── OWL API: 5.5.0
└── Apache Jena: 4.10.0 (SPARQL)
```

## 📋 Requisitos

- **Java 17** o superior
- **Maven 3.8+**
- **Navegador web** (para Swagger UI)

## 🚀 Instalación y Ejecución

### 1. Clonar o copiar el proyecto

```bash
cd semanticshop
```

### 2. Instalar dependencias

```bash
mvn clean install
```

### 3. Copiar tu ontología

**IMPORTANTE**: Debes copiar tu archivo de ontología completo a:
```
src/main/resources/ontology/semanticshop.owl
```

### 4. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación se iniciará en: `http://localhost:8080`

## 📚 Documentación de la API

Una vez iniciada la aplicación, accede a:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 🔌 Endpoints Principales

### Productos

```http
GET /api/productos                          # Listar todos los productos
GET /api/productos/{id}                     # Obtener producto por ID
GET /api/productos/categoria/{categoria}    # Productos por categoría
GET /api/productos/{id}/compatibles         # Productos compatibles (inferidos)
GET /api/productos/{id}/incompatibles       # Productos incompatibles (inferidos)
GET /api/productos/compatibilidad           # Verificar compatibilidad
GET /api/productos/buscar?query=...         # Buscar productos
```

### Recomendaciones

```http
GET /api/recomendaciones/cliente/{id}       # Recomendaciones personalizadas
GET /api/recomendaciones/clientes           # Listar todos los clientes
GET /api/recomendaciones/clientes/{id}      # Info de cliente (tipo inferido)
GET /api/recomendaciones/accesorios/{id}    # Accesorios compatibles
GET /api/recomendaciones/historial/{id}     # Recomendaciones por historial
```

### Análisis (SPARQL)

```http
POST /api/analisis/sparql                   # Consulta SPARQL personalizada
GET  /api/analisis/ventas/categoria         # Ventas por categoría
GET  /api/analisis/productos/mas-vendidos   # Top 10 productos
GET  /api/analisis/clientes/premium         # Clientes premium (inferidos)
GET  /api/analisis/productos/rango-precio   # Filtrar por precio
GET  /api/analisis/productos/bajo-stock     # Alertas de inventario
GET  /api/analisis/marcas/populares         # Análisis de marcas
GET  /api/analisis/pedidos/por-estado       # Estados de pedidos
```

### Ontología

```http
GET /api/ontology/consistencia              # Verificar consistencia con HermiT
GET /api/ontology/estadisticas              # Estadísticas de la ontología
GET /api/ontology/clases                    # Listar todas las clases
GET /api/ontology/individuos/{clase}        # Individuos de una clase
GET /api/ontology/info                      # Información del sistema
```

## 🧪 Ejemplos de Uso

### 1. Obtener todos los productos

```bash
curl http://localhost:8080/api/productos
```

### 2. Recomendaciones para un cliente

```bash
curl http://localhost:8080/api/recomendaciones/cliente/ClienteJuan
```

### 3. Verificar compatibilidad

```bash
curl "http://localhost:8080/api/productos/compatibilidad?producto1=iPhone15&producto2=CableUSBC"
```

### 4. Consulta SPARQL personalizada

```bash
curl -X POST http://localhost:8080/api/analisis/sparql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "PREFIX : <http://www.semanticshop.com/ontology#> SELECT ?nombre ?precio WHERE { ?p :nombre ?nombre . ?p :precio ?precio . FILTER(?precio < 500) }"
  }'
```

### 5. Verificar consistencia

```bash
curl http://localhost:8080/api/ontology/consistencia
```

## 🧠 Reglas de Razonamiento (SWRL)

La ontología incluye las siguientes reglas SWRL que son procesadas por HermiT:

1. **IncompatibilidadPorSistemaOperativo**: Productos con diferentes SO son incompatibles
2. **IncompatibilidadPorConector**: Productos con diferentes conectores son incompatibles
3. **CompatibilidadProductoCable**: Productos y cables del mismo conector son compatibles
4. **CompatibilidadProductoCargador**: Productos y cargadores del mismo conector son compatibles
5. **RecomendaciónPorSistemaOperativo**: Recomienda productos según SO preferido
6. **RecomendaciónPorMarca**: Recomienda productos de la marca preferida del cliente

## 📊 Clases Principales de la Ontología

- **Producto** (abstracta)
  - Smartphone
  - Laptop
  - Tablet
  - Accesorio
    - Audifonos
    - Cable
    - Cargador
    - Monitor
    - Mouse
    - Teclado

- **Cliente**
  - ClienteNuevo (sin pedidos)
  - ClientePremium (≥5 pedidos) - *inferido*

- **Caracteristica**
  - SistemaOperativo (iOS, Android, Windows, MacOS)
  - TipoConector (USB-C, Lightning, USB-A)

## 🔧 Configuración

Edita `src/main/resources/application.properties` para personalizar:

```properties
# Puerto del servidor
server.port=8080

# Ruta de la ontología
ontology.file.path=classpath:ontology/semanticshop.owl

# Namespace de la ontología
ontology.namespace=http://www.semanticshop.com/ontology#

# Tipo de razonador
reasoner.type=hermit
```

## 📁 Estructura del Proyecto

```
semanticshop/
├── src/
│   ├── main/
│   │   ├── java/com/semanticshop/
│   │   │   ├── SemanticShopApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── ProductoController.java
│   │   │   │   ├── RecomendacionController.java
│   │   │   │   ├── AnalisisController.java
│   │   │   │   └── OntologyController.java
│   │   │   ├── service/
│   │   │   │   ├── OntologyService.java
│   │   │   │   ├── ProductoService.java
│   │   │   │   ├── RecomendacionService.java
│   │   │   │   └── SPARQLService.java
│   │   │   └── dto/
│   │   │       ├── ProductoDTO.java
│   │   │       ├── ClienteDTO.java
│   │   │       └── RecomendacionDTO.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── ontology/
│   │           └── semanticshop.owl
│   └── test/
├── pom.xml
└── README.md
```

## 🎓 Conceptos Técnicos

### Razonamiento con HermiT

HermiT es un razonador OWL 2 que proporciona:
- **Clasificación**: Organiza clases en jerarquías
- **Realización**: Determina a qué clases pertenecen los individuos
- **Consistencia**: Verifica que no haya contradicciones lógicas
- **Inferencia**: Deduce nuevos hechos a partir de reglas SWRL

### Subsunción

La clasificación automática de productos usa subsunción para determinar:
- Laptops de Gama Alta/Media/Baja según características
- Clientes Premium vs Clientes Nuevos según número de pedidos

## 🐛 Troubleshooting

### Error: No se puede cargar la ontología

**Solución**: Verifica que el archivo `semanticshop.owl` esté en:
```
src/main/resources/ontology/semanticshop.owl
```

### Error: Ontología inconsistente

**Solución**: Usa el endpoint de consistencia para identificar problemas:
```bash
curl http://localhost:8080/api/ontology/consistencia
```

### Error: Puerto 8080 en uso

**Solución**: Cambia el puerto en `application.properties`:
```properties
server.port=8081
```

## 📝 Próximos Pasos

Para continuar el desarrollo:

1. ✅ **Backend completado** - API REST funcional
2. 🔄 **Frontend** - Crear interfaz con React
3. 🔄 **Carrito de compras** - Implementar funcionalidad
4. 🔄 **Base de datos** - Integrar Neo4j o GraphDB
5. 🔄 **Autenticación** - Spring Security
6. 🔄 **Tests** - JUnit y Mockito

## 👥 Autores

- Tu Nombre - Desarrollo del sistema SemanticShop

## 📄 Licencia

Este proyecto es parte de un trabajo académico.

---

**¿Necesitas ayuda?** Consulta la documentación en Swagger UI o revisa los logs de la aplicación.
