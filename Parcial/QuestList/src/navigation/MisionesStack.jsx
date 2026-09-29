import { createNativeStackNavigator } from '@react-navigation/native-stack';
import ListaMisiones from '../screens/ListaMisiones';
import DetalleMision from '../screens/DetalleMision';

const Stack = createNativeStackNavigator();

export default function MisionesStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="ListaMisiones" component={ListaMisiones} />
      <Stack.Screen name="DetalleMision" component={DetalleMision} />
    </Stack.Navigator>
  );
}