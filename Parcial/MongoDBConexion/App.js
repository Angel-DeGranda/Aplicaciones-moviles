import React, { useEffect, useState } from 'react';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, FlatList, Image, ActivityIndicator, TextInput, Pressable, Modal, ScrollView, TouchableOpacity } from 'react-native';

export default function App() {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(false);

  const [username, setUsername] = useState('');

  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [password, setPassword] = useState('');
  const [loginError, setLoginError] = useState('');

  const [selectedMovie, setSelectedMovie] = useState(null);

  useEffect(() => {
    if (!isLoggedIn) return;

    setLoading(true);
    fetch('http://10.200.28.228:4000/movies')
      .then((res) => res.json())
      .then((data) => {
        setMovies(data);
        setLoading(false);
      })
      .catch((error) => {
        console.log(error);
        setLoading(false);
      });
  }, [isLoggedIn]);

  const handleLogin = () => {
    setLoginError('');
    setLoading(true);

    fetch('http://10.200.28.228:4000/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          setIsLoggedIn(true);
        } else {
          setLoginError('Contraseña incorrecta');
          setLoading(false);
        }
      })
      .catch((error) => {
        console.log(error);
        setLoginError('Error al conectar con el servidor');
        setLoading(false);
      });
  };

  if (loading) {
    return (
      <View style={styles.loader}>
        <ActivityIndicator size="large" color="#07f" />
      </View>
    );
  }

  if (!isLoggedIn) {
    return (
      <View style={styles.loginContainer}>
        <Text style={styles.loginTitle}>Iniciar sesión</Text>
        <TextInput
          style={styles.input}
          placeholder="Usuario"
          value={username}
          onChangeText={setUsername}
        />
        <TextInput
          style={styles.input}
          placeholder="Contraseña"
          secureTextEntry
          value={password}
          onChangeText={setPassword}
        />
        {loginError ? <Text style={styles.errorText}>{loginError}</Text> : null}
        <Pressable style={styles.button} onPress={handleLogin}>
          <Text style={styles.buttonText}>Entrar</Text>
        </Pressable>
        <StatusBar style="auto" />
      </View>
    );
  }

  const renderItem = ({ item }) => (
    <TouchableOpacity style={styles.card} onPress={() => setSelectedMovie(item)}>
      {item.poster ? (
        <Image source={{ uri: item.poster }} style={styles.poster} />
      ) : (
        <View style={styles.noPoster}>
          <Text>No Image</Text>
        </View>
      )}
      <View style={styles.info}>
        <Text style={styles.title}>{item.title}</Text>
        <Text>{item.fullplot || 'Sin descripción'}</Text>
      </View>
    </TouchableOpacity>
  );

  return (
    <>
      <FlatList
        data={movies}
        keyExtractor={(item) => item._id}
        renderItem={renderItem}
      />

      <Modal
        visible={!!selectedMovie}
        animationType="slide"
        onRequestClose={() => setSelectedMovie(null)}
      >
        {selectedMovie && (
          <ScrollView contentContainerStyle={styles.modalContent}>
            {selectedMovie.poster ? (
              <Image
                source={{ uri: selectedMovie.poster }}
                style={styles.modalPoster}
              />
            ) : (
              <View style={[styles.noPoster, styles.modalPoster]}>
                <Text>No Image</Text>
              </View>
            )}

            <Text style={styles.modalTitle}>{selectedMovie.title}</Text>

            <Text style={styles.modalPlot}>
              {selectedMovie.fullplot || 'Sin descripción'}
            </Text>

            <Pressable
              style={styles.closeButton}
              onPress={() => setSelectedMovie(null)}
            >
              <Text style={styles.buttonText}>Cerrar</Text>
            </Pressable>
          </ScrollView>
        )}
      </Modal>
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  loader: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loginContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 30,
    backgroundColor: '#fff',
  },
  loginTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  input: {
    width: '100%',
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    paddingHorizontal: 15,
    paddingVertical: 10,
    marginBottom: 10,
  },
  errorText: {
    color: 'red',
    marginBottom: 10,
  },
  button: {
    backgroundColor: '#07f',
    paddingVertical: 12,
    paddingHorizontal: 30,
    borderRadius: 8,
  },
  buttonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
  card: {
    flexDirection: 'row',
    padding: 10,
    margin: 10,
    backgroundColor: '#f9f9f9',
    borderRadius: 10,
  },
  poster: {
    width: 80,
    height: 120,
    borderRadius: 10,
  },
  noPoster: {
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#ddd',
  },
  info: {
    flex: 1,
    marginLeft: 10,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  plot: {
    fontSize: 12,
    color: 'gray',
  },
  modalContent: {
    padding: 20,
    alignItems: 'center',
  },
  modalPoster: {
    width: 220,
    height: 330,
    borderRadius: 12,
    marginBottom: 20,
  },
  modalTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 15,
  },
  modalPlot: {
    fontSize: 16,
    lineHeight: 24,
    textAlign: 'justify',
    marginBottom: 25,
  },
  closeButton: {
    backgroundColor: '#07f',
    paddingVertical: 12,
    paddingHorizontal: 30,
    borderRadius: 8,
  },
});