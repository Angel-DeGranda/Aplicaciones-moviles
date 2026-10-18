import { View, Text, StyleSheet } from 'react-native';

export default function FaceOverlay({ rostro }) {
  if (!rostro) return null;

  const { bounds, nombre, hayRegistrados } = rostro;
  const color = nombre ? '#2ecc71' : hayRegistrados ? '#e74c3c' : '#f1c40f';
  const etiqueta = nombre ?? (hayRegistrados ? 'Desconocido' : 'Rostro detectado');

  return (
    <View
      pointerEvents="none"
      style={[
        styles.caja,
        {
          left: bounds.x,
          top: bounds.y,
          width: bounds.width,
          height: bounds.height,
          borderColor: color,
        },
      ]}
    >
      <Text style={[styles.etiqueta, { backgroundColor: color }]}>{etiqueta}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  caja: {
    position: 'absolute',
    borderWidth: 3,
    borderRadius: 6,
  },
  etiqueta: {
    position: 'absolute',
    top: -28,
    left: -3,
    paddingHorizontal: 8,
    paddingVertical: 3,
    color: '#000',
    fontWeight: '700',
    fontSize: 14,
    borderRadius: 4,
    overflow: 'hidden',
  },
});