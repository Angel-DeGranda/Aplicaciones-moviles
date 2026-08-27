import React, { useState } from 'react';
import { StatusBar } from 'expo-status-bar';
import { FlatList, SafeAreaView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import CustomModal from './componentes/CustomModal.jsx';

const Cursos = [
  {id: '1', titulo: 'Programacion movil', duracion: '10 horas', rating: '4.8'},
  {id: '2', titulo: 'IA para ingenieros', duracion: '20 horas', rating: '4.9'},
  {id: '3', titulo: 'Aplicaciones web', duracion: '18 horas', rating: '4.2'},
  {id: '4', titulo: 'Base de datos', duracion: '15 horas', rating: '5.0'},
  {id: '5', titulo: 'Office', duracion: '30 horas', rating: '4.6'},
];

export default function App() {
  const [modalVisible, setModalVisible] = useState(false);
  const [cursoSeleccionado, setCursoSeleccionado] = useState(null);

  const manejarPresionCurso = (tituloCurso) => {
    setCursoSeleccionado({valor: tituloCurso});
    setModalVisible(true);
  }

  const renderCard = ({ item }) => {
    return(
      <TouchableOpacity
        onPress={() => {manejarPresionCurso(item.titulo)}}
        activeOpacity={0.7}
        style={styles.card}
      >
        <View>
            <Text style={styles.title} >{item.titulo}</Text>
            <Text style={styles.subtitle} >Dura: {item.duracion} | rating: {item.rating}</Text>
        </View>
      </TouchableOpacity>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <View>
        <StatusBar style="black"/>
        <Text style={styles.header}>Mis Cursos</Text>
        <FlatList
          data={Cursos}
          keyExtractor={(item) => item.id}
          renderItem={renderCard}
          contentContainerStyle={styles.listContainer}
        />
        
      </View>
      <CustomModal
        visible={modalVisible}
        onClose={() => setModalVisible(false)}
        contenido={cursoSeleccionado}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ffffff',
  },
  header:{
    fontSize: 24,
    fontWeight: '600',
    padding: 16,
    color: '#1a1a1a',
  },
  listContainer: {
    paddingHorizontal: 16,
    paddingBottom: 16,
  },
  card: {
    backgroundColor: '#d0d0d0',
    padding: 16,
    marginVertical: 8,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2}
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 14,
    color: '#667667',
  },
});
