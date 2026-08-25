import { StyleSheet, Text, View, Modal, Pressable } from 'react-native';
import { useState } from 'react';


export default function App() {

  const [modal, setModal]=useState(true)
  return (
    <View style={styles.container}>
      <Modal
        animationType='slide'
        transparent={true}
        visible={modal}
      // configuracion del modal
      >
        <View style={styles.center}>
          <View style={styles.contenido}>
          <Text>Esto es un modal</Text>
          <Pressable
            style={styles.buttonClose}
            onPress={() => setModal(!modal)}
          >
            <Text>Close Modal</Text>
          </Pressable>
          </View>
        </View>
        {/* contenido del modal */}
      </Modal>
      <Text>Este texto está fuera del modal</Text>
      <Pressable
            style={styles.buttonOpen}
            onPress={() => setModal(!modal)}
      >
        <Text>Open Modal</Text>
      </Pressable>
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
  center: {
    flex: 1,
    alignItems: 'stretch',
    justifyContent:'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  contenido: {
    flex: 1,
    backgroundColor: 'rgb(142, 199, 230)',
    alignItems: 'center',
    justifyContent: 'center',
    marginVertical: 375,
    marginHorizontal: 110,
    borderWidth: 3,
    borderColor: 'rgb(69, 87, 96)',
    borderRadius:'15%',
  },
  buttonClose: {
    height: 35,
    width: 100,
    backgroundColor: 'rgb(223, 101, 8)',
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 2,
    borderColor: 'rgb(157, 117, 61)',
    borderRadius: 5,
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
