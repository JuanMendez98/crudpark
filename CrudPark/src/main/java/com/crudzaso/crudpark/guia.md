# 📋 Guía de Desarrollo - CrudPark Java (Aplicación Operativa)

## 📌 Resumen del Proyecto

**CrudPark** es un sistema de parqueadero donde desarrollarás la **aplicación de escritorio operativa** (Java Swing + JDBC) que usará el operador para:

- ✅ Registrar entradas/salidas de vehículos
- ✅ Generar tickets con QR/código de barras
- ✅ Calcular y cobrar tarifas
- ✅ Gestionar turnos de operador

**⚠️ Punto clave:** Compartes la **misma base de datos PostgreSQL** con el equipo de C# (que hace la parte administrativa web). **No hay APIs**, solo base de datos compartida.

---

## 🗂️ Orden de Desarrollo Recomendado

### **📦 Fase 1: Fundamentos (Primera semana)**

#### 1.1 Diseño de Base de Datos (CRÍTICO - coordinar con equipo C#)

**Acción:** Reunión con equipo para definir esquema compartido

**Tablas principales a definir:**
- `operadores` → usuarios del sistema operativo
- `tickets` → registro de ingresos/salidas
- `mensualidades` → clientes con plan mensual
- `tarifas` → reglas de cobro
- `pagos` → transacciones de cobro
- `turnos` (opcional) → control de turnos de operador

**Aspectos a acordar:**
- Nombres de tablas y columnas (en español o inglés)
- Tipos de datos y tamaños
- Llaves primarias y foráneas
- Estados/enums (ej: ticket.estado = 'ABIERTO'/'CERRADO')
- Formato del QR: `TICKET:{id}|PLATE:{placa}|DATE:{timestamp}`

#### 1.2 Configuración del Proyecto

**Pasos:**
1. Crear proyecto Maven en IntelliJ/Eclipse/NetBeans
2. Configurar `pom.xml` con dependencias necesarias
3. Crear estructura de paquetes
4. Configurar archivo de propiedades para BD

#### 1.3 Capa de Acceso a Datos (DAO)

**Implementar:**
- `DatabaseConnection.java` → Singleton para conexión PostgreSQL
- `OperadorDAO.java` → CRUD operadores
- `TicketDAO.java` → CRUD tickets
- `MensualidadDAO.java` → CRUD mensualidades
- `TarifaDAO.java` → CRUD tarifas
- `PagoDAO.java` → CRUD pagos

---

### **🔐 Fase 2: Funcionalidad Core (Segunda semana)**

#### 2.1 Login de Operador

**Componentes:**
- `LoginFrame.java` → ventana de login
- `OperadorService.java` → validación de credenciales
- Verificar que operador esté activo (`estado = true`)
- Guardar sesión del operador logueado

#### 2.2 Ingreso de Vehículo

**Flujo:**
1. Operador ingresa placa del vehículo
2. Sistema busca si placa tiene mensualidad vigente
3. Si tiene mensualidad → registrar entrada sin cobro
4. Si NO tiene → registrar como invitado (temporal)
5. Generar ticket en BD
6. Generar QR/código de barras
7. Imprimir ticket

**Componentes:**
- `IngresoVehiculoFrame.java`
- `TicketService.java` → lógica de generación de tickets
- Validación: no permitir duplicados (una placa no puede tener 2 tickets abiertos)

#### 2.3 Generación e Impresión de Ticket

**Formato QR acordado:**
```
TICKET:{id}|PLATE:{placa}|DATE:{timestamp}
```

**Implementar:**
- `QRGenerator.java` → usando ZXing (Google)
- `TicketPrinter.java` → layout del ticket térmico
- Usar `java.awt.print` o librería específica de impresora térmica

**Layout sugerido:**
```
==============================
     CrudPark - Crudzaso
==============================
Ticket #: 000123
Placa: ABC123
Tipo: Invitado
Ingreso: 2025-10-14 09:45 AM
Operador: Juan Pérez
------------------------------
[IMAGEN QR AQUÍ]
------------------------------
Gracias por su visita.
==============================
```

---

### **💰 Fase 3: Salidas y Cobros (Tercera semana)**

#### 3.1 Salida de Vehículo

**Flujo:**
1. Operador ingresa placa o escanea QR
2. Sistema busca ticket abierto
3. Calcular tiempo de estadía (salida - ingreso)
4. Aplicar regla de 30 min de gracia
5. Si ≤ 30 min → salida sin cobro
6. Si > 30 min → calcular tarifa
7. Si es mensualidad → salida automática sin cobro
8. Si requiere cobro → mostrar ventana de pago

**Componentes:**
- `SalidaVehiculoFrame.java`
- `TarifaCalculator.java` → cálculo de tarifas
- Usar `java.time` para cálculo de tiempo

#### 3.2 Registro de Pagos

