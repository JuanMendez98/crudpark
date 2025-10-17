# CrudPark - Sistema de Parqueadero

Sistema de gestión de parqueadero desarrollado en Java con Swing para la interfaz gráfica y PostgreSQL como base de datos.

## Integrantes del Equipo

- [Tu nombre aquí]
- Equipo: [Nombre del equipo]
- Registro: https://teams.crudzaso.com

## Descripción

CrudPark es una aplicación de escritorio que permite a los operadores de parqueadero:

- Registrar ingresos de vehículos
- Registrar salidas y calcular cobros automáticamente
- Gestionar mensualidades
- Ver dashboard en tiempo real
- Generar tickets con código QR
- Aplicar tiempo de gracia (30 minutos sin cobro)

## Tecnologías Utilizadas

- **Java 21**: Lenguaje de programación
- **Swing**: Interfaz gráfica de usuario
- **JDBC**: Conexión a base de datos
- **PostgreSQL**: Base de datos
- **Maven**: Gestión de dependencias
- **ZXing**: Generación de códigos QR
- **FlatLaf**: Look and Feel moderno

## Estructura del Proyecto

```
CrudPark/
├── src/main/java/com/crudzaso/crudpark/
│   ├── Main.java                    # Punto de entrada
│   ├── config/                      # Configuración
│   │   ├── AppConfig.java
│   │   └── DatabaseConnection.java
│   ├── model/                       # Modelos de datos
│   │   ├── Operador.java
│   │   ├── Ticket.java
│   │   ├── Mensualidad.java
│   │   ├── Tarifa.java
│   │   ├── Pago.java
│   │   ├── TipoCliente.java
│   │   └── TipoVehiculo.java
│   ├── dao/                         # Acceso a datos
│   │   ├── OperadorDAO.java
│   │   ├── TicketDAO.java
│   │   ├── MensualidadDAO.java
│   │   ├── TarifaDAO.java
│   │   └── PagoDAO.java
│   ├── service/                     # Lógica de negocio
│   │   ├── AuthService.java
│   │   ├── TicketService.java
│   │   ├── TarifaCalculator.java
│   │   └── MensualidadService.java
│   ├── util/                        # Utilidades
│   │   ├── QRGenerator.java
│   │   ├── TicketPrinter.java
│   │   ├── DateUtils.java
│   │   └── ValidationUtils.java
│   └── ui/                          # Interfaz gráfica
│       ├── LoginFrame.java
│       ├── MainFrame.java
│       └── panels/
│           ├── DashboardPanel.java
│           ├── IngresoVehiculoPanel.java
│           └── SalidaVehiculoPanel.java
├── src/main/resources/
│   └── application.properties       # Configuración
├── database.sql                     # Script de BD
└── pom.xml                         # Dependencias Maven
```

## Requisitos Previos

- Java JDK 21 o superior
- PostgreSQL 12 o superior
- Maven 3.6 o superior

## Instalación

### 1. Clonar el repositorio

```bash
git clone [URL_DEL_REPOSITORIO]
cd CrudPark
```

### 2. Configurar la base de datos

Crear la base de datos en PostgreSQL:

```bash
psql -U postgres
CREATE DATABASE crudpark;
\c crudpark
\i database.sql
```

### 3. Configurar application.properties

Editar el archivo `src/main/resources/application.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/crudpark
db.user=postgres
db.password=tu_password
```

### 4. Compilar el proyecto

```bash
mvn clean compile
```

### 5. Ejecutar la aplicación

```bash
mvn exec:java -Dexec.mainClass="com.crudzaso.crudpark.Main"
```

O crear un JAR ejecutable:

```bash
mvn clean package
java -jar target/crudpark-1.0.0.jar
```

## Uso de la Aplicación

### 1. Inicio de Sesión

Credenciales por defecto:
- **Usuario**: admin
- **Contraseña**: 123456

### 2. Registro de Ingreso

1. Ir a la pestaña "Ingreso de Vehículo"
2. Ingresar la placa del vehículo (formato: ABC123)
3. Hacer clic en "Registrar Ingreso"
4. El sistema detectará automáticamente si tiene mensualidad vigente

### 3. Registro de Salida

1. Ir a la pestaña "Salida de Vehículo"
2. Ingresar la placa del vehículo
3. Hacer clic en "Buscar"
4. Seleccionar el tipo de vehículo (Carro/Moto)
5. El sistema calculará el monto automáticamente
6. Seleccionar método de pago si aplica
7. Hacer clic en "Registrar Salida"

### 4. Dashboard

- Ver vehículos actualmente en el parqueadero
- Ver tiempo de estadía en tiempo real
- Actualizar información con el botón "Actualizar"

## Reglas de Negocio

1. **Tiempo de gracia**: Los primeros 30 minutos no generan cobro
2. **Ticket único**: No puede haber dos tickets abiertos para la misma placa
3. **Mensualidades**: Los vehículos con mensualidad vigente no pagan
4. **Formato de placa**: Debe ser AAA000 (3 letras + 3 números)
5. **Tarifas por tipo**: Carros y motos tienen tarifas diferentes

## Arquitectura

El proyecto sigue el patrón de arquitectura en capas:

- **UI Layer**: Interfaces Swing (LoginFrame, MainFrame, Panels)
- **Service Layer**: Lógica de negocio (AuthService, TicketService, etc.)
- **DAO Layer**: Acceso a datos con JDBC
- **Model Layer**: POJOs que representan entidades

### Principios Aplicados

- **KISS (Keep It Simple, Stupid)**: Código simple y directo
- **Single Responsibility**: Cada clase tiene una única responsabilidad
- **Separation of Concerns**: Separación clara entre capas
- **DRY (Don't Repeat Yourself)**: Reutilización de código

## Formato del Ticket

```
===========================================
       CrudPark - Crudzaso
===========================================
Ticket #: 000001
Placa: ABC123
Tipo: Invitado
Ingreso: 17/10/2025 02:30 PM
Operador: Admin
-------------------------------------------
QR: TICKET:1|PLATE:ABC123|DATE:1729180800
-------------------------------------------
      Gracias por su visita.
===========================================
```

## Contrato del QR

```
TICKET:{id}|PLATE:{placa}|DATE:{timestamp}
```

## Comunicación con el Sistema C#

Este sistema Java comparte la misma base de datos PostgreSQL con el sistema web desarrollado en C#. No hay comunicación mediante APIs, todo se realiza a través de la base de datos compartida.

## Solución de Problemas

### Error de conexión a la base de datos

- Verificar que PostgreSQL esté ejecutándose
- Verificar credenciales en `application.properties`
- Verificar que la base de datos `crudpark` exista

### Errores de compilación

```bash
mvn clean install -U
```

### Driver PostgreSQL no encontrado

Verificar que Maven haya descargado las dependencias:

```bash
mvn dependency:resolve
```

## Contribuciones

Este proyecto es parte del reto CrudPark del equipo Crudzaso.

## Licencia

Proyecto académico - 2025

## Contacto

- Equipo: [Nombre del equipo]
- Registro: https://teams.crudzaso.com
