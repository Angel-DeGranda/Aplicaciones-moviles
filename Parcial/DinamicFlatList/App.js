import React, { useState } from 'react';
import { FlatList, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
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

  const renderCard = ({item}) => {
    <TouchableOpacity
      onPress={() => {manejarPresionCurso(item.titulo)}}
      activeOpacity={0.7}
    >
      <View>
        <Text style={styles.title} >{item.titulo}</Text>
        <Text style={styles.subtitle} >Dura: {item.duracion} | rating: {item.rating}</Text>
      </View>
    </TouchableOpacity>
  }

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.header}>Mis Cursos</Text>
      <FlatList
        data={Cursos}
        renderItem={renderCard}
        keyExtractor={item => item.id}
        contentContainerStyle={styles.listContainer}
      />
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
    backgroundColor: '#f5f5f5',
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
    backgroundColor: '#fff',
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
