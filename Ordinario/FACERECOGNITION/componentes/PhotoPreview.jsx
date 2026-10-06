import { View, Text, Image, Pressable, StyleSheet } from 'react-native';

export default function PhotoPreview({ uri, onSave, onDiscard, saving }) {
  return (
    <View style={styles.container}>
      <Image source={{ uri }} style={styles.image} resizeMode="contain" />

      <View style={styles.actions}>
        <Pressable
          style={[styles.button, styles.discard, saving && styles.disabled]}
          onPress={onDiscard}
          disabled={saving}
        >
          <Text style={styles.buttonText}>Descartar</Text>
        </Pressable>

        <Pressable
          style={[styles.button, styles.save, saving && styles.disabled]}
          onPress={onSave}
          disabled={saving}
        >
          <Text style={styles.buttonText}>
            {saving ? 'Guardando...' : 'Guardar'}
          </Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000',
  },
  image: {
    flex: 1,
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    paddingVertical: 24,
    paddingHorizontal: 16,
    backgroundColor: '#111',
  },
  button: {
    paddingVertical: 12,
    paddingHorizontal: 28,
    borderRadius: 8,
    minWidth: 130,
    alignItems: 'center',
  },
  discard: {
    backgroundColor: '#c0392b',
  },
  save: {
    backgroundColor: '#2e7df6',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  disabled: {
    opacity: 0.5,
  },
});