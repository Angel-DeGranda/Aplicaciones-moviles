// Zona de importación
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import Cat from './componentes/Cat.jsx';
import Mensaje from './componentes/Mensaje.jsx'

//la logica del componente
export default function App() {
  // Codigo

  return ( // Vista o View
    //todo dentro de aqui tiene que estar en un view o en un fragmente
    //Lo de verde es un componente, y normalmente tiene una etiqueta de apertura y una de cierre
    <View style={styles.container}>
      <Text>Open up App.js to start working on your app!</Text>
      <Text style={styles.texto_rojo}>Este es otro componente de texto</Text>
      <Cat/>
      <Mensaje msg="Mi mensaje como propiedad." num="100"/>
    </View>
  );
}

//los estilos del componente
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ffffff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  texto_rojo: {
    color: 'red'
  }
});
