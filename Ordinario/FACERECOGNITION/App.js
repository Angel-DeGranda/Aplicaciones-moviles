import { useRef, useState } from 'react';
import { View, ActivityIndicator, Alert, StyleSheet } from 'react-native';
import { CameraView } from 'expo-camera';
import * as MediaLibrary from 'expo-media-library/legacy';

import useAppPermissions from './hooks/useAppPermissions';
import PermissionGate from './componentes/PermissionGate';
import CameraControls from './componentes/CameraControls';
import PhotoPreview from './componentes/PhotoPreview';

export default function App() {
  const { granted, loading, canAskAgain, request } = useAppPermissions();

  const cameraRef = useRef(null);
  const [facing, setFacing] = useState('back');
  const [photoUri, setPhotoUri] = useState(null);
  const [cameraReady, setCameraReady] = useState(false);
  const [taking, setTaking] = useState(false);
  const [saving, setSaving] = useState(false);

  const handleCapture = async () => {
    if (!cameraRef.current || taking) return;
    try {
      setTaking(true);
      const photo = await cameraRef.current.takePictureAsync();
      setPhotoUri(photo.uri);
    } catch (error) {
      console.log('Error al guardar:', error);
      Alert.alert('Error', `No se pudo guardar la foto.\n\n${error.message}`);
    } finally {
      setTaking(false);
    }
  };

  const handleFlip = () => {
    setFacing((current) => (current === 'back' ? 'front' : 'back'));
  };

  const backToCamera = () => {
    setPhotoUri(null);
    setCameraReady(false); // la cámara se vuelve a montar y avisará cuando esté lista
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      await MediaLibrary.saveToLibraryAsync(photoUri);
      Alert.alert('Listo', 'La foto se guardó en tu galería.');
      backToCamera();
    } catch (error) {
      Alert.alert('Error', 'No se pudo guardar la foto.');
    } finally {
      setSaving(false);
    }
  };

  // 1. Consultando permisos
  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  // 2. Sin permisos
  if (!granted) {
    return <PermissionGate canAskAgain={canAskAgain} onRequest={request} />;
  }

  // 3. Foto tomada: previsualización
  if (photoUri) {
    return (
      <PhotoPreview
        uri={photoUri}
        onSave={handleSave}
        onDiscard={backToCamera}
        saving={saving}
      />
    );
  }

  // 4. Cámara
  return (
    <View style={styles.container}>
      <CameraView
        ref={cameraRef}
        style={StyleSheet.absoluteFill}
        facing={facing}
        onCameraReady={() => setCameraReady(true)}
      />
      <CameraControls
        onCapture={handleCapture}
        onFlip={handleFlip}
        disabled={!cameraReady || taking}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000',
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});