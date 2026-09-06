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

