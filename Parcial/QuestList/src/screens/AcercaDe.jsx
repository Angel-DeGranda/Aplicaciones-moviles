import { View, Text, Image, ScrollView, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

const AUTOR = 'Angel De Granda';
const VERSION = '1.0.0';
const TECNOLOGIAS = [ 'React Native', 'Expo', 'Splash Screen', 'Stack Navigator', 'Tab Bar', 'Drawer', 'Modales', 'FlatList', 'Sensores (acelerómetro)', 'Animaciones (Animated)', 'Imágenes', ];

export default function AcercaDe() {
  return (
    <ScrollView style={styles.fondo} contentContainerStyle={styles.contenido}>
      <Image source={require('../../assets/logo.png')} style={styles.logo} resizeMode="contain" />
      <Text style={styles.nombre}>QuestList</Text>
      <Text style={styles.lema}>Convierte tus tareas en misiones</Text> 
      <Text style={styles.version}>Versión {VERSION}</Text>

      <View style={styles.tarjeta}>
        <View style={styles.fila}>
          <Ionicons name="person-outline" size={20} color="#4F46E5" />
          <Text style={styles.filaTexto}>Desarrollado por {AUTOR}</Text>
        </View>
        <View style={styles.fila}>
          <Ionicons name="phone-portrait-outline" size={20} color="#4F46E5" />
          <Text style={styles.filaTexto}>App móvil 100 % local, sin conexión a internet</Text>
        </View>
      </View>

      <Text style={styles.seccion}>Tecnologías</Text>
      <View style={styles.chips}>
        {TECNOLOGIAS.map((t) => (
          <View key={t} style={styles.chip}>
            <Text style={styles.chipTexto}>{t}</Text>
          </View>
        ))}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  fondo: { flex: 1, backgroundColor: '#F5F3FF' },
  contenido: { padding: 24, alignItems: 'center' },
  logo: { width: 140, height: 140, marginTop: 8 },
  nombre: { fontSize: 30, fontWeight: 'bold', color: '#1E1B4B', marginTop: 12 },
  lema: { fontSize: 15, color: '#6B7280', marginTop: 4 },
  version: { fontSize: 13, color: '#9CA3AF', marginTop: 4, marginBottom: 24 },

  tarjeta: { width: '100%', backgroundColor: '#fff', borderRadius: 14, padding: 16, elevation: 2, marginBottom: 24 },
  fila: { flexDirection: 'row', alignItems: 'center', marginVertical: 6 },
  filaTexto: { marginLeft: 10, fontSize: 15, color: '#374151', flex: 1 },

  seccion: { alignSelf: 'flex-start', fontSize: 18, fontWeight: 'bold', color: '#1E1B4B', marginBottom: 10 },
  chips: { flexDirection: 'row', flexWrap: 'wrap', alignSelf: 'flex-start' },
  chip: { backgroundColor: '#E0E7FF', borderRadius: 16, paddingHorizontal: 12, paddingVertical: 6, marginRight: 8, marginBottom: 8 },
  chipTexto: { color: '#3730A3', fontSize: 13, fontWeight: '600' },
});