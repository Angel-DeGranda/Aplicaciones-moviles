import { useEffect, useRef } from 'react';
import { View, Text, Animated, StyleSheet } from 'react-native';

export default function Splash({ navigation }) {
  const opacidad = useRef(new Animated.Value(0)).current;
  const escala = useRef(new Animated.Value(0.6)).current;

  useEffect(() => {
    Animated.sequence([
      Animated.parallel([
        Animated.timing(opacidad, {
          toValue: 1,
          duration: 900,
          useNativeDriver: true,
        }),
        Animated.spring(escala, {
          toValue: 1,
          friction: 5,
          useNativeDriver: true,
        }),
      ]),
      Animated.delay(900),
    ]).start(() => {
      navigation.replace('Drawer');
    });
  }, []);

  return (
    <View style={styles.contenedor}>
      <Animated.View
        style={{ opacity: opacidad, transform: [{ scale: escala }], alignItems: 'center' }}
      >
        <Text style={styles.emoji}>📋</Text>
        <Text style={styles.titulo}>QuestList</Text>
        <Text style={styles.subtitulo}>Convierte tus tareas en misiones</Text>
      </Animated.View>
    </View>
  );
}

const styles = StyleSheet.create({
  contenedor: {
    flex: 1,
    backgroundColor: '#24232e',
    alignItems: 'center',
    justifyContent: 'center',
  },
  emoji: { fontSize: 72 },
  titulo: { fontSize: 38, fontWeight: 'bold', color: '#bf8529', marginTop: 12 },
  subtitulo: { fontSize: 15, color: '#C7D2FE', marginTop: 6 },
});