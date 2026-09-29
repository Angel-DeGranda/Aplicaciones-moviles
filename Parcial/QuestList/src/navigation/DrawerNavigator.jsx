import { createDrawerNavigator } from '@react-navigation/drawer';
import { Ionicons } from '@expo/vector-icons';
import Inicio from '../screens/Inicio';
import TabNavigator from './TabNavigator';
import Ayuda from '../screens/Ayuda';
import AcercaDe from '../screens/AcercaDe';

const Drawer = createDrawerNavigator();

const icono = (nombre) => ({ color, size }) => (
  <Ionicons name={nombre} size={size} color={color} />
);

export default function DrawerNavigator() {
  return (
    <Drawer.Navigator
      screenOptions={{
        headerStyle: { backgroundColor: '#1E1B4B' },
        headerTintColor: '#fff',
        drawerActiveTintColor: '#4F46E5',
      }}
    >
      <Drawer.Screen
        name="Inicio"
        component={Inicio}
        options={{ drawerIcon: icono('home-outline') }}
      />
      <Drawer.Screen
        name="MisMisiones"
        component={TabNavigator}
        options={{ title: 'Mis Misiones', drawerIcon: icono('flag-outline') }}
      />
      <Drawer.Screen
        name="Ayuda"
        component={Ayuda}
        options={{ drawerIcon: icono('help-circle-outline') }}
      />
      <Drawer.Screen
        name="AcercaDe"
        component={AcercaDe}
        options={{ title: 'Acerca de', drawerIcon: icono('information-circle-outline') }}
      />
    </Drawer.Navigator>
  );
}