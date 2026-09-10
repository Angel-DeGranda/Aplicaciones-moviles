import { useState } from 'react';
import { StyleSheet, Text, View, Pressable } from 'react-native';

const tableroVacio = Array(9).fill(null);

export function TicTacToe() {
  const [tablero, setTablero] = useState(tableroVacio);
  const [turno, setTurno] = useState('X');

  const jugar = (indice) => {
    if (tablero[indice] !== null) return; // casilla ya ocupada, no hacer nada

    const nuevoTablero = [...tablero];
    nuevoTablero[indice] = turno;
    setTablero(nuevoTablero);
    setTurno(turno === 'X' ? 'O' : 'X');
  };

  const reiniciar = () => {
    setTablero(tableroVacio);
    setTurno('X');
  };

  return (
    <View style={styles.contenedor}>
      <Text style={styles.turno}>Turno: {turno}</Text>

      <View style={styles.tablero}>
        {tablero.map((valor, indice) => (
          <Pressable
            key={indice}
            style={styles.casilla}
            onPress={() => jugar(indice)}
          >
            <Text style={styles.simbolo}>{valor}</Text>
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
  turno: {
    fontSize: 18,
    marginBottom: 20,
    fontWeight: '600',
  },
  tablero: {
    width: 300,
    height: 300,
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  casilla: {
    width: '33.33%',
    height: '33.33%',
    borderWidth: 1,
    borderColor: '#333',
    justifyContent: 'center',
    alignItems: 'center',
  },
  simbolo: {
    fontSize: 40,
    fontWeight: 'bold',
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