**Ventana de cobro debe mostrar:**
- Datos del vehículo
- Tiempo de estadía
- Monto a pagar
- Método de pago (efectivo, tarjeta, etc.)

**Componentes:**
- `PagoFrame.java`
- `PagoService.java` → registro en BD
- Cerrar ticket después de pago (`estado = 'CERRADO'`)

---

### **📊 Fase 4: Refinamiento**

#### 4.1 Dashboard del Operador

**Mostrar:**
- Vehículos actualmente dentro (tickets abiertos)
- Histórico del turno actual
- Ingresos del día/turno
- Estadísticas rápidas

**Componente:**
- `DashboardFrame.java`

#### 4.2 Pruebas y Ajustes

**Checklist:**
- ✅ Validaciones de campos vacíos
- ✅ Manejo de errores de BD
- ✅ Sincronización con aplicación C#
- ✅ Pruebas de concurrencia (2 operadores simultáneos)
- ✅ Pruebas de impresión
- ✅ Backup de datos

---

## 🛠️ Qué Necesitas para Empezar

### **1. Herramientas y Software**

```
✓ JDK 17 o superior (recomendado JDK 21)
✓ IDE (IntelliJ IDEA Community, Eclipse, NetBeans)
✓ PostgreSQL 15+ instalado y configurado
✓ Maven 3.8+ (gestor de dependencias)
✓ Git para control de versiones
✓ DBeaver o pgAdmin (gestión de BD)
```

### **2. Dependencias Maven Principales**

```xml
<!-- PostgreSQL JDBC Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.2</version>
</dependency>

<!-- ZXing para generación de QR -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.3</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.3</version>
</dependency>

<!-- FlatLaf - Look and Feel moderno (opcional) -->
<dependency>
    <groupId>com.formdev</groupId>
    <artifactId>flatlaf</artifactId>
    <version>3.4</version>
</dependency>

<!-- SLF4J + Logback para logging (opcional) -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.14</version>
</dependency>
```

### **3. Coordinación con Equipo C#**

**⚠️ URGENTE - Primera reunión:**
- Diseñar el esquema de BD compartido
- Definir el contrato del QR/código de barras
- Acordar estados de tickets (ABIERTO, CERRADO, PAGADO, etc.)
- Acordar enums y catálogos (métodos de pago, tipos de vehículo, etc.)
- Establecer canal de comunicación continuo (Discord, Slack, WhatsApp)
- Definir estrategia de versionamiento de BD

### **4. Conocimientos Técnicos Necesarios**

- ✅ **Java Swing** → interfaces gráficas
- ✅ **JDBC** → conexión y queries a PostgreSQL
- ✅ **POO** → programación orientada a objetos
- ✅ **Patrón DAO** → acceso a datos
- ✅ **java.time** → manejo de fechas/tiempo
- ✅ **ZXing** → generación de códigos QR
- ✅ **PreparedStatement** → prevención SQL Injection
- ✅ **Try-with-resources** → gestión de conexiones

---

## 📁 Estructura de Proyecto Sugerida

```
crudpark-java/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── crudpark/
│   │   │           ├── dao/              # Data Access Objects
│   │   │           │   ├── DatabaseConnection.java
│   │   │           │   ├── OperadorDAO.java
│   │   │           │   ├── TicketDAO.java
│   │   │           │   ├── MensualidadDAO.java
│   │   │           │   ├── TarifaDAO.java
│   │   │           │   └── PagoDAO.java
│   │   │           │
│   │   │           ├── model/            # Entidades
│   │   │           │   ├── Operador.java
│   │   │           │   ├── Ticket.java
│   │   │           │   ├── Mensualidad.java
│   │   │           │   ├── Tarifa.java
│   │   │           │   └── Pago.java
│   │   │           │
│   │   │           ├── service/          # Lógica de negocio
│   │   │           │   ├── OperadorService.java
│   │   │           │   ├── TicketService.java
│   │   │           │   ├── TarifaCalculator.java
│   │   │           │   └── PagoService.java
│   │   │           │
│   │   │           ├── ui/               # Ventanas Swing
│   │   │           │   ├── LoginFrame.java
│   │   │           │   ├── MainFrame.java
│   │   │           │   ├── DashboardPanel.java
│   │   │           │   ├── IngresoVehiculoFrame.java
│   │   │           │   ├── SalidaVehiculoFrame.java
│   │   │           │   └── PagoFrame.java
│   │   │           │
│   │   │           ├── util/             # Utilidades
│   │   │           │   ├── QRGenerator.java
│   │   │           │   ├── TicketPrinter.java
│   │   │           │   ├── DateTimeUtil.java
│   │   │           │   └── ConfigLoader.java
│   │   │           │
│   │   │           └── Main.java         # Punto de entrada
│   │   │
│   │   └── resources/
│   │       ├── config.properties         # Configuración BD
│   │       ├── logback.xml              # Configuración logs
│   │       └── images/                  # Iconos, logos
│   │
│   └── test/
│       └── java/
│           └── com/crudpark/            # Tests unitarios
│
├── pom.xml                              # Maven dependencies
├── README.md                            # Documentación
└── .gitignore
```

