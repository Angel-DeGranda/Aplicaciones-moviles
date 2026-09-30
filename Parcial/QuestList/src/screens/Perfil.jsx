import { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useMisiones, XP_POR_NIVEL, obtenerRango } from '../context/MisionesContext';
import BarraXP from '../components/BarraXP';
import ModalPerfil from '../components/ModalPerfil';

export default function Perfil() {
  const { misiones, xpTotal, nivel, xpEnNivel, perfil, actualizarPerfil } = useMisiones();
  const [modalVisible, setModalVisible] = useState(false);

  const completadas = misiones.filter((m) => m.completada).length;
  const pendientes = misiones.length - completadas;
  const xpFaltante = XP_POR_NIVEL - xpEnNivel;
  const rango = obtenerRango(nivel);

  return (
    <ScrollView style={styles.fondo} contentContainerStyle={styles.contenido}>
      <View style={styles.tarjetaNivel}>
        <View style={styles.avatar}>
          <Text style={styles.avatarEmoji}>{rango.emoji}</Text>
        </View>

        <Text style={styles.nombre}>{perfil.nombre}</Text>
        {perfil.frase ? <Text style={styles.frase}>"{perfil.frase}"</Text> : null}
        <Text style={styles.nivel}>
          {rango.nombre} · Nivel {nivel}
        </Text>

        <View style={styles.barra}>
          <BarraXP progreso={xpEnNivel / XP_POR_NIVEL} />
        </View>
        <Text style={styles.xpTexto}>
          {xpEnNivel} / {XP_POR_NIVEL} XP
        </Text>
        <Text style={styles.faltante}>Te faltan {xpFaltante} XP para el siguiente nivel</Text>

        <TouchableOpacity style={styles.botonEditar} onPress={() => setModalVisible(true)}>
          <Ionicons name="create-outline" size={18} color="#1E1B4B" />
          <Text style={styles.botonEditarTexto}>Editar perfil</Text>
        </TouchableOpacity>
      </View>

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

      <ModalPerfil
        visible={modalVisible}
        perfil={perfil}
        onClose={() => setModalVisible(false)}
        onSave={(datos) => {
          actualizarPerfil(datos);
          setModalVisible(false);
        }}
      />
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
  nombre: { fontSize: 22, fontWeight: 'bold', color: '#fff', marginTop: 12 },
  frase: { fontSize: 14, fontStyle: 'italic', color: '#C7D2FE', marginTop: 4 },
  nivel: { fontSize: 16, color: '#FACC15', marginTop: 6, marginBottom: 16 },
  barra: { width: '100%' },
  xpTexto: { color: '#C7D2FE', marginTop: 8 },
  faltante: { color: '#A5B4FC', fontSize: 13, marginTop: 4 },

  botonEditar: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#FACC15',
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 20,
    marginTop: 18,
  },
  botonEditarTexto: { color: '#1E1B4B', fontWeight: 'bold', marginLeft: 6 },

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