import { View, Text, Image, ScrollView, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { XP_POR_DIFICULTAD, XP_POR_NIVEL } from '../context/MisionesContext';

const COLOR = { Fácil: '#22C55E', Media: '#F59E0B', Difícil: '#EF4444' };

const PASOS = [
  { icono: 'add-circle-outline', texto: 'Toca el botón + en Misiones para crear una misión.' },
  { icono: 'create-outline', texto: 'Entra a una misión para completarla, editarla o eliminarla.' },
  { icono: 'phone-portrait-outline', texto: 'Agita el celular en la lista para limpiar las misiones completadas.' },
];

export default function Ayuda() {
  return (
    <ScrollView style={styles.fondo} contentContainerStyle={styles.contenido}>
      <Image source={require('../../assets/logo.png')} style={styles.logo} resizeMode="contain" />
      <Text style={styles.titulo}>¿Cómo se juega?</Text>

      <Text style={styles.seccion}>Cómo usar la app</Text>
      {PASOS.map((p) => (
        <View key={p.icono} style={styles.paso}>
          <Ionicons name={p.icono} size={26} color="#4F46E5" />
          <Text style={styles.pasoTexto}>{p.texto}</Text>
        </View>
      ))}

      <Text style={styles.seccion}>Cuánta XP da cada misión</Text>
      <View style={styles.tarjeta}>
        {Object.entries(XP_POR_DIFICULTAD).map(([dif, xp]) => (
          <View key={dif} style={styles.filaXp}>
            <View style={[styles.etiqueta, { backgroundColor: COLOR[dif] }]}>
              <Text style={styles.etiquetaTexto}>{dif}</Text>
            </View>
            <Text style={styles.xp}>+{xp} XP</Text>
          </View>
        ))}
      </View>

      <Text style={styles.seccion}>Niveles</Text>
      <Text style={styles.parrafo}>
        Cada {XP_POR_NIVEL} XP subes de nivel. Tu rango cambia con el nivel: Novato, Aventurero,
        Héroe y Leyenda. Al eliminar misiones completadas no pierdes la XP que ya ganaste.
      </Text>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  fondo: { flex: 1, backgroundColor: '#F5F3FF' },
  contenido: { padding: 20 },
  logo: { width: 90, height: 90, alignSelf: 'center' },
  titulo: { fontSize: 24, fontWeight: 'bold', color: '#1E1B4B', textAlign: 'center', marginTop: 8, marginBottom: 8 },
  seccion: { fontSize: 18, fontWeight: 'bold', color: '#1E1B4B', marginTop: 20, marginBottom: 10 },

  paso: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff', borderRadius: 12, padding: 14, marginBottom: 8, elevation: 1 },
  pasoTexto: { flex: 1, marginLeft: 12, fontSize: 15, color: '#374151' },

  tarjeta: { backgroundColor: '#fff', borderRadius: 12, padding: 14, elevation: 1 },
  filaXp: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginVertical: 6 },
  etiqueta: { paddingHorizontal: 12, paddingVertical: 4, borderRadius: 12 },
  etiquetaTexto: { color: '#fff', fontWeight: 'bold' },
  xp: { color: '#4F46E5', fontWeight: 'bold', fontSize: 16 },

  parrafo: { fontSize: 15, color: '#374151', lineHeight: 22 },
});