import sys

from aplicacion import Aplicacion
from adapters.lector_bitacora import LectorBitacora, ErrorBitacora

URL = "https://cripta-api.kad06a0zhgs84.us-east-2.cs.amazonlightsail.com/v1"


def main():
    args = sys.argv[1:]
    if len(args) == 2 and args[0] == "--replay":
        lector = LectorBitacora(args[1])
        try:
            lector.leer()
        except (ErrorBitacora, FileNotFoundError) as error:
            print(f"No se pudo leer la bitácora: {error}")
            sys.exit(1)
        Aplicacion(URL, lector.cripta).reproducir(lector)
    elif len(args) == 1:
        Aplicacion(URL, args[0]).jugar()
    else:
        print("Uso: python main.py <ID_CRIPTA>")
        print("     python main.py --replay <archivo.log>")
        sys.exit(1)


if __name__ == "__main__":
    main()