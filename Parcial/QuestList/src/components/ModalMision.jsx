import { useState, useEffect } from 'react';
import { Modal, View, Text, TextInput, TouchableOpacity, KeyboardAvoidingView, Platform, StyleSheet, Pressable, Alert } from 'react-native';

const DIFICULTADES = ['Fácil', 'Media', 'Difícil'];
const XP = { Fácil: 10, Media: 25, Difícil: 50 };
const COLOR = { Fácil: '#22C55E', Media: '#F59E0B', Difícil: '#EF4444' };

const MAX_TITULO = 40;
const MIN_TITULO = 3;
const MAX_DESCRIPCION = 150;

export default function ModalMision({ visible, onClose, onSave, mision }) {
  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [dificultad, setDificultad] = useState(null);
  const [errores, setErrores] = useState({});

  const esEdicion = !!mision;
  
  useEffect(() => {
    if (visible) {
      setTitulo(mision?.titulo ?? '');
      setDescripcion(mision?.descripcion ?? '');
      setDificultad(mision?.dificultad ?? null);
      setErrores({});
    }
  }, [visible, mision]);

  const validar = () => {
    const nuevos = {};
    const t = titulo.trim();

    if (t.length === 0) nuevos.titulo = 'El título es obligatorio';
    else if (t.length < MIN_TITULO) nuevos.titulo = `Mínimo ${MIN_TITULO} caracteres`;
    else if (t.length > MAX_TITULO) nuevos.titulo = `Máximo ${MAX_TITULO} caracteres`;

    if (descripcion.trim().length > MAX_DESCRIPCION)
      nuevos.descripcion = `Máximo ${MAX_DESCRIPCION} caracteres`;

    if (!dificultad) nuevos.dificultad = 'Elige una dificultad';

    setErrores(nuevos);
    return Object.keys(nuevos).length === 0;
  };

  const guardar = () => {
    if (!validar()) return; 
    onSave({
      titulo: titulo.trim(),
      descripcion: descripcion.trim(),
      dificultad,
    });
  };

  const hayCambios =
    titulo !== (mision?.titulo ?? '') ||
    descripcion !== (mision?.descripcion ?? '') ||
    dificultad !== (mision?.dificultad ?? null);

  const cerrarConAviso = () => {
    if (!hayCambios) {
      onClose();
      return;
    }
    Alert.alert('Descartar cambios', '¿Cerrar sin guardar?', [
      { text: 'Seguir editando', style: 'cancel' },
      { text: 'Descartar', style: 'destructive', onPress: onClose },
    ]);
  };

  return (
    <Modal visible={visible} animationType="slide" transparent onRequestClose={cerrarConAviso}>
      <KeyboardAvoidingView
        style={styles.overlay}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >

        <Pressable style={StyleSheet.absoluteFill} onPress={cerrarConAviso} />

        <View style={styles.caja}>
          <Text style={styles.encabezado}>
            {esEdicion ? 'Editar misión' : 'Nueva misión'}
          </Text>

          <Text style={styles.etiqueta}>Título</Text>
          <TextInput
            style={[styles.input, errores.titulo && styles.inputError]}
            value={titulo}
            onChangeText={setTitulo}
            placeholder="Ej. Estudiar para el examen"
            maxLength={MAX_TITULO}
          />
          {errores.titulo && <Text style={styles.error}>{errores.titulo}</Text>}

          <Text style={styles.etiqueta}>Descripción (opcional)</Text>
          <TextInput
            style={[styles.input, styles.inputMultilinea, errores.descripcion && styles.inputError]}
            value={descripcion}
            onChangeText={setDescripcion}
            placeholder="Detalles de la misión"
            multiline
            maxLength={MAX_DESCRIPCION}
          />
          <Text style={styles.contador}>
            {descripcion.length}/{MAX_DESCRIPCION}
          </Text>
          {errores.descripcion && <Text style={styles.error}>{errores.descripcion}</Text>}

          <Text style={styles.etiqueta}>Dificultad</Text>
          <View style={styles.filaDificultad}>
            {DIFICULTADES.map((d) => {
              const activa = dificultad === d;
              return (
                <TouchableOpacity
                  key={d}
                  style={[
                    styles.chip,
                    { borderColor: COLOR[d] },
                    activa && { backgroundColor: COLOR[d] },
                  ]}
                  onPress={() => setDificultad(d)}
                >
                  <Text style={[styles.chipTexto, activa && { color: '#fff' }]}>{d}</Text>
                  <Text style={[styles.chipXp, activa && { color: '#fff' }]}>+{XP[d]} XP</Text>
                </TouchableOpacity>
              );
            })}
          </View>
          {errores.dificultad && <Text style={styles.error}>{errores.dificultad}</Text>}

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
  inputMultilinea: { minHeight: 70, textAlignVertical: 'top' },
  inputError: { borderColor: '#EF4444' },
  error: { color: '#EF4444', fontSize: 13, marginTop: 4 },
  contador: { alignSelf: 'flex-end', color: '#9CA3AF', fontSize: 12, marginTop: 2 },
  filaDificultad: { flexDirection: 'row', justifyContent: 'space-between' },
  chip: {
    flex: 1,
    borderWidth: 2,
    borderRadius: 12,
    paddingVertical: 10,
    alignItems: 'center',
    marginHorizontal: 4,
  },
  chipTexto: { fontWeight: 'bold', color: '#1E1B4B' },
  chipXp: { fontSize: 12, color: '#6B7280', marginTop: 2 },
  filaBotones: { flexDirection: 'row', marginTop: 24 },
  boton: { flex: 1, padding: 14, borderRadius: 12, alignItems: 'center', marginHorizontal: 4 },
  botonCancelar: { backgroundColor: '#E5E7EB' },
  botonCancelarTexto: { color: '#374151', fontWeight: 'bold' },
  botonGuardar: { backgroundColor: '#4F46E5' },
  botonGuardarTexto: { color: '#fff', fontWeight: 'bold' },
});