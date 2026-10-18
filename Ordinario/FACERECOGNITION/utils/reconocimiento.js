import AsyncStorage from '@react-native-async-storage/async-storage';

// Distancia (al cuadrado) máxima para considerar que es la misma persona.
// Es un valor de partida: hay que calibrarlo probando (ver nota abajo).
export const UMBRAL = 1.0;

const CLAVE = 'rostros_registrados';

export function normalizar(vector) {
  let suma = 0;
  for (let i = 0; i < vector.length; i++) suma += vector[i] * vector[i];
  const norma = Math.sqrt(suma) || 1;
  return vector.map((v) => v / norma);
}

// Convierte face.data (texto) a números normalizados
export function aNumeros(data) {
  return normalizar(data.map((v) => Number(v)));
}

export function distancia(a, b) {
  if (!a || !b || a.length !== b.length) return Infinity;
  let d = 0;
  for (let i = 0; i < a.length; i++) {
    const x = a[i] - b[i];
    d += x * x;
  }
  return d;
}

export function promediar(lista) {
  const n = lista.length;
  const resultado = new Array(lista[0].length).fill(0);
  for (const vector of lista) {
    for (let i = 0; i < vector.length; i++) resultado[i] += vector[i] / n;
  }
  return normalizar(resultado);
}

// Devuelve { nombre, distancia }. nombre es null si no hay coincidencia.
export function identificar(embedding, registrados) {
  let mejor = null;
  let mejorDistancia = Infinity;

  for (const persona of registrados) {
    const d = distancia(embedding, persona.embedding);
    if (d < mejorDistancia) {
      mejorDistancia = d;
      mejor = persona;
    }
  }

  if (!mejor) return { nombre: null, distancia: null };
  return {
    nombre: mejorDistancia <= UMBRAL ? mejor.nombre : null,
    distancia: mejorDistancia,
  };
}

export async function cargarRostros() {
  try {
    const texto = await AsyncStorage.getItem(CLAVE);
    return texto ? JSON.parse(texto) : [];
  } catch (error) {
    return [];
  }
}

export async function guardarRostros(lista) {
  try {
    await AsyncStorage.setItem(CLAVE, JSON.stringify(lista));
  } catch (error) {
    console.log('No se pudo guardar:', error);
  }
}