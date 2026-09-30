import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import Inicio from '../screens/Inicio';
import MisionesStack from './MisionesStack';
import DrawerNavigator from './DrawerNavigator';

const Tab = createBottomTabNavigator();

const icono = (nombre) => ({ color, size }) => (
  <Ionicons name={nombre} size={size} color={color} />
);

export default function TabNavigator() {
  return (
    <Tab.Navigator
      screenOptions={{
        headerStyle: { backgroundColor: '#1E1B4B' },
        headerTintColor: '#fff',
        tabBarActiveTintColor: '#4F46E5',
        tabBarInactiveTintColor: '#9CA3AF',
      }}
    >

      <Tab.Screen
        name="Inicio"
        component={Inicio}
        options={{ tabBarIcon: icono('home') }}
      />

      <Tab.Screen
        name="Misiones"
        component={MisionesStack}
        options={{ headerShown: false, tabBarIcon: icono('list') }}
      />

      <Tab.Screen
        name="PerfilTab"
        component={DrawerNavigator}
        options={{ headerShown: false, title: 'Perfil', tabBarIcon: icono('person') }}
      />
    </Tab.Navigator>
  );
}