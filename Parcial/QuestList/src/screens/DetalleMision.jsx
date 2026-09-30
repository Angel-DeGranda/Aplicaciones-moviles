import { useState, useRef } from 'react';
import { View, Text, TouchableOpacity, Alert, Animated, StyleSheet } from 'react-native';
import { useMisiones } from '../context/MisionesContext';
import ModalMision from '../components/ModalMision';

const COLOR_DIFICULTAD = { Fácil: '#22C55E', Media: '#F59E0B', Difícil: '#EF4444' };

export default function DetalleMision({ route, navigation }) {
  const { id } = route.params;
  const { misiones, completarMision, eliminarMision, editarMision } = useMisiones();
  const [modalVisible, setModalVisible] = useState(false);

  // Valores de animación (los hooks van antes del return condicional)
  const escala = useRef(new Animated.Value(1)).current;
  const flotante = useRef(new Animated.Value(0)).current;

  const mision = misiones.find((m) => m.id === id);
  if (!mision) return null; // por si se acaba de eliminar

  const animarCompletado = () => {
    flotante.setValue(0);
    Animated.parallel([
      // Pulso de la tarjeta
      Animated.sequence([
        Animated.timing(escala, { toValue: 1.06, duration: 150, useNativeDriver: true }),
        Animated.spring(escala, { toValue: 1, friction: 4, useNativeDriver: true }),
      ]),
      // "+XP" que sube y se desvanece
      Animated.timing(flotante, { toValue: 1, duration: 1100, useNativeDriver: true }),
    ]).start();
  };

  const alCompletar = () => {
    if (mision.completada) return;
    completarMision(id);
    animarCompletado();
  };

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

      <View style={styles.zonaTarjeta}>
        <Animated.View style={[styles.tarjeta, { transform: [{ scale: escala }] }]}>
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
        </Animated.View>

        {/* +XP flotante (invisible hasta que se dispara la animación) */}
        <Animated.Text
          pointerEvents="none"
          style={[
            styles.xpFlotante,
            {
              opacity: flotante.interpolate({ inputRange: [0, 0.2, 1], outputRange: [0, 1, 0] }),
              transform: [
                { translateY: flotante.interpolate({ inputRange: [0, 1], outputRange: [0, -70] }) },
              ],
            },
          ]}
        >
          +{mision.xp} XP
        </Animated.Text>
      </View>

      {!mision.completada && (
        <TouchableOpacity style={[styles.boton, styles.botonCompletar]} onPress={alCompletar}>
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

  zonaTarjeta: { marginBottom: 20 },
  tarjeta: { backgroundColor: '#fff', borderRadius: 14, padding: 20, elevation: 2 },
  xpFlotante: {
    position: 'absolute',
    top: 16,
    right: 20,
    fontSize: 26,
    fontWeight: 'bold',
    color: '#F59E0B',
  },

  titulo: { fontSize: 22, fontWeight: 'bold', color: '#1E1B4B' },
  fila: { flexDirection: 'row', alignItems: 'center', marginTop: 12 },
  etiqueta: { paddingHorizontal: 12, paddingVertical: 4, borderRadius: 12, marginRight: 12 },
  etiquetaTexto: { color: '#fff', fontWeight: 'bold' },
  xp: { color: '#4F46E5', fontWeight: 'bold', fontSize: 16 },
  descripcion: { marginTop: 16, fontSize: 15, color: '#374151' },
  estado: { marginTop: 16, fontSize: 15, fontWeight: '600', color: '#1E1B4B' },

  boton: { padding: 14, borderRadius: 12, alignItems: 'center', marginBottom: 10 },
  botonCompletar: { backgroundColor: '#22C55E' },
  botonEditar: { backgroundColor: '#4F46E5' },
  botonEliminar: { backgroundColor: '#EF4444' },
  botonTexto: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
});