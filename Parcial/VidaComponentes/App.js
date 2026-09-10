import 'react-native-gesture-handler';
import { StatusBar } from 'expo-status-bar';
import React, { useEffect, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { SplashScreen } from './src/screens/SplashScreen';
import { HomeScreen } from './src/screens/HomeScreen';
import { AppNavigator } from './src/screens/AppNavigator';
import { NavigationContainer } from '@react-navigation/native';

export default function App() {

  const [ loading, setLoading ] = useState(true);
  useEffect( () => {
      setTimeout(() => {
        setLoading(false);
      }, 5000);
    }, []
  );

  if (loading) {
    return(
      <SplashScreen/>
    );
  }

  return (
    <NavigationContainer>
      <StatusBar style="auto" />
      <AppNavigator/>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