---

## ⚙️ Reglas de Negocio Críticas a Implementar

### **1. Tiempo de gracia (30 minutos)**
- Si estadía ≤ 30 min → **NO COBRAR**
- Implementar en `TarifaCalculator.java`

### **2. Un solo ticket abierto por placa**
- Validar antes de registrar ingreso
- Query: `SELECT * FROM tickets WHERE placa = ? AND estado = 'ABIERTO'`

### **3. Mensualidad vigente**
- Revisar: `fecha_inicio ≤ HOY ≤ fecha_fin`
- Si vigente → entrada/salida sin cobro

### **4. Solo operadores activos**
- En login verificar: `WHERE usuario = ? AND password = ? AND activo = true`

### **5. Formato QR estándar**
- **Contrato acordado con C#:**
  ```
  TICKET:{id}|PLATE:{placa}|DATE:{timestamp}
  ```
- Ejemplo: `TICKET:123|PLATE:ABC123|DATE:1734204300`

### **6. Estados de Ticket**
- `ABIERTO` → vehículo dentro
- `CERRADO` → vehículo salió y pagó (si aplicaba)
- `GRACIA` → salió sin pagar (≤30 min)

---

## 🚀 Primeros Pasos Concretos

### **Paso 1: Diseño de Base de Datos**
Coordinar con equipo C# para definir esquema compartido

### **Paso 2: Configurar Proyecto Maven**
Crear proyecto con dependencias PostgreSQL + ZXing

### **Paso 3: Crear Estructura Base**
Implementar paquetes: dao, model, service, ui, util

### **Paso 4: Implementar Conexión PostgreSQL**
Crear `DatabaseConnection.java` con patrón Singleton

### **Paso 5: Implementar Login**
Primera funcionalidad: autenticación de operador

---

## 📝 Ejemplo de Esquema de BD (Propuesta)

```sql
-- Tabla: operadores
CREATE TABLE operadores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla: mensualidades
CREATE TABLE mensualidades (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    placa VARCHAR(20) UNIQUE NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activo BOOLEAN DEFAULT true
);

-- Tabla: tarifas
CREATE TABLE tarifas (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    valor_hora DECIMAL(10,2) NOT NULL,
    valor_fraccion DECIMAL(10,2),
    minutos_fraccion INT DEFAULT 60,
    tiempo_gracia INT DEFAULT 30,
    tope_diario DECIMAL(10,2),
    activo BOOLEAN DEFAULT true
);

-- Tabla: tickets
CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    folio VARCHAR(20) UNIQUE NOT NULL,
    placa VARCHAR(20) NOT NULL,
    tipo VARCHAR(20) NOT NULL, -- 'MENSUALIDAD' o 'INVITADO'
    fecha_ingreso TIMESTAMP NOT NULL,
    fecha_salida TIMESTAMP,
    operador_ingreso_id INT REFERENCES operadores(id),
    operador_salida_id INT REFERENCES operadores(id),
    estado VARCHAR(20) DEFAULT 'ABIERTO', -- 'ABIERTO', 'CERRADO', 'GRACIA'
    qr_code TEXT,
    monto_cobrado DECIMAL(10,2) DEFAULT 0
);

-- Tabla: pagos
CREATE TABLE pagos (
    id SERIAL PRIMARY KEY,
    ticket_id INT REFERENCES tickets(id),
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL, -- 'EFECTIVO', 'TARJETA', etc.
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operador_id INT REFERENCES operadores(id)
);
```

---

## 🎯 Checklist Final

**Antes de empezar:**
- [ ] Reunión con equipo C# para diseñar BD
- [ ] Crear repositorio en GitHub/GitLab
- [ ] Configurar PostgreSQL local
- [ ] Instalar herramientas (JDK, Maven, IDE)

**Durante desarrollo:**
- [ ] Implementar capa DAO completa
- [ ] Implementar modelos (entidades)
- [ ] Crear servicios de negocio
- [ ] Diseñar interfaces Swing
- [ ] Integrar generación de QR
- [ ] Configurar impresión de tickets
- [ ] Implementar cálculo de tarifas
- [ ] Validar reglas de negocio

**Antes de entregar:**
- [ ] Pruebas de integración con BD compartida
- [ ] Verificar sincronización con app C#
- [ ] Documentar README.md completo
- [ ] Registrar equipo en teams.crudzaso.com
- [ ] Preparar presentación/demo

---

## 📞 ¿Por dónde empezar?

Opciones sugeridas:

1. **Diseñar el esquema de base de datos** (recomendado - es la base)
2. **Configurar el proyecto Maven** con las dependencias
3. **Crear la estructura base** del proyecto
4. **Implementar la conexión a PostgreSQL**

**¿Qué prefieres hacer primero?** 🚀