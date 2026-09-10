import { useState } from 'react';
import { StyleSheet, Text, View, TextInput, Pressable } from 'react-native';

export function Imc() {
  const [peso, setPeso] = useState('');
  const [altura, setAltura] = useState('');
  const [resultado, setResultado] = useState(null);

  const calcularImc = () => {
    const pesoNum = parseFloat(peso);
    const alturaNum = parseFloat(altura);

    if (!pesoNum || !alturaNum) {
      setResultado(null);
      return;
    }

    const imc = pesoNum / (alturaNum * alturaNum);
    setResultado(imc.toFixed(2));
  };

  const obtenerCategoria = (valor) => {
    if (valor < 18.5) return 'Bajo peso';
    if (valor < 25) return 'Peso normal';
    if (valor < 30) return 'Sobrepeso';
    return 'Obesidad';
  };

  return (
    <View style={styles.contenedor}>
      <Text style={styles.etiqueta}>Peso (kg)</Text>
      <TextInput
        style={styles.input}
        value={peso}
        onChangeText={setPeso}
        keyboardType="numeric"
        placeholder="Ej. 70"
      />

      <Text style={styles.etiqueta}>Altura (m)</Text>
      <TextInput
        style={styles.input}
        value={altura}
        onChangeText={setAltura}
        keyboardType="numeric"
        placeholder="Ej. 1.75"
      />

      <Pressable style={styles.boton} onPress={calcularImc}>
        <Text style={styles.textoBoton}>Calcular IMC</Text>
      </Pressable>

      {resultado && (
        <View style={styles.resultadoContenedor}>
          <Text style={styles.resultado}>IMC: {resultado}</Text>
          <Text style={styles.categoria}>{obtenerCategoria(parseFloat(resultado))}</Text>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  contenedor: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
    paddingHorizontal: 30,
  },
  etiqueta: {
    fontSize: 14,
    marginBottom: 4,
    color: '#374151',
  },
  input: {
    borderWidth: 1,
    borderColor: '#d1d5db',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    marginBottom: 16,
    fontSize: 16,
  },
  boton: {
    backgroundColor: '#2563eb',
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 8,
  },
  textoBoton: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  resultadoContenedor: {
    marginTop: 24,
    alignItems: 'center',
  },
  resultado: {
    fontSize: 24,
    fontWeight: 'bold',
  },
  categoria: {
    fontSize: 16,
    color: '#6b7280',
    marginTop: 4,
  },
});