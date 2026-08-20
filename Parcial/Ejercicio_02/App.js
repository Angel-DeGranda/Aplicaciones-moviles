import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import Banner from './componentes/Banner.jsx';
import RepTexto from './componentes/ReplicaTexto.jsx';

export default function App() {
  return (
    <View style={styles.container}>
      {/* <Text>Open up App.js to start working on your app!</Text>
      <Banner titulo="Welcome to UCC">
        Esto es el texto children
        <Text>Un texto</Text>
        <Banner>Recursivo</Banner>
      </Banner> */}
      <RepTexto/>
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
