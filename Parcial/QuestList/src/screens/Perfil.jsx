import { View, Text, ScrollView, StyleSheet } from 'react-native';
import { useMisiones, XP_POR_NIVEL } from '../context/MisionesContext';
import BarraXP from '../components/BarraXP';

// El rango depende del nivel
function obtenerRango(nivel) {
  if (nivel < 3) return { nombre: 'Novato', emoji: '🌱' };
  if (nivel < 5) return { nombre: 'Aventurero', emoji: '🗡️' };
  if (nivel < 8) return { nombre: 'Héroe', emoji: '🛡️' };
  return { nombre: 'Leyenda', emoji: '👑' };
}

export default function Perfil() {
  const { misiones, xpTotal, nivel, xpEnNivel } = useMisiones();

  const completadas = misiones.filter((m) => m.completada).length;
  const pendientes = misiones.length - completadas;
  const xpFaltante = XP_POR_NIVEL - xpEnNivel;
  const rango = obtenerRango(nivel);

  return (
    <ScrollView style={styles.fondo} contentContainerStyle={styles.contenido}>
      {/* Tarjeta principal */}
      <View style={styles.tarjetaNivel}>
        <View style={styles.avatar}>
          <Text style={styles.avatarEmoji}>{rango.emoji}</Text>
        </View>
        <Text style={styles.rango}>{rango.nombre}</Text>
        <Text style={styles.nivel}>Nivel {nivel}</Text>

        <View style={styles.barra}>
          <BarraXP progreso={xpEnNivel / XP_POR_NIVEL} />
        </View>
        <Text style={styles.xpTexto}>
          {xpEnNivel} / {XP_POR_NIVEL} XP
        </Text>
        <Text style={styles.faltante}>Te faltan {xpFaltante} XP para el siguiente nivel</Text>
      </View>

      {/* Estadísticas */}
      <Text style={styles.seccion}>Estadísticas</Text>
      <View style={styles.filaStats}>
        <View style={styles.stat}>
          <Text style={styles.statNumero}>{completadas}</Text>
          <Text style={styles.statEtiqueta}>Completadas</Text>
        </View>
        <View style={styles.stat}>
          <Text style={styles.statNumero}>{pendientes}</Text>
          <Text style={styles.statEtiqueta}>Pendientes</Text>
        </View>
        <View style={styles.stat}>
          <Text style={styles.statNumero}>{xpTotal}</Text>
          <Text style={styles.statEtiqueta}>XP total</Text>
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  fondo: { flex: 1, backgroundColor: '#F5F3FF' },
  contenido: { padding: 16 },

  tarjetaNivel: {
    backgroundColor: '#1E1B4B',
    borderRadius: 16,
    padding: 24,
    alignItems: 'center',
    marginBottom: 24,
  },
  avatar: {
    width: 90,
    height: 90,
    borderRadius: 45,
    backgroundColor: '#3730A3',
    borderWidth: 3,
    borderColor: '#FACC15',
    alignItems: 'center',
    justifyContent: 'center',
  },
  avatarEmoji: { fontSize: 44 },
  rango: { fontSize: 22, fontWeight: 'bold', color: '#fff', marginTop: 12 },
  nivel: { fontSize: 16, color: '#FACC15', marginTop: 2, marginBottom: 16 },
  barra: { width: '100%' },
  xpTexto: { color: '#C7D2FE', marginTop: 8 },
  faltante: { color: '#A5B4FC', fontSize: 13, marginTop: 4 },

  seccion: { fontSize: 18, fontWeight: 'bold', color: '#1E1B4B', marginBottom: 12 },
  filaStats: { flexDirection: 'row', justifyContent: 'space-between' },
  stat: {
    flex: 1,
    backgroundColor: '#fff',
    borderRadius: 12,
    paddingVertical: 18,
    alignItems: 'center',
    marginHorizontal: 4,
    elevation: 2,
  },
  statNumero: { fontSize: 26, fontWeight: 'bold', color: '#4F46E5' },
  statEtiqueta: { fontSize: 13, color: '#6B7280', marginTop: 4 },
});