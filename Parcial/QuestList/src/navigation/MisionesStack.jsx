import { createNativeStackNavigator } from '@react-navigation/native-stack';
import ListaMisiones from '../screens/ListaMisiones';
import DetalleMision from '../screens/DetalleMision';

const Stack = createNativeStackNavigator();

export default function MisionesStack() {
  return (
    <Stack.Navigator
      screenOptions={{
        headerStyle: { backgroundColor: '#1E1B4B' },
        headerTintColor: '#fff',
      }}
    >
      <Stack.Screen
        name="ListaMisiones"
        component={ListaMisiones}
        options={{ title: 'Mis Misiones' }}
      />
      <Stack.Screen
        name="DetalleMision"
        component={DetalleMision}
        options={{ title: 'Detalle' }}
      />
    </Stack.Navigator>
  );
}