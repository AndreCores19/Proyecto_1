TAMANO_MAXIMO = 48
PORCION_DESCENSOS = 4
LIMITE_DESPLAZAMIENTOS = 4


def _contar_descensos(elementos, clave):
    descensos = 0
    anterior = clave(elementos[0])
    for i in range(1, len(elementos)):
        actual = clave(elementos[i])
        if actual < anterior:
            descensos += 1
        anterior = actual
    return descensos


def _insercion_limitada(elementos, clave, limite):
    resultado = list(elementos)
    desplazamientos = 0
    for i in range(1, len(resultado)):
        actual = resultado[i]
        valor = clave(actual)
        j = i - 1
        while j >= 0 and clave(resultado[j]) > valor:
            resultado[j + 1] = resultado[j]
            j -= 1
            desplazamientos += 1
            if desplazamientos > limite:
                return None
        resultado[j + 1] = actual
    return resultado


def ordenar(elementos, clave):
    n = len(elementos)
    if n <= TAMANO_MAXIMO:
        return insercion(elementos, clave)
    if _contar_descensos(elementos, clave) * PORCION_DESCENSOS <= n:
        resultado = _insercion_limitada(elementos, clave, LIMITE_DESPLAZAMIENTOS * n)
        if resultado is not None:
            return resultado
    return mergesort(elementos, clave)


def insercion(elementos, clave):
    #devuelve lista ordenada de menor a mayor
    resultado = list(elementos)
    for i in range(1, len(resultado)):
        actual = resultado[i]
        valor = clave(actual)
        j = i - 1
        while j >= 0 and clave(resultado[j]) > valor:
            resultado[j + 1] = resultado[j]
            j -= 1
        resultado[j + 1] = actual
    return resultado

def mergesort(elementos, clave):
    #devuelve lista ordenada de menor a mayor
    if len(elementos) <= 1:
        return list(elementos)
    medio = len(elementos) // 2
    izquierda = mergesort(elementos[:medio], clave)
    derecha = mergesort(elementos[medio:], clave)
    return _mezclar(izquierda, derecha, clave)


def _mezclar(izquierda, derecha, clave):
    resultado = []
    i = j = 0
    while i < len(izquierda) and j < len(derecha):
        if clave(derecha[j]) < clave(izquierda[i]):
            resultado.append(derecha[j])
            j += 1
        else:
            resultado.append(izquierda[i])
            i += 1
    while i < len(izquierda):
        resultado.append(izquierda[i])
        i += 1
    while j < len(derecha):
        resultado.append(derecha[j])
        j += 1
    return resultado