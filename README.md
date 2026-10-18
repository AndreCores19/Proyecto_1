# Cripta

Juego de exploración de criptas por turnos, con tiempo virtual, hecho en Python para el curso
Estructuras de Datos (II ciclo 2026).

Integrantes:
- Emily Benavides Rodríguez
- Andrea Cordero Espinoza.

## 1. ¿Qué se necesitá para ejecutarlo?

- Python 3.11.
- Conexión a internet para consultar la API de criptas (salvo que esté usando el modo `--offline`).
- Dependencias de desarrollo: `pytest` (solo para correr las pruebas), pero el juego en sí no usa
  paquetes externos.

## 2. Instalación

Desde un entorno limpio, en la carpeta del proyecto:

`python -m venv .venv`

Activar el entorno:

- Windows (PowerShell): `.venv\Scripts\Activate.ps1`

El proyecto también incluye pyproject.toml y uv.lock. 
Si ya está uv instalado, es opcional usarlo en lugar de los pasos anteriores:

`uv sync`

Instalar la dependencia de pruebas:

`pip install pytest`

## 3. Comandos principales

Jugar una cripta:

`python main.py <id_de_la_cripta>`

Opciones:

| Opción | Qué hace |
|---|---|
| `--cache-size N` | Fichas máximas guardadas en memoria (por defecto 25). |
| `--replay LOG` | Reproduce una partida grabada en un archivo de bitácora. |
| `--offline` | **[PENDIENTE: Andrea]** describir si está disponible y cómo se usa. |
| `--bench` | **[PENDIENTE: Andrea]** ejecuta las mediciones de rendimiento (ver sección 5). |

**[PENDIENTE: Andrea]** Controles dentro del juego (moverse, abrir puerta, atacar, esperar,
recoger, usar, equipar, soltar, recorrer inventario, pergamino de retroceso, guardar, cargar y salir).

## 4. Pruebas automáticas

`python -m pytest`

Las pruebas están en `tests/` y cubren: inventario, puertas, efectos (veneno y regeneración),
botín de enemigos, ordenamientos, bitácora, servicio y una prueba aleatoria del retroceso que
compara una foto exacta del estado antes de una acción y después de retroceder.

## 5. Benchmarks

Cada archivo de `bench/` mide una estructura contra la alternativa que se descartó. Se ejecutan
desde la carpeta del proyecto, con `-m`:

| Comando | Qué mide |
|---|---|
| `python -m bench.bench_agenda` | Agenda de eventos (heap) contra listas sin ordenar y ordenada. |
| `python -m bench.bench_inventario` | Lista doblemente enlazada con cursor contra arreglo. |
| `python -m bench.bench_historial` | Retroceso con funciones de deshacer contra copiar el estado completo. |
| `python -m bench.bench_rastro` | Rastro con caducidad al consultar contra eventos y barrido de salas. |
| `python -m bench.bench_bitacora` | Buffer circular de 20 mensajes contra lista que desplaza y contra guardar todo. |
| `python -m bench.bench_ordenamientos` | Inserción, mergesort y la función híbrida `ordenar`. |
| `python -m bench.bench_ciclo` | Ciclo completo del juego (esperar y retroceder) contra el límite de 20 ms. |

Los benchmarks usan `time`, `gc`, `tracemalloc` y `copy` únicamente para medir, la lógica del
juego no los usa. Los tiempos dependen de la computadora: lo que se mantiene entre equipos es
la forma de las curvas, no los números exactos.

**[PENDIENTE: Andrea]** Si `--bench` en `main.py` ejecuta estos benchmarks, indicarlo aquí.

## 6. Archivos generados

**[PENDIENTE: Andrea]** Indicar dónde se guardan: partida guardada (archivo binario), bitácora
de la partida para `--replay` y cualquier caché en disco.

## 7. Estructura del proyecto

| Carpeta | Contenido |
|---|---|
| `model/` | Lógica del juego: partida, jugador, enemigos, salas, objetos, trampas, efectos, inventario y reloj. No conoce la red ni los archivos. |
| `structures/` | Estructuras de datos propias: agenda (heap), historial (buffer circular de cuadros), bitácora (buffer circular), lista doblemente enlazada, ordenamientos, caché y cola. |
| `service/` | `JuegoService`: fachada entre los controladores y el modelo. |
| `data/`, `dto/`, `adapters/` | Acceso a la API, lectura de fichas y archivos. |
| `controller/`, `view/` | Entrada del usuario y pantalla. |
| `bench/` | Benchmarks. |
| `tests/` | Pruebas automáticas. |

## 8. Documentación

- **Documento de Decisiones de Diseño:** [docs/Documento_de_Decisiones.pdf](docs/Documento_de_Decisiones.pdf)
- **Bitácora de prompts a LLM:** `Prompts_LLM_EmilyB.md` y `Prompts_LLM_AndreaC.md`.

## 9. Decisiones de diseño resumidas 

- **Agenda:** min heap propio con número de secuencia para desempatar y posición guardada en cada
  evento para cancelar o reprogramar en O(log n).
- **Inventario:** lista doblemente enlazada con cursor; soltar, equipar y recorrer en O(1).
- **Historial:** un cuadro por acción en un buffer circular de 5; cada cambio guarda cómo
  deshacerse. El uso del pergamino no se registra.
- **Rastro:** un instante por sala; la caducidad (400) se revisa al consultar.
- **Bitácora:** buffer circular de 20 mensajes; no forma parte del historial de retroceso.
- **Ordenamientos:** `ordenar` escoge inserción (listas de hasta 48 o casi ordenadas, con un límite
  de trabajo) o mergesort; ambos son estables.
- **Veneno y regeneración:** opcionales; solo se aplican si la ficha de la cripta los trae.
- **Botín:** lo que suelta un enemigo queda en el suelo y se registra en el historial, de modo que
  retroceder lo quita y revive al enemigo.