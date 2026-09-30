import { useState, useRef, useCallback } from 'react';
import { View, Text, FlatList, TouchableOpacity, Alert, StyleSheet } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { Accelerometer } from 'expo-sensors';
import { Ionicons } from '@expo/vector-icons';
import { useMisiones } from '../context/MisionesContext';
import TarjetaMision from '../components/TarjetaMision';
import ModalMision from '../components/ModalMision';

const UMBRAL_AGITADO = 1.8;

export default function ListaMisiones({ navigation }) {
  const { misiones, agregarMision, limpiarCompletadas } = useMisiones();
  const [modalVisible, setModalVisible] = useState(false);

  const completadas = misiones.filter((m) => m.completada).length;

  const completadasRef = useRef(0);
  const modalRef = useRef(false);
  const limpiarRef = useRef(limpiarCompletadas);
  const bloqueadoRef = useRef(false);
  completadasRef.current = completadas;
  modalRef.current = modalVisible;
  limpiarRef.current = limpiarCompletadas;

  const liberar = () => {
    bloqueadoRef.current = false;
  };

  const preguntarLimpiar = useCallback(() => {
    if (bloqueadoRef.current) return;
    bloqueadoRef.current = true;

    const n = completadasRef.current;

    if (n === 0) {
      Alert.alert('Nada que limpiar', 'No tienes misiones completadas.', [{ text: 'OK', onPress: liberar }], {
        cancelable: true,
        onDismiss: liberar,
      });
      return;
    }

    Alert.alert(
      'Limpiar completadas',
      `¿Eliminar ${n} misión${n === 1 ? '' : 'es'} completada${n === 1 ? '' : 's'}? Tu XP se conserva.`,
      [
        { text: 'Cancelar', style: 'cancel', onPress: liberar },
        {
          text: 'Limpiar',
          style: 'destructive',
          onPress: () => {
            limpiarRef.current();
            liberar();
          },
        },
      ],
      { cancelable: true, onDismiss: liberar }
    );
  }, []);

  useFocusEffect(
    useCallback(() => {
      Accelerometer.setUpdateInterval(100);
      const suscripcion = Accelerometer.addListener(({ x, y, z }) => {
        const fuerza = Math.sqrt(x * x + y * y + z * z);
        if (fuerza > UMBRAL_AGITADO && !modalRef.current) {
          preguntarLimpiar();
        }
      });
      return () => suscripcion.remove();
    }, [preguntarLimpiar])
  );

  return (
    <View style={styles.fondo}>
      <FlatList
        data={misiones}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <TarjetaMision
            mision={item}
            onPress={() => navigation.navigate('DetalleMision', { id: item.id })}
          />
        )}
        contentContainerStyle={styles.lista}
        ListHeaderComponent={
          misiones.length > 0 ? (
            <View style={styles.barraAcciones}>
              <Text style={styles.tip}>📳 Agita el celular para limpiar las completadas</Text>
            </View>
          ) : null
        }
        ListEmptyComponent={
          <View style={styles.vacio}>
            <Text style={styles.vacioTexto}>¡Aún no tienes misiones!</Text>
            <Text style={styles.vacioSub}>Toca el botón + para crear una misión</Text>
          </View>
        }
      />

      <TouchableOpacity
        style={styles.boton}
        activeOpacity={0.8}
        onPress={() => setModalVisible(true)}
      >
        <Ionicons name="add" size={32} color="#1E1B4B" />
      </TouchableOpacity>

      <ModalMision
        visible={modalVisible}
        onClose={() => setModalVisible(false)}
        onSave={(datos) => {
          agregarMision(datos);
          setModalVisible(false);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  fondo: { flex: 1, backgroundColor: '#F5F3FF' },
  lista: { padding: 16, paddingBottom: 96 },

  barraAcciones: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  tip: { flex: 1, fontSize: 12, color: '#6B7280', marginRight: 8 },
  botonLimpiar: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#EF4444',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
  },
  botonLimpiarTexto: { color: '#fff', fontWeight: 'bold', fontSize: 13, marginLeft: 4 },

  vacio: { alignItems: 'center', marginTop: 80 },
  vacioTexto: { fontSize: 18, color: '#1E1B4B' },
  vacioSub: { fontSize: 14, color: '#6B7280', marginTop: 6 },
  boton: {
    position: 'absolute',
    right: 20,
    bottom: 20,
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: '#FACC15',
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 6,
    shadowColor: '#1E1B4B',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.4,
    shadowRadius: 8,
    elevation: 10,
  },
});