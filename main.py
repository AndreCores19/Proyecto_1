import sys
import argparse
from aplicacion import Aplicacion
from adapters.lector_bitacora import LectorBitacora, ErrorBitacora

URL = "https://cripta-api.kad06a0zhgs84.us-east-2.cs.amazonlightsail.com/v1"


def main():
    parser = argparse.ArgumentParser(description="Cripta")
    parser.add_argument("cripta", nargs="?", help="id de la cripta a jugar")
    parser.add_argument("--replay", metavar="LOG", help="reproduce una partida grabada")
    parser.add_argument("--cache-size", type=int, default=25, dest="tamano_cache",
                        help="fichas máximas en memoria (por defecto 25)")
    parser.add_argument("--bench", action="store_true",
                        help="ejecuta los benchmarks y termina")
    args = parser.parse_args()

    if args.bench:
        from bench import (bench_agenda, bench_bitacora, bench_ciclo,
                           bench_historial, bench_inventario,
                           bench_ordenamientos, bench_rastro,
                           bench_guardado, bench_cache, bench_fichas, bench_replay,
                           bench_precarga, bench_catalogo)
        for modulo in (bench_ciclo, bench_historial, bench_agenda,
                       bench_inventario, bench_bitacora, bench_ordenamientos,
                       bench_rastro, bench_cache, bench_guardado, bench_fichas,
                       bench_replay, bench_precarga, bench_catalogo):
            modulo.ejecutar()
            print()
        return

    if args.tamano_cache < 1:
        parser.error("--cache-size debe ser al menos 1")
    if args.replay:
        lector = LectorBitacora(args.replay)
        try:
            lector.leer()
        except (ErrorBitacora, FileNotFoundError) as error:
            print(f"No se pudo leer la bitácora: {error}")
            sys.exit(1)
        Aplicacion(URL, lector.cripta, args.tamano_cache).reproducir(lector)
    elif args.cripta:
        Aplicacion(URL, args.cripta, args.tamano_cache).jugar()
    else:
        parser.error("indica una cripta o usa --replay LOG")

if __name__ == "__main__":
    main()