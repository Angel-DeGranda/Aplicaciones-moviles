    import { useState, useEffect } from 'react';
import { StyleSheet, Text, View, Pressable } from 'react-native';

const SIMBOLOS = ['🍎', '🍌', '🍇', '🍊', '🍉', '🍓', '🍒', '🍍'];

function generarCartas() {
  const pares = [...SIMBOLOS, ...SIMBOLOS];
  return pares
    .sort(() => Math.random() - 0.5)
    .map((simbolo, indice) => ({
      id: indice,
      simbolo,
      volteada: false,
      encontrada: false,
    }));
}

export function Memorama() {
  const [cartas, setCartas] = useState(generarCartas());
  const [seleccionadas, setSeleccionadas] = useState([]);

  useEffect(() => {
    if (seleccionadas.length === 2) {
      const [primera, segunda] = seleccionadas;

      if (cartas[primera].simbolo === cartas[segunda].simbolo) {
        // coinciden: se marcan como encontradas
        setCartas((prev) =>
          prev.map((carta, indice) =>
            indice === primera || indice === segunda
              ? { ...carta, encontrada: true }
              : carta
          )
        );
        setSeleccionadas([]);
      } else {
        // no coinciden: se voltean de nuevo después de un momento
        setTimeout(() => {
          setCartas((prev) =>
            prev.map((carta, indice) =>
              indice === primera || indice === segunda
                ? { ...carta, volteada: false }
                : carta
            )
          );
          setSeleccionadas([]);
        }, 800);
      }
    }
  }, [seleccionadas]);

  const voltearCarta = (indice) => {
    if (seleccionadas.length === 2) return; // ya hay dos volteadas, esperar
    if (cartas[indice].volteada || cartas[indice].encontrada) return; // ya visible

    setCartas((prev) =>
      prev.map((carta, i) => (i === indice ? { ...carta, volteada: true } : carta))
    );
    setSeleccionadas((prev) => [...prev, indice]);
  };

  const reiniciar = () => {
    setCartas(generarCartas());
    setSeleccionadas([]);
  };

  return (
    <View style={styles.contenedor}>
      <View style={styles.tablero}>
        {cartas.map((carta, indice) => (
          <Pressable
            key={carta.id}
            style={[
              styles.carta,
              (carta.volteada || carta.encontrada) && styles.cartaVolteada,
            ]}
            onPress={() => voltearCarta(indice)}
          >
            <Text style={styles.simbolo}>
              {carta.volteada || carta.encontrada ? carta.simbolo : '?'}
            </Text>
          </Pressable>
        ))}
      </View>

      <Pressable style={styles.boton} onPress={reiniciar}>
        <Text style={styles.textoBoton}>Reiniciar</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  contenedor: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
  },
  tablero: {
    width: 320,
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
  },
  carta: {
    width: 70,
    height: 70,
    margin: 5,
    backgroundColor: '#2563eb',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 8,
  },
  cartaVolteada: {
    backgroundColor: '#e5e7eb',
  },
  simbolo: {
    fontSize: 28,
    color: '#fff',
  },
  boton: {
    backgroundColor: '#2563eb',
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 8,
    marginTop: 24,
  },
  textoBoton: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
});