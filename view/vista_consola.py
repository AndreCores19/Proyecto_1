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
                print(f"  {enemigo.nombre} (Vida: {enemigo.vida}/{enemigo.vida_max})")
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