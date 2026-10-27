import { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { supabase } from '../lib/supabase';

export default function AgregarProducto({ onAgregado }) {
  const [producto, setProducto] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [precio, setPrecio] = useState('');
  const [guardando, setGuardando] = useState(false);

  const guardar = async () => {
    const precioNumero = Number(precio.replace(',', '.'));

    if (!producto.trim()) {
      Alert.alert('Falta información', 'El nombre del producto es obligatorio.');
      return;
    }
    if (precio.trim() === '' || isNaN(precioNumero) || precioNumero < 0) {
      Alert.alert('Precio inválido', 'Escribe un precio válido, por ejemplo 99.50');
      return;
    }

    setGuardando(true);

    const { error } = await supabase.from('productos').insert({
      producto: producto.trim(),
      descripcion: descripcion.trim(),
      precio: precioNumero,
    });

    setGuardando(false);

    if (error) {
      Alert.alert('Error al guardar', error.message);
      return;
    }

    Alert.alert('Listo', 'Producto agregado correctamente.');
    setProducto('');
    setDescripcion('');
    setPrecio('');
    if (onAgregado) onAgregado();
  };

  return (
    <View style={styles.card}>
      <Text style={styles.titulo}>Agregar producto</Text>

      <TextInput
        style={styles.input}
        placeholder="Producto"
        value={producto}
        onChangeText={setProducto}
      />
      <TextInput
        style={[styles.input, styles.inputMultilinea]}
        placeholder="Descripción"
        value={descripcion}
        onChangeText={setDescripcion}
        multiline
      />
      <TextInput
        style={styles.input}
        placeholder="Precio"
        value={precio}
        onChangeText={setPrecio}
        keyboardType="decimal-pad"
      />

      <TouchableOpacity
        style={[styles.boton, guardando && styles.botonDeshabilitado]}
        onPress={guardar}
        disabled={guardando}
      >
        {guardando ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={styles.botonTexto}>Guardar</Text>
        )}
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 12,
    elevation: 2,
  },
  titulo: { fontSize: 18, fontWeight: 'bold', marginBottom: 12 },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    padding: 10,
    marginBottom: 10,
    fontSize: 16,
  },
  inputMultilinea: { minHeight: 70, textAlignVertical: 'top' },
  boton: {
    backgroundColor: '#2563eb',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  botonDeshabilitado: { opacity: 0.6 },
  botonTexto: { color: '#fff', fontSize: 16, fontWeight: 'bold' },
});