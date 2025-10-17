# CrudPark — Parking Ops & Admin Challenge

## Contexto general

**CrudPark** es una simulación de sistema de parqueadero con enfoque operativo y administrativo.
El objetivo es recrear cómo funcionaría un parqueadero real donde un operador maneja los ingresos y salidas de vehículos desde una aplicación de escritorio, mientras el área administrativa monitorea, configura y analiza la información desde una interfaz web.

El reto combina dos tecnologías —**Java (desktop)** y **C# (.NET web)**— que deben compartir **una misma base de datos PostgreSQL** para comunicarse e intercambiar información en tiempo real, sin usar APIs ni endpoints HTTP.

Se espera que el sistema:

* Permita el registro, control y cobro de ingresos de vehículos.
* Administre tarifas, mensualidades y operadores.
* Muestre reportes y métricas útiles para la operación diaria.
* Genere y use tickets impresos con QR o código de barras para cada ingreso.

---

## Formación de equipos

Cada equipo estará compuesto por **3 personas**:

* **1 integrante del equipo Berners-Lee (Java)**
* **2 integrantes del equipo Van Rossum (C#)**

Cada grupo debe coordinarse, definir su nombre y registrar oficialmente su equipo en:
👉 [https://teams.crudzaso.com](https://teams.crudzaso.com)

---

## Repositorios

* **crudpark-java** → Aplicación desktop (Swing + JDBC)
* **crudpark-csharp-front** → Aplicación web (ASP.NET Core)
* **crudpark-csharp-back** → Api REST (ASP.NET Core)

Cada repositorio debe incluir un **README.md** con:

* Objetivo del proyecto
* Instrucciones de instalación y ejecución
* Configuración de la base de datos (PostgreSQL)
* Flujo general de uso
* Créditos de los integrantes y enlace al registro del equipo en *teams.crudzaso.com*

---

## Java — Aplicación Operativa (Swing + JDBC)

### Requerimientos funcionales

1. **Inicio de sesión de operador**

   * Validación directa contra la tabla de operadores.
   * Solo pueden acceder operadores activos.

2. **Ingreso de vehículo**

   * El operador ingresa la placa del vehículo.
   * El sistema consulta la base de datos:

     * Si la placa pertenece a una mensualidad vigente, marca como *entrada sin cobro*.
     * Si no existe o está vencida, se registra como *ingreso temporal (invitado)*.
   * Se genera y se imprime un ticket con:

     * Número o folio
     * Placa
     * Fecha y hora de ingreso
     * Nombre del operador
     * Tipo detectado (Mensualidad / Invitado)
     * Código QR o código de barras

3. **Salida de vehículo**

   * Al registrar la salida, el sistema:

     * Calcula el tiempo total de estadía.
     * Aplica la regla de negocio de tiempo de gracia (30 minutos):
       Si el tiempo ≤ 30 min → no se cobra.
     * Si supera los 30 minutos → calcula el valor según las tarifas registradas.
   * Si el vehículo es de mensualidad vigente → salida automática sin cobro.
   * Si se requiere cobro → registrar el pago (método, monto, operador, hora).

4. **Impresión de ticket**

   * Usar impresora térmica (texto plano o imagen QR).
   * Formato simple, legible, con toda la información necesaria.

---

## C# — Aplicación Web Administrativa (ASP.NET Core)

### Requerimientos funcionales

1. **Dashboard principal (sin login)**

   * Mostrar métricas en tiempo real leídas desde la base de datos:

     * Vehículos actualmente dentro del parqueadero.
     * Ingresos del día (totales y por tipo).
     * Mensualidades activas, próximas a vencer y vencidas.

2. **Gestión de Operadores**

   * Crear, editar o inactivar operadores.
   * Campo opcional de correo electrónico para futuras notificaciones.

3. **Gestión de Mensualidades**

   * Registrar personas con mensualidad (nombre, correo, placa).
   * Definir fechas de inicio y fin.
   * Validar si una mensualidad ya está vigente para una placa.
   * Enviar correo automático cuando la mensualidad esté próxima a vencer (por ejemplo, a 3 días).

4. **Gestión de Tarifas**

   * Registrar reglas de cobro por hora o fracción.
   * Incluir parámetros como:

     * Valor base por hora
     * Valor adicional por fracción
     * Tope diario
     * Tiempo de gracia (mínimo 30 minutos, configurable)

5. **Reportes y análisis**

   * Mostrar gráficas (por ejemplo, con Chart.js) de:

     * Ingresos diarios, semanales y mensuales.
     * Promedio de ocupación.
     * Comparativa de mensualidades vs invitados.
   * Exportar datos a CSV o Excel.

6. **Notificaciones**

   * Envío de correos por vencimiento o creación de mensualidad.
   * Se recomienda usar Mailtrap, Gmail SMTP u otro sandbox.

---

## Comunicación entre sistemas

* Ambas aplicaciones (Java y C#) se comunican únicamente a través de la misma base de datos PostgreSQL.
* No hay endpoints ni API REST.
* El esquema de tablas debe ser acordado entre ambos proyectos antes de iniciar.
* El intercambio se basa en leer y escribir registros sobre las mismas entidades (tickets, mensualidades, pagos, operadores, tarifas).

---

## Reglas de negocio

1. **Tiempo de gracia:** los primeros 30 minutos de estadía no generan cobro.
2. **Un solo ticket abierto por placa:** si hay un ingreso sin salida, no puede registrarse otro.
3. **Mensualidad vigente:** el vehículo entra y sale sin cobro mientras la fecha actual esté dentro del rango.
4. **Pagos:** solo aplican a ingresos de tipo invitado y superen el tiempo de gracia.
5. **Turnos de operador (opcional):** un operador puede abrir y cerrar turno; el sistema debe registrar sus ingresos y cobros.
6. **Validaciones básicas:** no se permiten placas vacías, fechas inconsistentes ni duplicados activos.

---

## Formato sugerido del ticket

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
QR: TICKET:000123|PLATE:ABC123|DATE:1734204300
------------------------------
Gracias por su visita.
==============================
```

**Contrato del QR/Barcode:**

```
TICKET:{id}|PLATE:{placa}|DATE:{date}
```

Ambos sistemas deben usar el mismo formato para validar y leer información.

---

## Recomendaciones generales

* Usar PostgreSQL como base de datos principal.
* Mantener la conexión y credenciales configurables por entorno.
* Registrar en logs las operaciones clave (ingresos, cobros, cierres de turno).
* Documentar todas las decisiones en el README.md correspondiente.
* Mantener la consistencia de datos entre ambas aplicaciones.
