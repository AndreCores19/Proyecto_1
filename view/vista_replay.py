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

    def mostrar_reporte(self, solicitudes, presupuesto, aciertos, fallos):
        print("-" * 40)
        print(f"Solicitudes realizadas: {solicitudes} de {presupuesto}")
        if presupuesto is not None:
            print(f"Presupuesto restante: {presupuesto - solicitudes}")
        print(f"Aciertos: {aciertos}, Fallos: {fallos}")
        total = aciertos + fallos
        if total > 0:
            print(f"Porcentaje de aciertos: {aciertos / total * 100:.2f}%")
        else:
            print("Porcentaje de aciertos: N/A")
        print("-" * 40)