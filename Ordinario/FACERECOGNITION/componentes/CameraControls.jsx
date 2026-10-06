import { View, Text, Pressable, StyleSheet } from 'react-native';

export default function CameraControls({ onCapture, onFlip, disabled }) {
  return (
    <View style={styles.container}>
      {/* Espaciador izquierdo: mantiene el botón de disparo centrado */}
      <View style={styles.side} />

      <Pressable
        style={[styles.shutterOuter, disabled && styles.disabled]}
        onPress={onCapture}
        disabled={disabled}
      >
        <View style={styles.shutterInner} />
      </Pressable>

      <View style={styles.side}>
        <Pressable
          style={styles.flipButton}
          onPress={onFlip}
          disabled={disabled}
        >
          <Text style={styles.flipText}>🔄</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    bottom: 40,
    left: 0,
    right: 0,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 30,
  },
  side: {
    width: 60,
    alignItems: 'center',
  },
  shutterOuter: {
    width: 76,
    height: 76,
    borderRadius: 38,
    borderWidth: 4,
    borderColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
  },
  shutterInner: {
    width: 58,
    height: 58,
    borderRadius: 29,
    backgroundColor: '#fff',
  },
  flipButton: {
    width: 50,
    height: 50,
    borderRadius: 25,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  flipText: {
    fontSize: 24,
  },
  disabled: {
    opacity: 0.5,
  },
});