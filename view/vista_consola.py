class VistaConsola:
    def mostrar_estado(self, partida):
        sala = partida.jugador.sala
        print("-" * 40)
        print(f"Sala {sala.id}: {sala.nombre}")
        print(f"Tiempo: {partida.reloj}")
        print(f"Jugador: {partida.jugador.nombre} "
              f"(Vida: {partida.jugador.vida}/{partida.jugador.vida_max})")
        print("Salidas:")
        for salida in sala.salidas:
            estado = "cerrada" if salida.cerrada else "abierta"
            print(f"  {salida.direccion} -> {salida.destino.nombre} ({estado})")
        print("Enemigos en la sala:")
        for enemigo in partida.enemigos:
            if enemigo.sala is sala:
                print(f"  {enemigo.nombre} [{enemigo.id_enemigo}] (Vida: {enemigo.vida}/{enemigo.vida_max})")
        print("Trampas en la sala:")
        for trampa in sala.trampas:
            estado = "armada" if trampa.armada else "desarmada"
            print(f"  {trampa.nombre} (Daño: {trampa.dano}, {estado})")
        print("Objetos en la sala:")
        for objeto in sala.objetos:
            print(f"  [{objeto.id_instancia}] {objeto.nombre}")
        print("Bitácora:")
        for mensaje in partida.bitacora.obtener_todos()[-6:]:
            print(f"  {mensaje}")
        print("-" * 40)

    def mostrar_final(self, partida):
        print("-" * 40)
        for mensaje in partida.bitacora.obtener_todos()[-6:]:
            print(f"  {mensaje}")
        if partida.victoria:
            print("¡Victoria! Escapaste de la cripta.")
        elif partida.terminada:
            print("Has muerto.")
        else:
            print("Saliste de la partida.")

    def mostrar_mensaje(self, texto):
        print(texto)

    def pedir_comando(self):
        return input("> ")

    def mostrar_inventario(self, partida):
        inv = partida.jugador.inventario
        print(f"Inventario ({inv.tamano}/{inv.capacidad_max}):")
        if inv.tamano == 0:
            print("  (vacío)")
            return
        nodo = inv.cabeza
        while nodo is not None:
            objeto = nodo.objeto
            marca = ">" if nodo is inv.actual else " "
            equipo = " (equipado)" if objeto.equipado else ""
            print(f" {marca} [{objeto.id_instancia}] {objeto.nombre}{equipo}")
            nodo = nodo.siguiente

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