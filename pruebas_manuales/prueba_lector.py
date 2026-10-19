from adapters.lector_bitacora import LectorBitacora

lector = LectorBitacora("docs/ejemplo_partida.txt")
lector.leer()
print("Cripta:", lector.cripta)
print("Version Cripta:", lector.version_cripta)
print("Version Catalogo:", lector.version_catalogo)
print("Semilla:", lector.semilla)
print("Comandos:", lector.comandos)