#Bitácora de prompts a LLM

## Emily Benavides

### 16 de Setiembre, 2026
- Herramienta: Claude
- Prompt: Estoy trabajando en un proyecto y necesito entender la lógica del reloj virtual,
- me puede explicar cómo se calculan las dos funciones principales de tiempo y la lógica detrás
- del tiempo que tarda una acción según la velocidad del personaje, y cómo reajustar el tiempo
- que le falta a una acción.
- Uso: entender la lógica matemática detrás del reloj virtual y entender cómo funciona.

### 18 de Setiembre, 2026
- Herramienta: Claude
- Prompt: Está bien tener Evento y AgendaEventos en el mismo .py, y Salida con Sala juntas también?
- Es que separé las clases de jugadores y enemigos en archivos distintos, pero no sé si estas clases
- más pequeñas también deberían ir separadas o si dejarlas juntas es funcional y correcto.
- Uso: saber si era una buena práctica dejarlas en el mismo archivo o separarlas todas en archivos
- individuales como hice con los personajes.

### 31 de Setiembre, 2026
- Herramienta: Claude
- Prompt: Necesito diseñar el módulo de retroceso para el juego, tomando en cuenta que con cada
- decisión del jugador pueden ocurrir múltiples eventos y no sé cómo puedo estructurar el historial
- para registrar estos cambios intermedios y revertirlos en el orden correcto.
- Uso: entender cómo implementar un sistema de retroceso que pueda manejar múltiples eventos y decisiones
- del jugador, asegurando que se pueda revertir al estado anterior de manera coherente.

### 4 de Octubre, 2026
- Herramienta: Claude
- Prompt: Ya tengo la estructura básica del historial pero necesito definir las reglas de qué
- datos de la partida deben modificarse a través de él para cumplir con el retroceso y qué
- debo tomar en cuenta al alterar atributos, listas, contadores, el generador aleatorio y la bitácora
- de la interfaz?
- Uso: saber cómo manejar correctamente los cambios en los datos del juego para que el sistema de retroceso
- funcione sin errores y mantenga la coherencia del estado del juego.

### 7 de Octubre, 2026
- Herramienta: Claude
- Prompt: Necesito escribir la sección 4.1 del Documento de Decisiones con evidencia y
- tengo tablas de tiempos del heap contra las listas para sacar el siguiente evento y para cancelar un
- evento. Mi texto de la tabla de cancelación es correcto?
- Uso: se detectó que mezclaba cifras de dos tablas y microsegundos con milisegundos y lo corregí

### 10 de Octubre, 2026
- Herramienta: Claude
- Prompt: En el rastro se puede lograr que el costo crezca solo linealmente y cuánto costaría
- hacerlo? Además, cómo funcionan los rastreadores  y el rastro en general
- Uso: entendí cómo funcionan rastreadores y el rastro


### 11 de Octubre, 2026
- Herramienta: Claude
- Prompt: Necesito benchmarks y pruebas unitarias para las estructuras que me corresponden
- (inventario, historial, rastro, bitácora y ordenamientos), siguiendo las restricciones del
- enunciado. Me ayuda a crearlos para medir cada estructura contra la alternativa descartada?
- Uso: el LLM generó un pseudocódigo base de los benchmarks y de los tests

### 11 de Octubre, 2026
- Herramienta: Claude
- Prompt: Las librerías gc, tracemalloc y copy están permitidas por el enunciado para
- los benchmarks y la lógica del juego?
- Uso: confirmar qué librerías cumplen las restricciones de la sección 7 antes de usarlas

### 11 de Octubre, 2026
- Herramienta: Claude
- Prompt: Para la sección 4.5, tiene sentido que yo escriba la parte de qué operaciones
- se usan de verdad, de dónde sale la cifra de operaciones que aparece en el texto y cómo puedo
- comprobarla yo misma en el programa o en el benchmark?
- Uso: ejecuté el benchmark de inventario en mi computadora, comprobé de dónde salía la cifra y
- usé mis propios resultados en el documento.

### 11 de Octubre, 2026
- Herramienta: Claude
- Prompt: En la bitácora, la lista fue más rápida en el benchmark. Por qué aún así es mejor
- buffer circular?
- Uso: entender por qué se justifica la memoria constante del buffer frente a la velocidad en
- tamaños pequeños

### 15 de Octubre, 2026
- Herramienta: Claude
- Prompt: Quiero que los enemigos suelten objetos al morir, pero "fabrica_modelo.py" es de
- mi compañera y no puedo editarlo sin su permiso, qué cambio habría que hacer y cuánto le afecta a ella?
- Uso: se evaluaron dos diseños en el servicio o en la fábrica. Lo hablé con mi compañera y
- decidimos aplicar el cambio en la fábrica y en "Enemigo"

### 15 de Octubre, 2026
- Herramienta: Claude
- Prompt: En el benchmark de ordenamientos, con claves costosas el cruce entre inserción y mergesort
- baja a unos 32-48 elementos. Me explica por qué pasa? Además cómo debería decidir "ordenar" entre los
- dos algoritmos sin usar sort ni sorted?
- Uso: entender por qué el cruce baja con claves costosas y aplicar el criterio de "ordenar"

### 15 de Octubre, 2026
- Herramienta: Claude
- Prompt: Revisa el proyecto completo (zip) junto con el enunciado y ayúdeme a arreglar
- el servicio "juego_service.py" para que no falle
- Uso: corrí las pruebas con los cambios, apliqué las correcciones en mis archivos


