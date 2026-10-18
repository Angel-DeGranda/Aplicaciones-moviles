import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { View, Text, ActivityIndicator, Alert, Pressable, StyleSheet, useWindowDimensions, } from 'react-native';
import { Camera, useCameraDevice, usePhotoOutput } from 'react-native-vision-camera';
import { initTensor, useFaceScannerOutput } from 'vision-camera-face-detection';
import * as MediaLibrary from 'expo-media-library/legacy';

import useAppPermissions from './hooks/useAppPermissions';
import PermissionGate from './componentes/PermissionGate';
import CameraControls from './componentes/CameraControls';
import PhotoPreview from './componentes/PhotoPreview';
import FaceOverlay from './componentes/FaceOverlay';
import RegistroRostro from './componentes/RegistroRostro';
import { UMBRAL, aNumeros, promediar, identificar, cargarRostros, guardarRostros, } from './utils/reconocimiento';

export default function App() {
  const { granted, loading, canAskAgain, request } = useAppPermissions();
  const { width, height } = useWindowDimensions();

  const [facing, setFacing] = useState('front');
  const [photoUri, setPhotoUri] = useState(null);
  const [taking, setTaking] = useState(false);
  const [saving, setSaving] = useState(false);

  const [rostro, setRostro] = useState(null);
  const [numRostros, setNumRostros] = useState(0);
  const [registrados, setRegistrados] = useState([]);
  const [registrando, setRegistrando] = useState(false);

  // Refs: el detector llama al callback muchas veces por segundo,
  // así evitamos leer estado viejo y re-renderizar de más.
  const registradosRef = useRef([]);
  const historial = useRef([]); // últimos embeddings del rostro actual
  const pendiente = useRef(null); // embedding listo para registrar
  const ultimoUpdate = useRef(0);
  const hayRostro = useRef(false);

  const device = useCameraDevice(facing);
  const photoOutput = usePhotoOutput();

  // Cargar rostros guardados
  useEffect(() => {
    cargarRostros().then((lista) => {
      registradosRef.current = lista;
      setRegistrados(lista);
    });
  }, []);

  // Iniciar el modelo de embeddings cuando ya hay permisos
  useEffect(() => {
    if (!granted) return;
    const resultado = initTensor('facenet_512');
    console.log('initTensor:', resultado);
  }, [granted]);

  const onFaceScanned = useCallback((faces) => {
    // Sin rostros
    if (!faces || faces.length === 0) {
      historial.current = [];
      if (hayRostro.current) {
        hayRostro.current = false;
        setRostro(null);
        setNumRostros(0);
      }
      return;
    }

    hayRostro.current = true;

    // Tomamos el rostro más grande (el más cercano a la cámara)
    const principal = faces.reduce((a, b) =>
      b.bounds.width * b.bounds.height > a.bounds.width * a.bounds.height ? b : a
    );

    let nombre = null;
    let distancia = null;

    if (principal.data && principal.data.length > 0) {
      const embedding = aNumeros(principal.data);
      historial.current.push(embedding);
      if (historial.current.length > 5) historial.current.shift();

      const resultado = identificar(embedding, registradosRef.current);
      nombre = resultado.nombre;
      distancia = resultado.distancia;
    }

    // Limitar actualizaciones de pantalla (~10 por segundo)
    const ahora = Date.now();
    if (ahora - ultimoUpdate.current < 100) return;
    ultimoUpdate.current = ahora;

    setNumRostros(faces.length);
    setRostro({
      bounds: principal.bounds,
      nombre,
      distancia,
      hayRegistrados: registradosRef.current.length > 0,
    });
  }, []);

  const opciones = useMemo(
    () => ({
      performanceMode: 'fast',
      runClassifications: true,
      runContours: false,
      runLandmarks: false,
      windowWidth: width,
      windowHeight: height,
    }),
    [width, height]
  );

  const faceOutput = useFaceScannerOutput({
    ...opciones,
    autoMode: true,
    cameraFacing: facing,
    onFaceScanned,
    onError: (error) => console.error('Error de detección:', error),
  });

  // ---------- Fotos ----------
  const handleCapture = async () => {
    if (taking) return;
    try {
      setTaking(true);
      const foto = await photoOutput.capturePhotoToFile({ flashMode: 'off' }, {});
      const uri = foto.filePath.startsWith('file://')
        ? foto.filePath
        : `file://${foto.filePath}`;
      setPhotoUri(uri);
    } catch (error) {
      console.log('Error al tomar foto:', error);
      Alert.alert('Error', 'No se pudo tomar la foto.');
    } finally {
      setTaking(false);
    }
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      await MediaLibrary.saveToLibraryAsync(photoUri);
      Alert.alert('Listo', 'La foto se guardó en tu galería.');
      setPhotoUri(null);
    } catch (error) {
      console.log('Error al guardar:', error);
      Alert.alert('Error', 'No se pudo guardar la foto.');
    } finally {
      setSaving(false);
    }
  };

  const handleFlip = () => {
    setFacing((actual) => (actual === 'front' ? 'back' : 'front'));
  };

  // ---------- Registro de rostros ----------
  const abrirRegistro = () => {
    if (historial.current.length === 0) {
      Alert.alert('Sin rostro', 'Ponte frente a la cámara hasta que se detecte tu rostro.');
      return;
    }
    // Congelamos el embedding en este momento (antes de abrir el teclado)
    pendiente.current = promediar(historial.current);
    setRegistrando(true);
  };

  const guardarRostro = async (nombre) => {
    setRegistrando(false);
    if (!pendiente.current) return;

    const lista = [
      ...registradosRef.current.filter(
        (p) => p.nombre.toLowerCase() !== nombre.toLowerCase()
      ),
      { nombre, embedding: pendiente.current },
    ];
    pendiente.current = null;
    registradosRef.current = lista;
    setRegistrados(lista);
    await guardarRostros(lista);
    Alert.alert('Listo', `${nombre} fue registrado.`);
  };

  const borrarRegistros = () => {
    Alert.alert('Borrar registros', '¿Eliminar todos los rostros registrados?', [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Borrar',
        style: 'destructive',
        onPress: async () => {
          registradosRef.current = [];
          setRegistrados([]);
          await guardarRostros([]);
        },
      },
    ]);
  };

  // ---------- Pantallas ----------
  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  if (!granted) {
    return <PermissionGate canAskAgain={canAskAgain} onRequest={request} />;
  }

  if (photoUri) {
    return (
      <PhotoPreview
        uri={photoUri}
        onSave={handleSave}
        onDiscard={() => setPhotoUri(null)}
        saving={saving}
      />
    );
  }

  if (!device) {
    return (
      <View style={styles.center}>
        <Text>No se encontró una cámara en este dispositivo.</Text>
      </View>
    );
  }

  let estado = 'Sin rostro';
  if (rostro) {
    if (rostro.nombre) estado = `Reconocido: ${rostro.nombre}`;
    else if (rostro.hayRegistrados) estado = 'Rostro desconocido';
    else estado = 'Rostro detectado';
  }

  return (
    <View style={styles.container}>
      <Camera
        style={StyleSheet.absoluteFill}
        device={device}
        isActive
        orientationSource="device"
        outputs={[faceOutput, photoOutput]}
        onError={(error) => console.error('Error de cámara:', error)}
      />

      <FaceOverlay rostro={rostro} />

      <View style={styles.panel}>
        <Text style={styles.estado}>{estado}</Text>
        <Text style={styles.detalle}>
          Rostros: {numRostros} | Registrados: {registrados.length}
          {rostro?.distancia != null
            ? ` | Distancia: ${rostro.distancia.toFixed(2)} (umbral ${UMBRAL})`
            : ''}
        </Text>
        <View style={styles.fila}>
          <Pressable style={styles.boton} onPress={abrirRegistro}>
            <Text style={styles.textoBoton}>Registrar rostro</Text>
          </Pressable>
          <Pressable
            style={[styles.boton, styles.botonSecundario]}
            onPress={borrarRegistros}
          >
            <Text style={styles.textoBoton}>Borrar registros</Text>
          </Pressable>
        </View>
      </View>

      <CameraControls onCapture={handleCapture} onFlip={handleFlip} disabled={taking} />

      <RegistroRostro
        visible={registrando}
        onGuardar={guardarRostro}
        onCancelar={() => {
          pendiente.current = null;
          setRegistrando(false);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#000' },
  center: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  panel: {
    position: 'absolute',
    top: 50,
    left: 12,
    right: 12,
    backgroundColor: 'rgba(0,0,0,0.55)',
    borderRadius: 10,
    padding: 10,
  },
  estado: { color: '#fff', fontSize: 18, fontWeight: 'bold' },
  detalle: { color: '#ddd', fontSize: 12, marginTop: 2, marginBottom: 8 },
  fila: { flexDirection: 'row', gap: 8 },
  boton: {
    backgroundColor: '#2e7df6',
    paddingVertical: 8,
    paddingHorizontal: 14,
    borderRadius: 8,
  },
  botonSecundario: { backgroundColor: '#c0392b' },
  textoBoton: { color: '#fff', fontWeight: '600', fontSize: 13 },
});