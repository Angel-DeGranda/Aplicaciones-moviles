import { useState } from 'react';
import {
  TouchableOpacity,
  Text,
  StyleSheet,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { supabase } from '../lib/supabase';

export default function BotonEliminar({ item, onEliminado }) {
  const [eliminando, setEliminando] = useState(false);

  const borrar = async () => {
    setEliminando(true);

    const { data, error } = await supabase
      .from('productos')
      .delete()
      .eq('id', item.id)
      .select();

    setEliminando(false);

    if (error) {
      Alert.alert('Error al eliminar', error.message);
      return;
    }

    // Sin error pero sin filas = la política de delete bloqueó el borrado
    if (!data || data.length === 0) {
      Alert.alert(
        'No se eliminó',
        'No se borró ningún registro. Revisa la política de delete en Supabase.'
      );
      return;
    }

    if (onEliminado) onEliminado();
  };

  const confirmar = () => {
    Alert.alert(
      'Eliminar producto',
      `¿Seguro que quieres eliminar "${item.producto}"? Esta acción no se puede deshacer.`,
      [
        { text: 'Cancelar', style: 'cancel' },
        { text: 'Eliminar', style: 'destructive', onPress: borrar },
      ]
    );
  };

  return (
    <TouchableOpacity
      style={[styles.boton, eliminando && styles.deshabilitado]}
      onPress={confirmar}
      disabled={eliminando}
    >
      {eliminando ? (
        <ActivityIndicator color="#fff" size="small" />
      ) : (
        <Text style={styles.texto}>Eliminar</Text>
      )}
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  boton: {
    backgroundColor: '#dc2626',
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 6,
    minWidth: 72,
    alignItems: 'center',
  },
  deshabilitado: { opacity: 0.6 },
  texto: { color: '#fff', fontSize: 14, fontWeight: 'bold' },
});