import { useState, useEffect } from 'react';
import { Modal, View, Text, TextInput, TouchableOpacity, Pressable, KeyboardAvoidingView, Platform, StyleSheet, } from 'react-native';

const MIN_NOMBRE = 2;
const MAX_NOMBRE = 20;
const MAX_FRASE = 30;
const SOLO_LETRAS = /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$/;

export default function ModalPerfil({ visible, onClose, onSave, perfil }) {
  const [nombre, setNombre] = useState('');
  const [frase, setFrase] = useState('');
  const [errores, setErrores] = useState({});

  useEffect(() => {
    if (visible) {
      setNombre(perfil?.nombre ?? '');
      setFrase(perfil?.frase ?? '');
      setErrores({});
    }
  }, [visible, perfil]);

  const validar = () => {
    const nuevos = {};
    const n = nombre.trim();

    if (n.length === 0) nuevos.nombre = 'El nombre es obligatorio';
    else if (n.length < MIN_NOMBRE) nuevos.nombre = `Mínimo ${MIN_NOMBRE} caracteres`;
    else if (n.length > MAX_NOMBRE) nuevos.nombre = `Máximo ${MAX_NOMBRE} caracteres`;
    else if (!SOLO_LETRAS.test(n)) nuevos.nombre = 'Solo letras y espacios';

    if (frase.trim().length > MAX_FRASE) nuevos.frase = `Máximo ${MAX_FRASE} caracteres`;

    setErrores(nuevos);
    return Object.keys(nuevos).length === 0;
  };

  const guardar = () => {
    if (!validar()) return;
    onSave({ nombre: nombre.trim(), frase: frase.trim() });
  };

  return (
    <Modal visible={visible} animationType="slide" transparent onRequestClose={onClose}>
      <KeyboardAvoidingView
        style={styles.overlay}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <Pressable style={StyleSheet.absoluteFill} onPress={onClose} />

        <View style={styles.caja}>
          <Text style={styles.encabezado}>Editar perfil</Text>

          <Text style={styles.etiqueta}>Nombre</Text>
          <TextInput
            style={[styles.input, errores.nombre && styles.inputError]}
            value={nombre}
            onChangeText={setNombre}
            placeholder="Tu nombre"
            maxLength={MAX_NOMBRE}
          />
          {errores.nombre && <Text style={styles.error}>{errores.nombre}</Text>}

          <Text style={styles.etiqueta}>Frase (opcional)</Text>
          <TextInput
            style={[styles.input, errores.frase && styles.inputError]}
            value={frase}
            onChangeText={setFrase}
            placeholder="Ej. Un día a la vez"
            maxLength={MAX_FRASE}
          />
          <Text style={styles.contador}>
            {frase.length}/{MAX_FRASE}
          </Text>
          {errores.frase && <Text style={styles.error}>{errores.frase}</Text>}

          <View style={styles.filaBotones}>
            <TouchableOpacity style={[styles.boton, styles.botonCancelar]} onPress={onClose}>
              <Text style={styles.botonCancelarTexto}>Cancelar</Text>
            </TouchableOpacity>
            <TouchableOpacity style={[styles.boton, styles.botonGuardar]} onPress={guardar}>
              <Text style={styles.botonGuardarTexto}>Guardar</Text>
            </TouchableOpacity>
          </View>
        </View>
      </KeyboardAvoidingView>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'flex-end' },
  caja: {
    backgroundColor: '#fff',
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    padding: 20,
  },
  encabezado: { fontSize: 20, fontWeight: 'bold', color: '#1E1B4B', marginBottom: 8 },
  etiqueta: { fontSize: 14, fontWeight: '600', color: '#1E1B4B', marginTop: 14, marginBottom: 6 },
  input: {
    borderWidth: 1,
    borderColor: '#D1D5DB',
    borderRadius: 10,
    padding: 12,
    fontSize: 15,
    backgroundColor: '#F9FAFB',
  },
  inputError: { borderColor: '#EF4444' },
  error: { color: '#EF4444', fontSize: 13, marginTop: 4 },
  contador: { alignSelf: 'flex-end', color: '#9CA3AF', fontSize: 12, marginTop: 2 },
  filaBotones: { flexDirection: 'row', marginTop: 24 },
  boton: { flex: 1, padding: 14, borderRadius: 12, alignItems: 'center', marginHorizontal: 4 },
  botonCancelar: { backgroundColor: '#E5E7EB' },
  botonCancelarTexto: { color: '#374151', fontWeight: 'bold' },
  botonGuardar: { backgroundColor: '#4F46E5' },
  botonGuardarTexto: { color: '#fff', fontWeight: 'bold' },
});