import { StyleSheet, Text, View, Modal, Pressable, SafeAreaView } from 'react-native';
import { useState } from 'react';
import { CustomModal } from './componentes/CustomModal.jsx';
import FlatListBasics from './componentes/FlatListBasics.jsx';
import SectionListBasics from './componentes/SectionListBasics.jsx';


export default function App() {

  const [modalVisible, setModalVisible]=useState(false)
  
  const objetoContenido = {
    valor: "Hola, Juan Perez",
  }
  return (
    <SafeAreaView style={styles.container}>
        <View style={styles.content}>
            <Pressable
                style={styles.buttonOpen}
                onPress={() => setModalVisible(!modalVisible)}
            >
                <Text>Ver mensaje</Text>
            </Pressable>
            <CustomModal
                visible={modalVisible}
                onClose={() => setModalVisible(false)}
                contenido={objetoContenido}
            />
            
        </View>
        <FlatListBasics/>
        <SectionListBasics/>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f4e150',
  },
  content: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  center: {
    flex: 1,
    alignItems: 'stretch',
    justifyContent:'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  buttonOpen: {
    height: 35,
    width: 100,
    backgroundColor: 'rgb(255, 158, 83)',
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 2,
    borderColor: 'rgb(157, 117, 61)',
    borderRadius: 5,
  }
});
