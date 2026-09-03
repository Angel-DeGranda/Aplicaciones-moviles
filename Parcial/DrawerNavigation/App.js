import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createDrawerNavigator } from '@react-navigation/drawer';

import { HomeScreen } from './componentes/HomeScreen.jsx';
import { SearchScreen } from './componentes/SearchScreen.jsx';
import { ProfileScreen } from './componentes/ProfileScreen.jsx';
import { SettingsScreen } from './componentes/SettingsScreen.jsx'

const Drawer = createDrawerNavigator();

function MyDrawer(){
  return(
    <Drawer.Navigator>
      <Drawer.Screen name='Inicio' component={HomeScreen} />
      <Drawer.Screen name='Buscar' component={SearchScreen} />
      <Drawer.Screen name='Perfil' component={ProfileScreen} />
      <Drawer.Screen name='Ajustes' component={SettingsScreen} />
    </Drawer.Navigator>
  );
}

export default function App() {
  return (
    <NavigationContainer>
      <StatusBar style="Black" />
      <MyDrawer></MyDrawer>
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
