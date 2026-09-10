import { useEffect, useRef } from 'react';
import { Animated, Easing, StyleSheet, Text, View } from 'react-native';

export function SplashScreen(){

    const rotate = useRef(
        new Animated.Value(0)
    ).current;

    useEffect(() => {
        Animated.loop(
            Animated.timing(
                rotate,
                {
                    toValue: 1,
                    duration: 100,
                    easing: Easing.linear,
                    useNativeDriver: true,
                }
            )
        ).start();
    }, [rotate]);

    const rot = rotate.interpolate({
        inputRange: [0, 1],
        outputRange: ['0deg', '360deg'],
    });

    return(
        <View style={styles.contenedor}>
            <Animated.View style={{ transform: [{rotate: rot}] }}>
                <Text>
                ➡️
                </Text>
            </Animated.View>
            
            <Text>
                cargando...
            </Text>
        </View>
    );
}

const styles = StyleSheet.create({
    contenedor: {
        flex: 1,
        backgroundColor: '#fff',
        justifyContent: 'center',
        alignItems: 'center',
    }
});