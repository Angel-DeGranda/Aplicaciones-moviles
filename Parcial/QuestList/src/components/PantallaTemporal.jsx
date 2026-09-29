import { View, Text, StyleSheet } from 'react-native';

export default function PantallaTemporal({ titulo, children }) {
  return (
    <View style={styles.contenedor}>
      <Text style={styles.titulo}>{titulo}</Text>
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  contenedor: { flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: '#F5F3FF' },
  titulo: { fontSize: 22, fontWeight: 'bold', color: '#1E1B4B', marginBottom: 16 },
});