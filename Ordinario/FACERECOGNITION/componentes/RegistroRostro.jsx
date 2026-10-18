import { useState } from 'react';
import { Modal, View, Text, TextInput, Pressable, StyleSheet } from 'react-native';

export default function RegistroRostro({ visible, onGuardar, onCancelar }) {
  const [nombre, setNombre] = useState('');

  const guardar = () => {
    const limpio = nombre.trim();
    if (!limpio) return;
    onGuardar(limpio);
    setNombre('');
  };

  const cancelar = () => {
    setNombre('');
    onCancelar();
  };

  return (
    <Modal visible={visible} transparent animationType="fade" onRequestClose={cancelar}>
      <View style={styles.fondo}>
        <View style={styles.tarjeta}>
          <Text style={styles.titulo}>Registrar rostro</Text>
          <TextInput
            style={styles.input}
            placeholder="Nombre de la persona"
            placeholderTextColor="#888"
            value={nombre}
            onChangeText={setNombre}
            autoFocus
          />
          <View style={styles.fila}>
            <Pressable style={[styles.boton, styles.cancelar]} onPress={cancelar}>
              <Text style={styles.textoBoton}>Cancelar</Text>
            </Pressable>
            <Pressable style={[styles.boton, styles.guardar]} onPress={guardar}>
              <Text style={styles.textoBoton}>Guardar</Text>
            </Pressable>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  fondo: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.6)',
    justifyContent: 'center',
    padding: 24,
  },
  tarjeta: {
    backgroundColor: '#1c1c1e',
    borderRadius: 12,
    padding: 20,
  },
  titulo: {
    color: '#fff',
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 12,
  },
  input: {
    backgroundColor: '#2c2c2e',
    color: '#fff',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 16,
    marginBottom: 16,
  },
  fila: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    gap: 12,
  },
  boton: {
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 8,
  },
  cancelar: { backgroundColor: '#555' },
  guardar: { backgroundColor: '#2e7df6' },
  textoBoton: { color: '#fff', fontWeight: '600', fontSize: 15 },
});