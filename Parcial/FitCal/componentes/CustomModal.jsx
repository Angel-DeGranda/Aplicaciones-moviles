import { StyleSheet, Text, View, Modal, Pressable } from 'react-native';

export function CustomModal({ visible, onClose, contenido }) {
    return(
        <Modal
        animationType='slide'
        transparent={true}
        visible={visible}
        onRequestClose={onClose}
      >
        <View style={styles.centeredView}>
          <View style={styles.ModalView}>
            <Text style={styles.ModalText}>{contenido}</Text>
            <Pressable
                style={styles.buttonClose}
                onPress={onClose}
            >
                <Text style={styles.textoBotonModal}>Aceptar</Text>
            </Pressable>
          </View>
        </View>
      </Modal>
    );
}

const styles = StyleSheet.create({
  centeredView: {
    flex: 1,
    alignItems: 'center',
    justifyContent:'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  ModalView: {
    height: 250,
    margin: 20,
    backgroundColor: 'white',
    borderRadius: 15,
    padding: 25,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.3,
    shadowRadius: 4,
    alignItems: 'center',
    justifyContent: 'center',
    elevatio: 5,
  },
  ModalText: {
    marginBotton: 20,
    textAlign: 'center',
    fontSize: 50,
    fontWeight: '500',
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
    height: 45,
    width: 300,
    backgroundColor: 'rgb(223, 101, 8)',
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 2,
    borderColor: 'rgb(157, 117, 61)',
    borderRadius: 10,
  },
  textoBotonModal: {
    fontSize: 20,
    color: '#fff'
  },
});
