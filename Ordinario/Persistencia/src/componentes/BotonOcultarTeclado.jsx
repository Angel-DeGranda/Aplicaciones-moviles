import { useEffect, useState } from 'react';
import { Keyboard, Platform, TouchableOpacity, Text, StyleSheet } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

export default function BotonOcultarTeclado() {
  const [visible, setVisible] = useState(false);
  const insets = useSafeAreaInsets();

  useEffect(() => {
    const eventoMostrar = Platform.OS === 'ios' ? 'keyboardWillShow' : 'keyboardDidShow';
    const eventoOcultar = Platform.OS === 'ios' ? 'keyboardWillHide' : 'keyboardDidHide';

    const subMostrar = Keyboard.addListener(eventoMostrar, () => setVisible(true));
    const subOcultar = Keyboard.addListener(eventoOcultar, () => setVisible(false));

    return () => {
      subMostrar.remove();
      subOcultar.remove();
    };
  }, []);

  if (!visible) return null;

  return (
    <TouchableOpacity
      style={[styles.boton, { top: insets.top + 8 }]}
      onPress={Keyboard.dismiss}
    >
      <Text style={styles.texto}>⌨️ Ocultar teclado</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  boton: {
    position: 'absolute',
    right: 16,
    backgroundColor: '#111827',
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: 20,
    elevation: 6,
    zIndex: 10,
  },
  texto: { color: '#fff', fontSize: 14, fontWeight: 'bold' },
});