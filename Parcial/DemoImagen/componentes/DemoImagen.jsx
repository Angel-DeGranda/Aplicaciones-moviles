import { StyleSheet, Text, View, ImageBackground, Dimensions, Image } from "react-native";

export function DemoImagen() {
    return(
        <View>
            <ImageBackground
                style={styles.fondo}
                source={require('../assets/monsta-x-neon-28mejt18zkmeo7ru.jpg')}
            >
                <View style={styles.container}>
                    <Text style={styles.titulo}>The Best Group</Text>
                    <Image 
                        style={styles.foto}
                        source={{uri:'https://imgs.search.brave.com/EcH_6qrKBcnkm0FrC7dMe8nSHZ-YnGvyiPcZh_GUNIY/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly93d3cu/cG5na2V5LmNvbS9w/bmcvZnVsbC82MTMt/NjEzNjUyMV9idXR0/b24tbW9uc3RhLXgt/bW9uc3RhLXgtbG9n/by1wbmcucG5n'}}
                    />
                </View>
            </ImageBackground>
        </View>
    )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'rgb(0, 0, 0, 0.5)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  fondo: {
    width: Dimensions.get('window').width,
    height: Dimensions.get('window').height,
  },
  titulo:{
    width: Dimensions.get('window').width,
    fontSize: 50,
    textAlign: 'center',
    fontFamily: 'Arial',
    fontWeight: 'bold',
    color: '#fcfafe',
    backgroundColor: 'rgb( 0, 0, 0,0.5)',

  },
  foto: {
    width: 200,
    height: 200,
    borderRadius: '100%',
    borderWidth: 5,
    borderColor: '#aa32da',
    shadowColor: '#fff',
    shadowOffset: { width: 0, height: 5},
  }
});
