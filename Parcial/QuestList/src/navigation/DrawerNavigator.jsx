import { createDrawerNavigator } from '@react-navigation/drawer';
import { Ionicons } from '@expo/vector-icons';
import Perfil from '../screens/Perfil';
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
        name="MiPerfil"
        component={Perfil}
        options={{ title: 'Perfil', drawerIcon: icono('person-outline') }}
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