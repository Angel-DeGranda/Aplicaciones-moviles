import { StatusBar } from 'expo-status-bar';
import React, { useEffect, useRef } from 'react';
import { Animated, Image, StyleSheet, Text, View } from 'react-native';

export default function App2(){

    const scale = useRef(
        new Animated.Value(0)
    ).current;

    const opacity = useRef(
        new Animated.Value(0)
    ).current;

    const position = useRef(
        new Animated.Value(200)
    ).current;

    useEffect(() => {

        Animated.parallel([
            Animated.timing(
                scale,
                {
                    toValue: 1,
                    duration: 5000,
                    useNativeDriver: true
                }
            ),


            Animated.timing(
                opacity,
                {
                    toValue: 1,
                    duration: 5000,
                    useNativeDriver: true
                }
            ),
            Animated.timing(
                position,
                {
                    toValue: 0,
                    duration: 5000,
                    useNativeDriver: true,
                }
            )
        ]).start();
        
    }, []);
    return(
        <View style={{flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#fff',}}>
            <Animated.Text style={{fontSize: 80, transform: [{translateX: position}, {scale: scale}], opacity: opacity,}}>
                🐜
                <Image
                    source={require('./assets/favicon.png')}
                />
                🐜
            </Animated.Text>
        </View>
    );
}