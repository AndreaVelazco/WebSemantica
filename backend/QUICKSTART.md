# 🚀 Guía Rápida de Inicio - SemanticShop

## Paso 1: Preparar la Ontología

**MUY IMPORTANTE**: Copia tu archivo de ontología completo (el que me enviaste) a:

```
src/main/resources/ontology/semanticshop.owl
```

Este archivo debe contener TODOS los individuos, clases, propiedades y reglas SWRL.

## Paso 2: Compilar el Proyecto

```bash
cd semanticshop
mvn clean package
```

Si ves el mensaje `BUILD SUCCESS`, ¡perfecto!

## Paso 3: Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

Verás en la consola:
```
===========================================
  SemanticShop - Sistema Iniciado
  Swagger UI: http://localhost:8080/swagger-ui.html
  H2 Console: http://localhost:8080/h2-console
===========================================
```

## Paso 4: Verificar que Todo Funcione

Abre tu navegador en: **http://localhost:8080/swagger-ui.html**

### 4.1 Verificar Consistencia

En Swagger, busca el endpoint:
```
GET /api/ontology/consistencia
```

Click en "Try it out" → "Execute"

Deberías ver:
```json
{
  "consistente": true,
  "mensaje": "La ontología es consistente",
  "status": "OK"
}
```

### 4.2 Ver Productos

Busca el endpoint:
```
GET /api/productos
```

Execute → Verás tu catálogo completo de productos

### 4.3 Ver Recomendaciones

Busca:
```
GET /api/recomendaciones/cliente/ClienteJuan
```

Execute → Verás productos recomendados basados en las preferencias de Juan

## Paso 5: Probar Funcionalidades Clave

### Compatibilidad de Productos

```
GET /api/productos/iPhone15/compatibles
```

Esto mostrará productos compatibles inferidos por HermiT (por ejemplo, cables USB-C)

### Clientes Premium (Inferidos)

```
GET /api/recomendaciones/clientes
```

Verás que ClienteJuan y ClienteLaura son automáticamente clasificados como ClientePremium porque tienen 5+ pedidos.

### Análisis de Ventas

```
GET /api/analisis/productos/mas-vendidos
```

## 📊 Ejemplos de Uso Real

### Ejemplo 1: Sistema de Recomendaciones

Un cliente (Ana) entra a la tienda. El sistema:
1. Lee sus preferencias (Android, Google)
2. Ejecuta el razonador HermiT
3. Aplica reglas SWRL
4. Recomienda el Google Pixel 8 y accesorios compatibles

**Endpoint**: `GET /api/recomendaciones/cliente/ClienteAna`

### Ejemplo 2: Detección de Incompatibilidades

Un cliente intenta comprar iPhone14 + Cable Lightning. El sistema:
1. Verifica compatibilidad
2. HermiT detecta que iPhone14 usa Lightning
3. Confirma compatibilidad

Pero si intenta iPhone15 + Cable Lightning:
1. HermiT detecta incompatibilidad (iPhone15 usa USB-C)
2. Sistema muestra alerta
3. Recomienda Cable USB-C en su lugar

**Endpoint**: `GET /api/productos/compatibilidad?producto1=iPhone15&producto2=CableLightning`

### Ejemplo 3: Clasificación Automática

Cuando un Cliente Nuevo hace su 5to pedido:
1. HermiT recalcula automáticamente
2. Cliente es reclasificado como ClientePremium
3. Obtiene acceso a recomendaciones premium

## 🎯 Principales Características Demostradas

✅ **Razonamiento OWL**: HermiT infiere nuevas relaciones
✅ **Reglas SWRL**: Aplicadas automáticamente
✅ **Subsunción**: Clasificación automática de clientes
✅ **Consultas SPARQL**: Análisis de datos
✅ **Validación**: Detección de inconsistencias

## 🔍 Debugging

### Ver Logs del Razonador

Los logs mostrarán:
```
Inicializando ontología desde: classpath:ontology/semanticshop.owl
Ontología cargada exitosamente
Número de axiomas: XXX
Ejecutando razonamiento con HermiT...
Ontología consistente: true
Razonador HermiT inicializado correctamente
```

### Si Algo Sale Mal

1. **Ontología no carga**: Verifica la ruta del archivo .owl
2. **Inconsistencia**: Usa `/api/ontology/consistencia` para diagnóstico
3. **Sin recomendaciones**: Verifica que las reglas SWRL estén en la ontología
4. **Puerto ocupado**: Cambia el puerto en application.properties

## 📝 Siguientes Pasos

1. ✅ Verifica que todo funcione
2. 📖 Explora todos los endpoints en Swagger
3. 🧪 Ejecuta el script de pruebas: `./test-api.sh`
4. 💻 Comienza a desarrollar el frontend
5. 📊 Agrega más consultas SPARQL personalizadas

## 🆘 ¿Necesitas Ayuda?

- Revisa los logs en la consola
- Consulta el README.md completo
- Usa Swagger UI para explorar la API
- Los endpoints de ontología te dan información del sistema

¡Listo! Tu sistema SemanticShop está funcionando con razonamiento semántico real 🎉
