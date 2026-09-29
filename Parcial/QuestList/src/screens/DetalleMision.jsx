import { useState } from 'react';
import { View, Text, TouchableOpacity, Alert, StyleSheet } from 'react-native';
import { useMisiones } from '../context/MisionesContext';
import ModalMision from '../components/ModalMision';

const COLOR_DIFICULTAD = { Fácil: '#22C55E', Media: '#F59E0B', Difícil: '#EF4444' };

export default function DetalleMision({ route, navigation }) {
  const { id } = route.params;
  const { misiones, completarMision, eliminarMision, editarMision } = useMisiones();
  const [modalVisible, setModalVisible] = useState(false);

  const mision = misiones.find((m) => m.id === id);
  if (!mision) return null;

  const confirmarEliminar = () => {
    Alert.alert('Eliminar misión', '¿Seguro que quieres eliminar esta misión?', [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Eliminar',
        style: 'destructive',
        onPress: () => {
          eliminarMision(id);
          navigation.goBack();
        },
      },
    ]);
  };

  return (
    <View style={styles.fondo}>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text style={styles.volver}>← Volver</Text>
      </TouchableOpacity>

      <View style={styles.tarjeta}>
        <Text style={styles.titulo}>{mision.titulo}</Text>

        <View style={styles.fila}>
          <View style={[styles.etiqueta, { backgroundColor: COLOR_DIFICULTAD[mision.dificultad] }]}>
            <Text style={styles.etiquetaTexto}>{mision.dificultad}</Text>
          </View>
          <Text style={styles.xp}>+{mision.xp} XP</Text>
        </View>

        <Text style={styles.descripcion}>
          {mision.descripcion ? mision.descripcion : 'Sin descripción'}
        </Text>

        <Text style={styles.estado}>
          {mision.completada ? '✅ Misión completada' : '⏳ Pendiente'}
        </Text>
      </View>

      {!mision.completada && (
        <TouchableOpacity
          style={[styles.boton, styles.botonCompletar]}
          onPress={() => completarMision(id)}
        >
          <Text style={styles.botonTexto}>Completar misión</Text>
        </TouchableOpacity>
      )}

      {!mision.completada && (
        <TouchableOpacity
          style={[styles.boton, styles.botonEditar]}
          onPress={() => setModalVisible(true)}
        >
          <Text style={styles.botonTexto}>Editar</Text>
        </TouchableOpacity>
      )}

      <TouchableOpacity style={[styles.boton, styles.botonEliminar]} onPress={confirmarEliminar}>
        <Text style={styles.botonTexto}>Eliminar</Text>
      </TouchableOpacity>

      <ModalMision
        visible={modalVisible}
        mision={mision}
        onClose={() => setModalVisible(false)}
        onSave={(datos) => {
          editarMision(id, datos);
          setModalVisible(false);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  fondo: { flex: 1, backgroundColor: '#F5F3FF', padding: 16 },
  volver: { color: '#4F46E5', fontSize: 16, marginBottom: 12 },
  tarjeta: { backgroundColor: '#fff', borderRadius: 14, padding: 20, marginBottom: 20, elevation: 2 },
  titulo: { fontSize: 22, fontWeight: 'bold', color: '#1E1B4B' },
  fila: { flexDirection: 'row', alignItems: 'center', marginTop: 12 },
  etiqueta: { paddingHorizontal: 12, paddingVertical: 4, borderRadius: 12, marginRight: 12 },
  etiquetaTexto: { color: '#fff', fontWeight: 'bold' },
  xp: { color: '#4F46E5', fontWeight: 'bold', fontSize: 16 },
  descripcion: { marginTop: 16, fontSize: 15, color: '#374151' },
  estado: { marginTop: 16, fontSize: 15, fontWeight: '600', color: '#1E1B4B' },
  boton: { padding: 14, borderRadius: 12, alignItems: 'center', marginBottom: 10 },
  botonCompletar: { backgroundColor: '#22C55E' },
  botonEliminar: { backgroundColor: '#EF4444' },
  botonTexto: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
  botonEditar: { backgroundColor: '#4F46E5' },
});