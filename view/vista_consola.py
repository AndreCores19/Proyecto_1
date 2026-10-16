
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
        print("-" * 40)
        print("Enemigos en la sala:")
        for enemigo in sala.inactivos:
            print(f"  {enemigo.nombre} (Vida: {enemigo.vida}/{enemigo.vida_max})")
        print("Trampas en la sala:")
        for trampa in sala.trampas:
            print(f"  {trampa.nombre} (Daño: {trampa.daño})")
        print("Objetos en la sala:")
        for objeto in sala.objetos:
            print(f"  {objeto.nombre}")
