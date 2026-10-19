class VistaConsola:
    def mostrar_estado(self, partida):
        sala = partida.jugador.sala
        print("-" * 40)
        print(f"Sala {sala.id}: {sala.nombre}")
        print(f"Tiempo: {partida.reloj}")
        print(f"Jugador: {partida.jugador.nombre} "
              f"(Vida: {partida.jugador.vida}/{partida.jugador.vida_max})")
        jugador = partida.jugador
        inv = jugador.inventario
        arma = inv.arma_equipada
        armadura = inv.armadura_equipada
        print(f"  Ataque: {jugador.ataque}  Defensa: {jugador.defensa}  Velocidad: {jugador.velocidad}")
        print(f"  Arma: {arma.nombre if arma is not None else 'ninguna'}")
        print(f"  Armadura: {armadura.nombre if armadura is not None else 'ninguna'}")
        efectos = ", ".join(e.tipo for e in jugador.efectos)
        print(f"  Efectos: {efectos if efectos else 'ninguno'}")

        print("Salidas:")
        for salida in sala.salidas:
            estado = "cerrada" if salida.cerrada else "abierta"
            print(f"  {salida.direccion} -> {salida.destino.nombre} ({estado})")
        print("Enemigos en la sala:")
        for enemigo in partida.enemigos:
            if enemigo.sala is sala:
                efectos = ", ".join(e.tipo for e in enemigo.efectos)
                linea = (f"  {enemigo.nombre} [{enemigo.id_enemigo}] "
                         f"(Vida: {enemigo.vida}/{enemigo.vida_max})")
                if efectos:
                    linea += f" [{efectos}]"
                print(linea)
        print("Trampas en la sala:")
        for trampa in sala.trampas:
            estado = "armada" if trampa.armada else "desarmada"
            print(f"  {trampa.nombre} (Daño: {trampa.dano}, {estado})")
        print("Objetos en la sala:")
        for objeto in sala.objetos:
            print(f"  [{objeto.id_instancia}] {objeto.nombre}")
        print("Bitácora:")
        for mensaje in partida.bitacora.obtener_todos()[-20:]:
            print(f"  {mensaje}")
        print("-" * 40)

    def mostrar_final(self, partida):
        print("-" * 40)
        for mensaje in partida.bitacora.obtener_todos()[-20:]:
            print(f"  {mensaje}")
        print(f"Tiempo final: {partida.reloj}")
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
            print(f" {marca} [{objeto.id_instancia}] {objeto.nombre}{equipo} (peso {objeto.peso}, valor {objeto.valor})")
            nodo = nodo.siguiente

    def mostrar_inventario_ordenado(self, objetos, criterio):
        print(f"Inventario ordenado por {criterio}:")
        if not objetos:
            print("  (vacío)")
            return
        for objeto in objetos:
            print(f"  [{objeto.id_instancia}] {objeto.nombre} "
                  f"(peso {objeto.peso}, valor {objeto.valor})")

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

    def pedir_nombre(self):
        nombre = input("Nombre del jugador: ").strip()
        return nombre if nombre else "Jugador"

    def elegir_cripta(self, criptas):
        print("Criptas disponibles:")
        for i, c in enumerate(criptas, 1):
            print(f"  {i}. {c['nombre']} ({c['id']}) - "
                  f"{c['salas']} salas, dificultad {c['dificultad']}")
        while True:
            texto = input("Elige una cripta (número, o 'salir'): ").strip().lower()
            if texto == "salir":
                return None
            if texto.isdigit() and 1 <= int(texto) <= len(criptas):
                return criptas[int(texto) - 1]["id"]
            print("Opción no válida.")

    def mostrar_puntajes(self, puntajes):
        print("Tabla de puntajes:")
        print(f"  {'#':>2} {'Jugador':<14} {'Cripta':<10} {'Ver.':<6} "
              f"{'Acc.':>5} {'Derr.':>5} {'Tiempo':>7}  Resultado")
        for i, p in enumerate(puntajes[:10], 1):
            resultado = "victoria" if p.victoria else "derrota"
            print(f"  {i:>2} {p.nombre:<14} {p.id_cripta:<10} {p.version:<6} "
                  f"{p.acciones:>5} {p.derrotados:>5} {p.tiempo:>7}  {resultado}")