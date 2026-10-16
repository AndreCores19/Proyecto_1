from adapters.lector_bitacora import LectorBitacora

lector = LectorBitacora("saves/partida_cripta-02_2440366726.log")
lector.leer()
print("Cripta:", lector.cripta)
print("Version Cripta:", lector.version_cripta)
print("Version Catalogo:", lector.version_catalogo)
print("Semilla:", lector.semilla)
print("Comandos:", lector.comandos)