import { useEffect, useRef } from 'react';
import { View, Text, Animated, ScrollView, StyleSheet } from 'react-native';
import { useMisiones, XP_POR_NIVEL } from '../context/MisionesContext';

const COLOR_DIFICULTAD = { Fácil: '#22C55E', Media: '#F59E0B', Difícil: '#EF4444' };

export default function Inicio() {
  const { misiones, nivel, xpEnNivel } = useMisiones();

  const progreso = xpEnNivel / XP_POR_NIVEL;
  const pendientes = misiones.filter((m) => !m.completada).slice(0, 3);

  const anchoBarra = useRef(new Animated.Value(0)).current;
  useEffect(() => {
    Animated.timing(anchoBarra, {
      toValue: progreso,
      duration: 900,
      useNativeDriver: false,
    }).start();
  }, [progreso]);

  return (
    <ScrollView style={styles.fondo} contentContainerStyle={styles.contenido}>
      {/* Tarjeta de bienvenida + nivel */}
      <View style={styles.tarjetaNivel}>
        <Text style={styles.saludo}>¡Bienvenido, Angel!</Text>
        <Text style={styles.nivel}>Nivel {nivel}</Text>

        <View style={styles.barraFondo}>
          <Animated.View
            style={[
              styles.barraRelleno,
              {
                width: anchoBarra.interpolate({
                  inputRange: [0, 1],
                  outputRange: ['0%', '100%'],
                }),
              },
            ]}
          />
        </View>
        <Text style={styles.xpTexto}>
          {xpEnNivel} / {XP_POR_NIVEL} XP
        </Text>
      </View>

      {/* Misiones pendientes */}
      <Text style={styles.seccion}>Misiones pendientes</Text>

      {pendientes.length === 0 ? (
        <View style={styles.vacio}>
          <Text style={styles.vacioTexto}>🎯 No tienes misiones pendientes</Text>
          <Text style={styles.vacioSub}>Crea tu primera misión para ganar XP</Text>
        </View>
      ) : (
        pendientes.map((m) => (
          <View key={m.id} style={styles.tarjetaMision}>
            <Text style={styles.tituloMision}>{m.titulo}</Text>
            <View style={styles.filaInferior}>
              <View style={[styles.etiqueta, { backgroundColor: COLOR_DIFICULTAD[m.dificultad] }]}>
                <Text style={styles.etiquetaTexto}>{m.dificultad}</Text>
              </View>
              <Text style={styles.xpMision}>+{m.xp} XP</Text>
            </View>
          </View>
        ))
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  fondo: { flex: 1, backgroundColor: '#F5F3FF' },
  contenido: { padding: 16 },

  tarjetaNivel: {
    backgroundColor: '#1E1B4B',
    borderRadius: 16,
    padding: 20,
    marginBottom: 24,
  },
  saludo: { fontSize: 22, fontWeight: 'bold', color: '#fff' },
  nivel: { fontSize: 16, color: '#FACC15', marginTop: 4, marginBottom: 14 },
  barraFondo: {
    height: 14,
    backgroundColor: '#3730A3',
    borderRadius: 7,
    overflow: 'hidden',
  },
  barraRelleno: { height: '100%', backgroundColor: '#FACC15', borderRadius: 7 },
  xpTexto: { color: '#C7D2FE', marginTop: 8, textAlign: 'right' },

  seccion: { fontSize: 18, fontWeight: 'bold', color: '#1E1B4B', marginBottom: 12 },

  tarjetaMision: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 10,
    elevation: 2, // sombra en Android
    shadowColor: '#000', // sombra en iOS
    shadowOpacity: 0.08,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
  },
  tituloMision: { fontSize: 16, fontWeight: '600', color: '#1E1B4B' },
  filaInferior: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 10,
  },
  etiqueta: { paddingHorizontal: 10, paddingVertical: 3, borderRadius: 10 },
  etiquetaTexto: { color: '#fff', fontSize: 12, fontWeight: 'bold' },
  xpMision: { color: '#4F46E5', fontWeight: 'bold' },

  vacio: { alignItems: 'center', padding: 24 },
  vacioTexto: { fontSize: 16, color: '#1E1B4B' },
  vacioSub: { fontSize: 13, color: '#6B7280', marginTop: 4 },
});