import { StyleSheet, Text, View, Modal, Pressable } from 'react-native';
import { useState } from 'react';

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
            <Text style={styles.ModalText}>{contenido ? contenido.valor : "Hola, Mundo"}</Text>
            <Pressable
                style={styles.buttonClose}
                onPress={onClose}
            >
                <Text>Close</Text>
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
    margin: 20,
    backgroundColor: 'white',
    borderRadius: 15,
    padding: 25,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.3,
    shadowRadius: 4,
    elevatio: 5,
  },
  ModalText: {
    marginBotton: 20,
    textAlign: 'center',
    fontSize: 20,
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
    height: 35,
    width: 100,
    backgroundColor: 'rgb(223, 101, 8)',
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 2,
    borderColor: 'rgb(157, 117, 61)',
    borderRadius: 5,
  },
});
