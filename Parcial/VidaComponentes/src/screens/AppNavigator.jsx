import { createDrawerNavigator } from '@react-navigation/drawer';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { HomeScreen } from './HomeScreen.jsx';
import { Dice } from '../../componentes/Dice.jsx';
import { Imc } from '../../componentes/IMC.jsx';
import { TicTacToe } from '../../componentes/TicTacToe.jsx';
import { Memorama } from '../../componentes/Memorama.jsx';

const Tab = createBottomTabNavigator();
const Drawer = createDrawerNavigator();

function Tabs() {
  return (
    <Tab.Navigator screenOptions={{ headerShown: false }}>
      <Tab.Screen name="Dados" component={Dice} />
      <Tab.Screen name="IMC" component={Imc} />
      <Tab.Screen name="TicTacToe" component={TicTacToe} />
      <Tab.Screen name="Memorama" component={Memorama} />
    </Tab.Navigator>
  );
}

export function AppNavigator() {
  return (
    <Drawer.Navigator>
      <Drawer.Screen name="Inicio" component={HomeScreen} />
      <Drawer.Screen name="Componentes" component={Tabs} options={{ title: 'Componentes' }} />
    </Drawer.Navigator>
  );
}