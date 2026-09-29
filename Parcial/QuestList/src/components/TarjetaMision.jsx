import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

const COLOR_DIFICULTAD = { Fácil: '#22C55E', Media: '#F59E0B', Difícil: '#EF4444' };

export default function TarjetaMision({ mision, onPress }) {
  return (
    <TouchableOpacity style={styles.tarjeta} onPress={onPress} activeOpacity={0.7}>
      <View style={styles.izquierda}>
        <Text style={[styles.titulo, mision.completada && styles.tachado]} numberOfLines={1}>
          {mision.titulo}
        </Text>
        <View style={styles.fila}>
          <View style={[styles.etiqueta, { backgroundColor: COLOR_DIFICULTAD[mision.dificultad] }]}>
            <Text style={styles.etiquetaTexto}>{mision.dificultad}</Text>
          </View>
          <Text style={styles.xp}>+{mision.xp} XP</Text>
        </View>
      </View>

      <Ionicons
        name={mision.completada ? 'checkmark-circle' : 'chevron-forward'}
        size={26}
        color={mision.completada ? '#22C55E' : '#9CA3AF'}
      />
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  tarjeta: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 10,
    elevation: 2,
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
  },
  izquierda: { flex: 1, marginRight: 12 },
  titulo: { fontSize: 16, fontWeight: '600', color: '#1E1B4B' },
  tachado: { textDecorationLine: 'line-through', color: '#9CA3AF' },
  fila: { flexDirection: 'row', alignItems: 'center', marginTop: 8 },
  etiqueta: { paddingHorizontal: 10, paddingVertical: 3, borderRadius: 10, marginRight: 10 },
  etiquetaTexto: { color: '#fff', fontSize: 12, fontWeight: 'bold' },
  xp: { color: '#4F46E5', fontWeight: 'bold' },
});