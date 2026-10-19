# Bitácora de prompts a LLM

## Andrea Cordero

### 20 de Setiembre, 2026
- **Herramienta:** Claude
- **Prompt:** No comprendo bien cómo funcionan las solicitudes al servicio ni el presupuesto de solicitudes. Puedes explicármelo paso a paso?
- **Uso:** comprender las solicitudes HTTP, el formato JSON y el presupuesto de solicitudes para diseñar el cliente de red.

### 20 de Setiembre, 2026
- **Herramienta:** Claude
- **Prompt:** Antes de escribir los métodos públicos del cliente, puedes explicarme con un lenguaje muy sencillo qué hace cada uno?
- **Uso:** entender el propósito de cada uno de los siete métodos públicos del cliente.

### 23 de Setiembre, 2026
- **Herramienta:** Claude
- **Prompt:** Quiero continuar con la conversión de las respuestas JSON a objetos. Puedes explicarme como aplicar los DTOs en este proyecto?
- **Uso:** entender cómo convertir las respuestas del servicio a objetos.

### 26 de Setiembre, 2026
- **Herramienta:** Claude
- **Prompt:** Podemos usar diccionarios en el proyecto? Recuerdo que el enunciado mencionaba algo al respecto.
- **Uso:** verificar qué permite y qué prohíbe el enunciado sobre el uso de `dict`.

### 3 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** Para las fichas del catálogo, sería muy inconveniente usar una clase por cada tipo de ficha?
- **Uso:** decidir cómo representar las fichas del catálogo (una clase por tipo o una sola clase).

### 3 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** Para el algoritmo de búsqueda, sería mejor la versión iterativa o la recursiva?
- **Uso:** decidir entre las dos versiones de la búsqueda binaria y justificarlo en el documento.

### 6 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** Para la caché del catálogo, qué estructura conviene usar si no puedo usar un diccionario? Necesito buscar rápido por id y saber cuál ficha usé hace más tiempo.
- **Uso:** decidir que la caché (CacheFichas) combine un vector ordenado por id con búsqueda binaria y una lista doblemente enlazada para el orden de uso (LRU). La búsqueda lineal y FIFO quedan como alternativas para medir en el benchmark.

### 10 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** Para precargar el contenido de las salas, cómo recorro el esqueleto de la cripta para pedir las salas por lotes y gastar pocas solicitudes?
- **Uso:** diseñar la precarga por lotes con recorrido por niveles usando una Cola propia, para pedir el contenido de varias salas por solicitud y cuidar el presupuesto.

### 10 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** Para construir los objetos del modelo desde las fichas del catálogo, ¿debo comparar la clase de la ficha (fichaObjetoDTO.clase) con los tipos de objeto?
- **Uso:** decidir que la fábrica del modelo (data/fabrica_modelo.py) elija qué objeto crear con un if/elif sobre la clase de la ficha, sin diccionario de funciones, para respetar la restricción de no usar dict como tabla de búsqueda.

### 12 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** ¿Por qué usar n, s, e, o para moverse? ¿No es mejor el tradicional w, a, s, d? Y mostrar_estado, ¿dónde debería vivir?
- **Uso:** decidir que el movimiento use WASD traducido a direcciones N/S/E/O en el controlador, y que `mostrar_estado` viva en la vista (VistaConsola), de modo que el controlador solo lea comandos y el modelo no imprima nada.

### 12 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** En la sala 2 de cripta-01 pierdo siempre aunque ataque. ¿Es un error del motor o está bien?
- **Uso:** verificar el motor contra el enunciado y con una simulación: la sala está casi perdida por aritmética y no por un bug. Se decidió no cambiar el motor y consultar al profesor si cripta-01 debe poder ganarse.

### 12 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** Quedé encerrada: la sala 19 solo va a la 20 y la puerta hacia la sala 5 aparece cerrada. ¿Es un error?
- **Uso:** comprender el cierre automático de puertas (la puerta se cierra sola tras cierto tiempo, en ambas caras) y decidir mostrar en la vista qué llave requiere cada puerta cerrada como mejora futura.

### 15 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** ¿Cómo diseño la bitácora de comandos para que --replay reproduzca la partida de forma fiel? ¿Qué debe llevar el encabezado y qué pasa si cambia la versión de la cripta?
- **Uso:** definir un log de texto con encabezado (cripta, versiones, semilla) y un comando por línea con flush; validar versiones antes de reproducir y medir el costo en bench_replay.

### 15 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** Descubrimos que veneno va en las trampas y regeneración en los enemigos, y Emily ya lo añadió y lo probó. Eso afecta lo que ya hicimos (caché, guardado, benchmarks)? Además, en las fichas reales de trp_dardos y ent_rata_gigante no aparece ninguno de los dos campos, ¿es un problema?
- **Uso:** comprobar que los campos nuevos no rompen la caché ni el guardado, y entender que los nombres `veneno` y `regeneracion` son una suposición nuestra porque el enunciado no nombra los campos de la ficha.

### 15 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** Revisa los benchmarks de Emily en bench/ y dime cómo agrego la opción --bench a main.py sin romper el resto de los flags.
- **Uso:** entender qué mide cada benchmark contra lo que pide la sección 5.3 del enunciado (agenda, caché, fichas expulsadas, inventario, historial, ordenamientos, guardado y carga) y agregar --bench con argparse.


### 16 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** Necesito un formato de guardado binario para la partida: ¿qué debe llevar el encabezado, cómo valido que la cripta y las versiones coinciden, y por qué es mejor que JSON?
- **Uso:** diseñar GuardadoBinario (magic, versión de formato, índice de salas al final) y compararlo con JSON midiendo tamaño y tiempos de guardar, cargar todo y leer una sola sala (bench_guardado).

### 16 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** Quiero medir cuánto cuesta una ficha según venga de memoria, de disco o de la red, y cuánto cuesta marcar las fichas en uso en cada acción del jugador. Cómo armo ese benchmark sin usar la red real?
- **Uso:** escribir bench_fichas con una fuente falsa y una cita de 400 ms por solicitud; comprobar que marcar las fichas en uso es una fracción pequeña del límite de 20 ms por ciclo.

### 17 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** Al jugar cripta-04 el programa gasta muchas solicitudes. ¿Estamos pidiendo el contenido de las salas y las fichas del catálogo de la forma más eficiente? Mide cuántas solicitudes gasta cada estrategia.
- **Uso:** descubrir que se pedía contenido en cada sala y fichas una por una; cambiar a pedir un lote de salas solo cuando falta la sala de destino y resolver las fichas de todo el lote juntas. Medimos con bench_precarga y bench_catalogo (por ejemplo, de 83,5 a 9,9 solicitudes en una cripta de 100 salas).

### 18 de Octubre, 2026
- **Herramienta:** Claude
- **Prompt:** El benchmark de precarga me da el doble de solicitudes que a ti. Aquí está mi función precargar y la salida de una prueba mínima. Por qué da diferente?
- **Uso:** encontrar con una prueba mínima que `precargar` hacía la solicitud dos veces porque quedó el bloque viejo junto al nuevo; borrar el duplicado y verificar con prueba_precarga y --bench que los conteos coinciden.




