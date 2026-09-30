import { Modal, View, Text, TouchableOpacity, Pressable, StyleSheet } from 'react-native';
import { obtenerRango } from '../context/MisionesContext';

export default function ModalNivel({ visible, nivel, onSeguir, onVerPerfil }) {
  const rango = obtenerRango(nivel);

  return (
    <Modal visible={visible} animationType="fade" transparent onRequestClose={onSeguir}>
      <View style={styles.overlay}>
        <Pressable style={StyleSheet.absoluteFill} onPress={onSeguir} />

        <View style={styles.caja}>
          <View style={styles.avatar}>
            <Text style={styles.emoji}>{rango.emoji}</Text>
          </View>
          <Text style={styles.titulo}>¡Subiste de nivel!</Text>
          <Text style={styles.nivel}>Nivel {nivel}</Text>
          <Text style={styles.rango}>Rango: {rango.nombre}</Text>

          <View style={styles.fila}>
            <TouchableOpacity style={[styles.boton, styles.botonSec]} onPress={onSeguir}>
              <Text style={styles.botonSecTexto}>Seguir</Text>
            </TouchableOpacity>
            <TouchableOpacity style={[styles.boton, styles.botonPri]} onPress={onVerPerfil}>
              <Text style={styles.botonPriTexto}>Ver mi perfil</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.6)',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  caja: {
    width: '100%',
    backgroundColor: '#1E1B4B',
    borderRadius: 20,
    padding: 24,
    alignItems: 'center',
    borderWidth: 2,
    borderColor: '#FACC15',
  },
  avatar: {
    width: 96,
    height: 96,
    borderRadius: 48,
    backgroundColor: '#3730A3',
    borderWidth: 3,
    borderColor: '#FACC15',
    alignItems: 'center',
    justifyContent: 'center',
  },
  emoji: { fontSize: 48 },
  titulo: { fontSize: 24, fontWeight: 'bold', color: '#FACC15', marginTop: 16 },
  nivel: { fontSize: 20, color: '#fff', fontWeight: 'bold', marginTop: 6 },
  rango: { fontSize: 15, color: '#C7D2FE', marginTop: 4 },
  fila: { flexDirection: 'row', marginTop: 24 },
  boton: { flex: 1, padding: 14, borderRadius: 12, alignItems: 'center', marginHorizontal: 4 },
  botonSec: { backgroundColor: '#3730A3' },
  botonSecTexto: { color: '#fff', fontWeight: 'bold' },
  botonPri: { backgroundColor: '#FACC15' },
  botonPriTexto: { color: '#1E1B4B', fontWeight: 'bold' },
});