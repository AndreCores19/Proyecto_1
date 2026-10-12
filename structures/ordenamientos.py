TAMANO_MAXIMO = 48

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


def ordenar(elementos, clave):
    if len(elementos) <= TAMANO_MAXIMO:
        return insercion(elementos, clave)
    return mergesort(elementos, clave)