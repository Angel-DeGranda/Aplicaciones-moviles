import { useEffect, useRef, useState } from 'react';
import { View, Text, Animated, Image, ImageBackground, StyleSheet } from 'react-native';
import * as SplashScreen from 'expo-splash-screen';

SplashScreen.preventAutoHideAsync();

export default function Splash({ navigation }) {
  const opacidad = useRef(new Animated.Value(0)).current;
  const escala = useRef(new Animated.Value(0.6)).current;
  const [listo, setListo] = useState(false);

  useEffect(() => {
    const t = setTimeout(() => setListo(true), 3000);
    return () => clearTimeout(t);
  }, []);

  useEffect(() => {
    if (!listo) return;

    SplashScreen.hideAsync();

    Animated.sequence([
      Animated.parallel([
        Animated.timing(opacidad, { toValue: 1, duration: 900, useNativeDriver: true }),
        Animated.spring(escala, { toValue: 1, friction: 5, useNativeDriver: true }),
      ]),
      Animated.delay(900),
    ]).start(() => {
      navigation.replace('Tabs');
    });
  }, [listo]);

  return (
    <ImageBackground
      source={require('../../assets/Fond-decran-galaxie.png')}
      style={styles.contenedor}
      resizeMode="cover"
      onLoadEnd={() => setListo(true)}
    >
      <View style={styles.capa} />

      <Animated.View
        style={{ opacity: opacidad, transform: [{ scale: escala }], alignItems: 'center' }}
      >
        <Image source={require('../../assets/logo.png')} style={styles.logo} resizeMode="contain" />
        <Text style={styles.titulo}>QuestList</Text>
        <Text style={styles.subtitulo}>Convierte tus tareas en misiones</Text>
      </Animated.View>
    </ImageBackground>
  );
}

const styles = StyleSheet.create({
  contenedor: {
    flex: 1,
    backgroundColor: '#1E1B4B',
    alignItems: 'center',
    justifyContent: 'center',
  },
  capa: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(30, 27, 75, 0.35)',
  },
  logo: { width: 140, height: 140 },
  titulo: {
    fontSize: 38,
    fontWeight: 'bold',
    color: '#FACC15',
    marginTop: 12,
    textShadowColor: 'rgba(0, 0, 0, 0.75)',
    textShadowOffset: { width: 0, height: 3 },
    textShadowRadius: 8,
  },
  subtitulo: {
    fontSize: 15,
    color: '#E0E7FF',
    marginTop: 6,
    textShadowColor: 'rgba(0, 0, 0, 0.7)',
    textShadowOffset: { width: 0, height: 2 },
    textShadowRadius: 5,
  },
});