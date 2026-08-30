# Codigo Fuente del Sistema de Reservas de Recursos y Actividades

## Jose:
**Lógica de Reservas, IA y Matriz Diaria (35%)**



* **Módulo de Inteligencia Artificial (LLM):** Integración mediante cliente HTTP/REST para enviar la frase en lenguaje natural a la API del modelo de lenguaje, procesar la respuesta JSON y autopoblar los campos del formulario de reserva.





* **Algoritmo de Asignación de Recursos:** Desarrollo de la lógica central que evalúa traslapes de horario. Verifica la disponibilidad de al menos una unidad por categoría solicitada y asigna automáticamente el primer recurso libre; en caso de fallo, identifica y retorna las categorías no disponibles.





* **Vistas de Reservas:** Formulario interactivo con selección múltiple de categorías, tabla de "Mis Reservas" y opción de cancelación de reservas futuras.





* **Matriz de Calendarización de Recursos:** Construcción de la tabla Swing donde las filas representan las horas del día y las columnas los recursos de la categoría seleccionada.






## Emily:
**Persistencia XML, Seguridad y Mantenimientos Base (35%)**



* **Capa de Persistencia XML (DAO):** Implementación de la arquitectura de almacenamiento de datos en archivos XML (usando bibliotecas como JAXB, DOM o XMLEncoder) para la lectura, escritura y actualización de todas las entidades del sistema.





* **Autenticación y Sesión:** Formulario de Login con validación de credenciales (ID y contraseña) y asignación de roles (Administrador / Funcionario). Incluye el control de sesión activa y el flujo para cambio de clave.





* **Mantenimientos CRUD (Admin):** Módulos completos (búsqueda, inclusión, consulta, modificación y borrado) para:

* *Funcionarios:* Manejo de datos personales y asignación inicial de clave igual al ID.





* *Categorías de Recursos:* Generación automática de ID y gestión de descripciones.





* *Recursos:* Vinculación de activos a sus respectivas categorías y filtrado.










## Andrea:
**Programación Semanal, Motor PDF y Estadísticas (30%)**



* **Motor Base de Reportes PDF:** Creación de una clase de utilidad centralizada (utilizando librerías como iText o PDFBox) que reciba estructuras de datos y genere documentos PDF formateados. Servirá como servicio para los botones "Imprimir/PDF" de todos los módulos.





* **Matriz de Programación de Actividades:** Cálculo de fechas semanales y renderizado de la matriz de horarios (Filas = Horas, Columnas = Días Lunes a Domingo) detallando actividad y funcionario a cargo.





* **Módulo de Estadísticas y Analítica:** Filtrado de datos por rangos de fechas "desde/hasta" tanto para recursos como para actividades.





* **Visualización de Gráficos:** Integración de la librería gráfica (como JFreeChart) para generar gráficos de barras de recursos más utilizados por categoría y volumen de actividades por semana.







Entregables Compartidos (Responsabilidad de los 3)

Pruebas Automatizadas: Implementación de Test Cases con JUnit Jupiter para pruebas unitarias de su respectivo código (Surefire Plugin) y pruebas de integración (Failsafe Plugin).

* **Repositorio Git:** Creación de ramas por funcionalidad (*feature branches*) y revisiones periódicas en GitHub.


#
# Pantallas y Funcionalidades del Sistema de Reservas

## Emily:

#### 1. Pantalla de Ingreso (Login) y Cambio de Clave

* Acceso: Todos los usuarios.

* Layout y Componentes: Formulario centrado con campos de texto para ID y Clave, botones "Ingresar" y "Cancelar", y botón/diálogo secundario emergente para "Cambiar Clave" (Clave Actual, Clave Nueva y Confirmación).

* Comportamiento: Valida credenciales contra la persistencia XML e inicia sesión cargando la vista correspondiente según el rol (Admin o Funcionario).

#### 2. Pantalla de Reservas (Vista Funcionario)

* Acceso: Funcionario.

* Layout y Componentes:

   * Módulo de IA (LLM): Campo de texto libre para ingresar frases en lenguaje natural y botón "Extraer" para poblar automáticamente los campos.

   * Formulario de Reserva: Campos de Actividad, Fecha, Hora inicio, Hora fin y lista de selección múltiple de Categorías requeridas.

   * Tabla "Mis Reservas": Listado inferior que muestra ID de reserva, Actividad, Fecha, Horario, Recursos asignados y Estado (ACTIVA).

   * Botones de Acción: "Reservar", "Cancelar reserva seleccionada", "Limpiar" e "Imprimir" (PDF).
  
#### 7. Visualización de Programación de Actividades Semanales

* Acceso: Administrador y Funcionario.

* Layout y Componentes:

   * Panel Superior: Campo "Fecha de referencia" para seleccionar la semana a consultar, botones "Cargar" e "Imprimir".

   * Matriz Semanal: Filas asignadas a las horas del día y columnas divididas en los días de la semana (Lunes a Domingo con fecha correspondiente). Muestra las actividades agendadas y el nombre del responsable en cada celda.

## Andrea:  

#### Visualización de Calendarización de Recursos

* Acceso: Administrador y Funcionario.

* Layout y Componentes:

   * Panel Superior: Seleccionadores de Fecha y Categoría de recurso, botones "Cargar" e "Imprimir".

   * Matriz Dinámica: Filas que representan las horas del día (06:00 a 11:00+) y columnas que muestran los recursos específicos de esa categoría. Las celdas ocupadas muestran la actividad y el funcionario que la reservó.


#### Pantalla de Estadísticas y Analítica

* Acceso: Administrador y Funcionario.

* Layout y Componentes: Dividida en dos columnas paralelas:

   * Columna Recursos: Filtro "Fechas Desde/Hasta", tabla resumen por categoría/cantidad reservada y gráfico de barras de uso de recursos.

   * Columna Actividades: Filtro "Fechas Desde/Hasta", tabla resumen por semana/cantidad de actividades y gráfico de barras de volumen semanal.


## Jose:

#### 3. Mantenimiento de Funcionarios (Vista Admin) 

* Acceso: Administrador.

* Layout y Componentes:

   * Búsqueda: Filtros por ID y Nombre con botón "Buscar" e "Imprimir".

   * Formulario de Datos: Campos para ID, Nombre y Teléfono.

   * Controles: Botones "Guardar", "Borrar", "Limpiar" y tabla desplegable en la parte inferior con el listado de funcionarios registrados.

#### 4. Mantenimiento de Categorías de Recursos (Vista Admin)

* Acceso: Administrador.

* Layout y Componentes:

   * Búsqueda: Filtro por Descripción.

   * Formulario: Campo ID (autogenerado y bloqueado) y campo Descripción (ej. "Laptop windows 11", "Sala para 10 personas").

   * Controles: Botones CRUD ("Guardar", "Borrar", "Limpiar"), botón "Imprimir" y tabla con el listado general.

#### 5. Mantenimiento de Recursos (Vista Admin)

* Acceso: Administrador.

* Layout y Componentes:

   * Filtro: Menú desplegable para seleccionar por Categoría.

   * Formulario: ID / Número de Activo, selector de Categoría y Descripción del equipo o espacio físico.

   * Controles: Botones "Guardar", "Borrar", "Limpiar", "Imprimir" y tabla con los activos creados.





