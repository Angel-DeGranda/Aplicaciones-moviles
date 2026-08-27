import { StyleSheet, Text, View, Pressable, TextInput } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useState } from 'react';
import { CustomModal } from './componentes/CustomModal';

export default function App() {

  const [peso, setPeso] = useState(0);
  const [altura, setAltura] = useState(0.0);
  const [resultado, setResultado] = useState(0);
  const [visibleModal, setVisibleModal] = useState(false);

  const calcularIMC = () => {
    var baj = altura*altura;
    var divi = peso/baj;
    setResultado(divi)
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.vista}>
        <Text style={styles.textos}>Peso (Kg)</Text>
        <TextInput style={styles.inputs} onChangeText={(textPeso) => {setPeso(textPeso)}} placeholder='40 Kg' />
        <Text style={styles.textos}>Altura (m)</Text>
        <TextInput style={styles.inputs} onChangeText={(textAltura) => {setAltura(textAltura)}} placeholder='1.60 m' />
        <View style={styles.viewBoton}></View>
        <Pressable style={pressBoton} onPress={() => {calcularIMC(); setVisibleModal(true)}} >
          <Text style={styles.textoBoton}>Aceptar</Text>
        </Pressable>
        <CustomModal visible={visibleModal} onClose={() => {setVisibleModal(false)}} contenido={resultado} />
      </View>
    </SafeAreaView> 
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'left',
    justifyContent: 'center',
  },
  vista: {
    marginHorizontal: 10,
  },
  textos: {
    fontSize: 45,
    marginTop: 20,
  },
  inputs: {
    height: 50,
    fontSize: 40,
    borderWidth: 3,
    borderColor: '#160707',
    borderRadius: 10,
    shadowOpacity: 0.5,
    shadowColor: '#090404',
    shadowOffset: {height: 0, width: 2}
  },
  boton: {
    borderColor: '#495fb5',
    borderWidth: 3,
    height: 50,
    borderRadius: 10,
  },
  textoBoton: {
    fontSize: 35,
    textAlign: 'center',
  },
  viewBoton: {
    height: 450,
  }
});

const pressBoton = ({ pressed }) => [
  styles.boton,
  { backgroundColor: pressed ? '#8fa3f3' : '#fff' }
]