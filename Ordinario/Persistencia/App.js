import { useState } from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';
import AgregarProducto from './src/componentes/AgregarProducto';
import ListaProductos from './src/componentes/ListaProductos';
import EditarProducto from './src/componentes/EditarProducto';
import BotonOcultarTeclado from './src/componentes/BotonOcultarTeclado';

export default function App() {
  const [recarga, setRecarga] = useState(0);
  const [productoEditando, setProductoEditando] = useState(null);

  const recargarLista = () => setRecarga((n) => n + 1);

  return (
    <SafeAreaProvider>
      <SafeAreaView style={styles.container}>
        <View style={styles.contenido}>
          <AgregarProducto onAgregado={recargarLista} />
          <Text style={styles.subtitulo}>Productos</Text>
          <View style={styles.zonaLista}>
            <ListaProductos
              recarga={recarga}
              onEditar={setProductoEditando}
              onEliminado={recargarLista}
            />
          </View>
        </View>

        <EditarProducto
          item={productoEditando}
          onCancelar={() => setProductoEditando(null)}
          onGuardado={() => {
            setProductoEditando(null);
            recargarLista();
          }}
        />

        <BotonOcultarTeclado />
      </SafeAreaView>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f3f4f6' },
  contenido: { flex: 1, padding: 16 },
  subtitulo: { fontSize: 18, fontWeight: 'bold', marginTop: 16, marginBottom: 4 },
  zonaLista: { flex: 1 },
});