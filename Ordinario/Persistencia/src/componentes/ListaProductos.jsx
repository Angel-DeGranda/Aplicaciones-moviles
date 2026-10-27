import { useEffect, useState, useCallback } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  RefreshControl,
  TouchableOpacity,
} from 'react-native';
import { supabase } from '../lib/supabase';
import BotonEliminar from './BotonEliminar';

export default function ListaProductos({ recarga, onEditar, onEliminado }) {
  const [productos, setProductos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [refrescando, setRefrescando] = useState(false);
  const [error, setError] = useState(null);

  const cargarProductos = useCallback(async () => {
    const { data, error } = await supabase
      .from('productos')
      .select('*')
      .order('created_at', { ascending: false });

    if (error) {
      setError(error.message);
    } else {
      setError(null);
      setProductos(data);
    }
  }, []);

  // Carga inicial y cada vez que cambia "recarga"
  useEffect(() => {
    const cargar = async () => {
      setCargando(true);
      await cargarProductos();
      setCargando(false);
    };
    cargar();
  }, [recarga, cargarProductos]);

  const alRefrescar = async () => {
    setRefrescando(true);
    await cargarProductos();
    setRefrescando(false);
  };

  if (cargando) {
    return (
      <View style={styles.centro}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.centro}>
        <Text style={styles.textoError}>❌ {error}</Text>
      </View>
    );
  }

  return (
    <FlatList
      data={productos}
      keyExtractor={(item) => String(item.id)}
      contentContainerStyle={styles.lista}
      keyboardDismissMode="on-drag"
      keyboardShouldPersistTaps="handled"
      refreshControl={
        <RefreshControl refreshing={refrescando} onRefresh={alRefrescar} />
      }
      ListEmptyComponent={
        <Text style={styles.vacio}>No hay productos todavía.</Text>
      }
      renderItem={({ item }) => (
        <View style={styles.item}>
          <View style={styles.itemInfo}>
            <Text style={styles.producto}>{item.producto}</Text>
            {!!item.descripcion && (
              <Text style={styles.descripcion}>{item.descripcion}</Text>
            )}
          </View>
          <View style={styles.derecha}>
            <Text style={styles.precio}>${Number(item.precio).toFixed(2)}</Text>
            <View style={styles.acciones}>
              <TouchableOpacity
                style={styles.botonEditar}
                onPress={() => onEditar(item)}
              >
                <Text style={styles.botonEditarTexto}>Editar</Text>
              </TouchableOpacity>
              <BotonEliminar item={item} onEliminado={onEliminado} />
            </View>
          </View>
        </View>
      )}
    />
  );
}

const styles = StyleSheet.create({
  centro: { flex: 1, alignItems: 'center', justifyContent: 'center' },
  textoError: { color: '#b91c1c', fontSize: 16, textAlign: 'center', padding: 16 },
  lista: { paddingVertical: 8 },
  vacio: { textAlign: 'center', marginTop: 24, color: '#6b7280', fontSize: 16 },
  item: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    padding: 14,
    borderRadius: 12,
    marginBottom: 10,
    elevation: 1,
  },
  acciones: { flexDirection: 'row', gap: 6 },
  itemInfo: { flex: 1, paddingRight: 8 },
  producto: { fontSize: 16, fontWeight: 'bold' },
  descripcion: { fontSize: 14, color: '#6b7280', marginTop: 2 },
  precio: { fontSize: 16, fontWeight: 'bold', color: '#16a34a' },
    derecha: { alignItems: 'flex-end', gap: 6 },
  botonEditar: {
    backgroundColor: '#2563eb',
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 6,
  },
  botonEditarTexto: { color: '#fff', fontSize: 14, fontWeight: 'bold' },
});