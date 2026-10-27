import { useEffect, useState } from 'react';
import {
  Modal,
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { supabase } from '../lib/supabase';
import BotonOcultarTeclado from './BotonOcultarTeclado';

export default function EditarProducto({ item, onGuardado, onCancelar }) {
  const [nombre, setNombre] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [precio, setPrecio] = useState('');
  const [guardando, setGuardando] = useState(false);

  // Cada vez que se elige un producto, llenamos el formulario con sus datos
  useEffect(() => {
    if (item) {
      setNombre(item.producto ?? '');
      setDescripcion(item.descripcion ?? '');
      setPrecio(String(item.precio ?? ''));
    }
  }, [item]);

  const guardar = async () => {
    const precioNumero = Number(precio.replace(',', '.'));

    if (!nombre.trim()) {
      Alert.alert('Falta información', 'El nombre del producto es obligatorio.');
      return;
    }
    if (precio.trim() === '' || isNaN(precioNumero) || precioNumero < 0) {
      Alert.alert('Precio inválido', 'Escribe un precio válido, por ejemplo 99.50');
      return;
    }

    setGuardando(true);

    const { data, error } = await supabase
      .from('productos')
      .update({
        producto: nombre.trim(),
        descripcion: descripcion.trim(),
        precio: precioNumero,
      })
      .eq('id', item.id)
      .select();

    setGuardando(false);

    if (error) {
      Alert.alert('Error al actualizar', error.message);
      return;
    }

    // Sin error pero sin filas = la política de update bloqueó el cambio
    if (!data || data.length === 0) {
      Alert.alert(
        'No se actualizó',
        'No se modificó ningún registro. Revisa la política de update en Supabase.'
      );
      return;
    }

    Alert.alert('Listo', 'Producto actualizado correctamente.');
    onGuardado();
  };

  return (
    <Modal
      visible={!!item}
      transparent
      animationType="slide"
      onRequestClose={onCancelar}
    >
      <KeyboardAvoidingView
        style={styles.fondo}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <View style={styles.card}>
          <Text style={styles.titulo}>Editar producto</Text>

          <TextInput
            style={styles.input}
            placeholder="Producto"
            value={nombre}
            onChangeText={setNombre}
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

          <View style={styles.fila}>
            <TouchableOpacity
              style={[styles.boton, styles.botonCancelar]}
              onPress={onCancelar}
              disabled={guardando}
            >
              <Text style={styles.botonCancelarTexto}>Cancelar</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={[styles.boton, guardando && styles.botonDeshabilitado]}
              onPress={guardar}
              disabled={guardando}
            >
              {guardando ? (
                <ActivityIndicator color="#fff" />
              ) : (
                <Text style={styles.botonTexto}>Guardar cambios</Text>
              )}
            </TouchableOpacity>
          </View>
        </View>

        <BotonOcultarTeclado />
      </KeyboardAvoidingView>
    </Modal>
  );
}

const styles = StyleSheet.create({
  fondo: {
    flex: 1,
    justifyContent: 'center',
    padding: 16,
    backgroundColor: 'rgba(0,0,0,0.5)',
  },
  card: { backgroundColor: '#fff', padding: 16, borderRadius: 12 },
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
  fila: { flexDirection: 'row', gap: 10, marginTop: 4 },
  boton: {
    flex: 1,
    backgroundColor: '#2563eb',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  botonCancelar: { backgroundColor: '#e5e7eb' },
  botonCancelarTexto: { color: '#111827', fontSize: 16, fontWeight: 'bold' },
  botonDeshabilitado: { opacity: 0.6 },
  botonTexto: { color: '#fff', fontSize: 16, fontWeight: 'bold' },
});