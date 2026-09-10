import { useState } from 'react';
import { StyleSheet, Text, View, Pressable } from 'react-native';

export function Dice() {
  const [dado1, setDado1] = useState(1);
  const [dado2, setDado2] = useState(1);

  const lanzarDados = () => {
    setDado1(Math.floor(Math.random() * 6) + 1);
    setDado2(Math.floor(Math.random() * 6) + 1);
  };

  return (
    <View style={styles.contenedor}>
      <View style={styles.dados}>
        <Text style={styles.numero}>{dado1}</Text>
        <Text style={styles.numero}>{dado2}</Text>
      </View>

      <Pressable style={styles.boton} onPress={lanzarDados}>
        <Text style={styles.textoBoton}>Lanzar dados</Text>
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
  dados: {
    flexDirection: 'row',
    gap: 30,
    marginBottom: 40,
  },
  numero: {
    fontSize: 60,
    fontWeight: 'bold',
    borderWidth: 2,
    borderRadius: 12,
    paddingHorizontal: 20,
    paddingVertical: 10,
  },
  boton: {
    backgroundColor: '#2563eb',
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 8,
  },
  textoBoton: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
});