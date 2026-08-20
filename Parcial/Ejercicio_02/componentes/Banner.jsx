import { StyleSheet, View, Text } from "react-native";

export default function Banner({children, titulo}) {
    return(
        <View style={style.texto}>
            <Text>{titulo}</Text>
            {children}
        </View>
    )
}

const style=StyleSheet.create({
    texto: {
        color: "red"
    }
})