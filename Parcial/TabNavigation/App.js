import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { HomeScreen } from './src/Screens/HomeScreen';
import { ProfileScreen } from './src/Screens/ProfileScreen';
import { SearchScreen } from './src/Screens/SearchScreen';

const Tab = createBottomTabNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Tab.Navigator
        screenOptions={{
          headerShown: false,
          tabBarActiveTintColor: '#dc3f3f',
          tabBarInactiveTintColor: '#4d4c4c',
        }}
      >
        <Tab.Screen name='Inicio' component={HomeScreen} />
        <Tab.Screen name='Buscar' component={SearchScreen} />
        <Tab.Screen name='Perfil' component={ProfileScreen} />
      </Tab.Navigator>
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
