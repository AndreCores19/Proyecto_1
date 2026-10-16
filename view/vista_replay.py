class VistaReplay:
    def mostrar_estado(self, partida):
        pass

    def pedir_comando(self):
        pass

    def mostrar_mensaje(self, texto):
        print(texto)

    def mostrar_inventario(self, partida):
        pass

    def mostrar_final(self, partida):
        print("-" * 40)
        for mensaje in partida.bitacora.obtener_todos()[-6:]:
            print(f"  {mensaje}")
        print(f"Tiempo final: {partida.reloj}")
        if partida.victoria:
            print("¡Victoria! Escapaste de la cripta.")
        elif partida.terminada:
            print("Has muerto.")
        else:
            print("Saliste de la partida.")