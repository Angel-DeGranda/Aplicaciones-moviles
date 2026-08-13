import { StyleSheet, View, Text } from 'react-native';

export default function Mensaje(props) {
    const variableMensaje = "Mi mensaje.";
    const num = 1000;
    const double = n => n*2;
    return(
        <View style={styles.container}>
            <Text style={styles.texto_color}>{props.msg}</Text>
            <Text style={styles.texto_color}>{props.num}</Text>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        backgroundColor: '#bd8c43',
    },
    texto_color: {
        color: '#b400ea',
    }
});