import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import { DemoImagen } from './componentes/DemoImagen.jsx';

export default function App() {
  return (
    <View style={styles.container}>
      <DemoImagen/>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#4dd8a7',
  },
  panel1: {
    flex: 1,
    backgroundColor: '#f1df56',
  },
  panel2: {
    flex: 1,
    backgroundColor: '#d7452e',
  },
  panel3: {
    flex: 1,
    backgroundColor: '#2028b5',
  }
});
