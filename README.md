ATM Service - Challenge Tecnico
📋 Descripcion del Proyecto
Servicio RESTful para operaciones bancarias a traves de cajeros automaticos (ATM).
Sistema multi-modulo que incluye un backend Spring Boot y una aplicacion de consola Java.
Proyecto desarrollado en Spring Boot 3.5.5 con arquitectura limpia y manejo robusto de excepciones.

Tiempo de desarrollo estimado: 32 horas

🎯 Decisiones de Diseño y Arquitectura
Arquitectura Elegida

Spring Boot 3.5.5 por su madurez, ecosistema robusto

MyBatis como ORM para mantener control preciso sobre las queries SQL y optimizar el rendimiento

Arquitectura en capas (Controller-Service-Mapper) para separacion clara de responsabilidades

H2 Database embebida para simplicidad en desarrollo y testing

Modularizacion Maven para separar backend de consola y facilitar mantenimiento

Patrones y Principios Implementados
ControllerAdvice Global para manejo centralizado de excepciones

DTO Pattern para transferencia de datos entre capas

RESTful API Design con endpoints semanticos y verbos HTTP apropiados

Estrategia de Manejo de Transacciones
Tabla TRANSACCION dedicada para auditoria completa de todas las operaciones

Registro automatico tanto de operaciones exitosas como fallidas

Sistema de logging integrado con la base de datos para trazabilidad

Manejo robusto de errores con codigos HTTP especificos y mensajes descriptivos

⏰ Concesiones y Compromisos por Limitaciones de Tiempo
Funcionalidades Postergadas para una Futura Version:

Sistema de transferencias

Autenticacion JWT robusta con tokens de seguridad

Dockerizacion del servicio para despliegue en contenedores

API documentation con Swagger/OpenAPI

Decisiones Tecnicas Justificadas
MyBatis sobre JPA para mayor control sobre las queries SQL

H2 simplicidad en desarrollo (facil migracion)


🚀 Caracteristicas Principales
✅ Funcionalidades Implementadas
Login con tarjeta - Validacion de tarjetas activas/inactivas

Extraccion de fondos - Con validacion de saldo y estado de cuenta

Deposito de fondos - Acreditacion a cuentas validas

Consulta de saldo - Informacion de saldo en tiempo real

CLI Console - Aplicacion de consola para testing

Sistema de transacciones - Registro completo en base de datos de todas las operaciones

🛡️ Manejo de Errores
ControllerAdvice global para manejo consistente de excepciones

Excepciones especificas por cada caso de error de negocio

Respuestas JSON estandarizadas con tipo de error y mensaje

Codigos HTTP apropiados para cada tipo de error

Registro automatico de transacciones fallidas

📊 Sistema de Transacciones
Tabla TRANSACCION para auditoria completa de operaciones

Registro automatico de operaciones exitosas y fallidas

Campos completos: tipo_operacion, numero_tarjeta, numero_cuenta, monto, estado, motivo_error

Logs detallados para trazabilidad de todas las operaciones ATM

🧪 Testing
Tests unitarios completos para service layer

Tests de integracion para controller endpoints

Cobertura de casos happy path y error paths

Mocking de dependencias con Mockito

🔄 Flujo de Comunicacion
text
atm-console (Cliente) 
    → HTTP Requests → 
        atm-service (API REST) 
            → MyBatis → 
                Base de Datos H2

📊 Base de Datos H2 (Embedded)
Estructura de Tablas:
tarjeta - Informacion de tarjetas (activa/inactiva)

cuenta - Cuentas bancarias con saldos

tarjeta_cuenta - Relacion muchos-a-muchos

transaccion - Registro completo de operaciones

Datos de Prueba Incluidos:
sql
-- Tarjetas
('1234567890123456', true),    -- Activa, asociada a cuenta 0001 y 0004
('9876543210987654', true),    -- Activa, asociada a cuenta 0002
('1111222233334444', false),   -- Inactiva, asociada a cuenta 0003  
('5555666677778888', true)     -- Activa, sin cuentas asociadas

