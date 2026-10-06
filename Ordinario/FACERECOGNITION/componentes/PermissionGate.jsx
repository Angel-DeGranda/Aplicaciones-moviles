import { View, Text, Pressable, StyleSheet, Linking } from 'react-native';

export default function PermissionGate({ canAskAgain, onRequest }) {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Necesitamos tus permisos</Text>
      <Text style={styles.message}>
        Para tomar y guardar fotos, la app necesita acceso a la cámara y a la
        galería.
      </Text>

      {canAskAgain ? (
        <Pressable style={styles.button} onPress={onRequest}>
          <Text style={styles.buttonText}>Dar permisos</Text>
        </Pressable>
      ) : (
        <>
          <Text style={styles.hint}>
            Los permisos fueron denegados. Actívalos desde los ajustes del
            teléfono.
          </Text>
          <Pressable style={styles.button} onPress={() => Linking.openSettings()}>
            <Text style={styles.buttonText}>Abrir ajustes</Text>
          </Pressable>
        </>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
    backgroundColor: '#111',
  },
  title: {
    color: '#fff',
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 12,
  },
  message: {
    color: '#ccc',
    fontSize: 16,
    textAlign: 'center',
    marginBottom: 24,
  },
  hint: {
    color: '#f0a500',
    fontSize: 14,
    textAlign: 'center',
    marginBottom: 16,
  },
  button: {
    backgroundColor: '#2e7df6',
    paddingVertical: 12,
    paddingHorizontal: 28,
    borderRadius: 8,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
});