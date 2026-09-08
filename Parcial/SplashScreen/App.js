import { StatusBar } from 'expo-status-bar';
import React, { useEffect, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';

function SplashScreen() {
  return (
    <View style={styles.splash}>
      <StatusBar style="auto" />
      <Text style={styles.logo}>

      </Text>
      <Text style={styles.title}>
        Mi Aplicación
      </Text>
      <Text>
        cargando...
      </Text>
    </View>
  );
}

function HomeScreen() {
  return(
    <View style={styles.home}>
      <Text style={styles.homeText}>
        ¡Bienvenido!
      </Text>
    </View>
  );
}

export default function App() {
  const [ loading, setLoading ] = useState(true);
  useEffect( () => {
      setTimeout(() => {
        setLoading(false);
      }, 5000);
    }, []
  );

  if (loading) {
    return <SplashScreen/>;
  }

  return (
    <HomeScreen/>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  splash: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#fff',
  },
  logo: {
    fontSize: 80,
  },
  title: {
    fontSize: 30,
  },
  home: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#fff',
  },
  homeText: {
    fontSize: 30,
  },
});