-- Cuentas
('0001', 5000.00, true),      -- Activa, saldo alto
('0002', 3000.00, true),      -- Activa, saldo medio
('0003', 1000.00, false),     -- Inactiva
('0004', 100.00, true),       -- Activa, saldo bajo
('0005', 0.00, true)          -- Activa, sin saldo

🚀 Instalacion y Ejecucion
Prerrequisitos
Java 17 o superior

Maven 3.6+

Ejecutar el Backend Spring Boot:
# Clonar repositorio
git clone <repository-url>
cd atm-service

# Compilar y ejecutar
mvn clean spring-boot:run

Ejecutar la Aplicacion de Consola:
# Compilar y ejecutar la consola
cd atm-console
mvn clean package

java -jar target/atm-console-1.0-SNAPSHOT.jar

# Comandos disponibles:
java -jar atm-console-1.0-SNAPSHOT.jar login <tarjeta>
java -jar atm-console-1.0-SNAPSHOT.jar saldo <tarjeta> <cuenta>
java -jar atm-console-1.0-SNAPSHOT.jar extraer <tarjeta> <cuenta> <importe>
java -jar atm-console-1.0-SNAPSHOT.jar depositar <tarjeta> <cbu> <importe>

Ejecutar Tests:

# Todos los tests
mvn test

# Saltar tests (solo build)
mvn clean package -DskipTests

Acceso a H2 Console:
URL: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:testdb

Usuario: sa

Password: password

📡 API Endpoints:

🔐 Login
http
GET /api/login?numeroTarjeta=1234567890123456
Respuesta Exitosa:

json
{
  "exito": true,
  "mensaje": "Login exitoso",
  "numeroTarjeta": "1234567890123456"
}

💰 Extraccion
http
POST /api/extraer
Content-Type: application/json

{
  "tarjeta": "1234567890123456",
  "cbu": "0001", 
  "monto": 1000.0
}

📥 Deposito
http
POST /api/depositar
Content-Type: application/json

{
  "tarjeta": "1234567890123456",
  "cbu": "0001",
  "monto": 500.0
}

📊 Consulta de Saldo
http
GET /api/saldo?tarjeta=1234567890123456&cbu=0001
Respuesta:

json
{
  "saldo": 5000.0
}

🚨 Manejo de Errores
La API devuelve respuestas consistentes en formato JSON:

json
{
  "errorType": "TIPO_ERROR",
  "message": "Descripcion del error"
}

Tipos de Error Implementados:
TARJETA_NO_EXISTE

CUENTA_NO_EXISTE

TARJETA_INACTIVA

CUENTA_INACTIVA

SALDO_INSUFICIENTE

MONTO_NEGATIVO

TARJETA_NO_ASOCIADA

PARAMETRO_INVALIDO

ERROR_INTERNO

🧪 Testing Strategy
Tests Unitarios (Service Layer)
✅ Validacion de reglas de negocio

✅ Manejo de excepciones especificas

✅ Mocking de dependencias externas

Tests de Integracion (Controller Layer)
✅ Endpoints REST con MockMvc

✅ Validacion de respuestas HTTP

✅ Formato JSON de respuestas

Casos de Prueba Cubiertos

Flujos exitosos (happy paths)

Errores de negocio (saldo insuficiente, cuentas inactivas, monto negativo)

Validacion de parametros

Errores del sistema

🛠️ Tecnologias Utilizadas

Spring Boot 3.5.5 - Framework principal

Spring Web MVC - API REST

MyBatis - Mapeo ORM

H2 Database - Base de datos embebida

JUnit 5 - Testing framework

Mockito - Mocking para tests

Maven - Gestion de dependencias

Lombok - Reduccion de boilerplate code

👨‍💻 Autor
Elian Cordone - https://github.com/Elian-Cordone7
Tiempo de desarrollo: 32 horas

📄 Licencia
Este proyecto es parte de un challenge tecnico. Desarrollado con fines demostrativos.
