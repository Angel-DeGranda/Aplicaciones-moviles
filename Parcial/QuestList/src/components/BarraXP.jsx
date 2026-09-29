import { useEffect, useRef } from 'react';
import { View, Animated, StyleSheet } from 'react-native';

export default function BarraXP({ progreso }) {
  const ancho = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.timing(ancho, {
      toValue: progreso,
      duration: 900,
      useNativeDriver: false, 
    }).start();
  }, [progreso]);

  return (
    <View style={styles.fondo}>
      <Animated.View
        style={[
          styles.relleno,
          { width: ancho.interpolate({ inputRange: [0, 1], outputRange: ['0%', '100%'] }) },
        ]}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  fondo: { height: 14, backgroundColor: '#3730A3', borderRadius: 7, overflow: 'hidden' },
  relleno: { height: '100%', backgroundColor: '#FACC15', borderRadius: 7 },
});