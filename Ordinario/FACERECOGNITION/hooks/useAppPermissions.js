import { useState } from 'react';
import { useCameraPermission } from 'react-native-vision-camera';
import * as MediaLibrary from 'expo-media-library/legacy';

export default function useAppPermissions() {
  const { hasPermission, requestPermission } = useCameraPermission();
  const [mediaPermission, requestMedia] = MediaLibrary.usePermissions({
    writeOnly: true,
  });
  const [denied, setDenied] = useState(false);

  const loading = mediaPermission === null;
  const granted = !!hasPermission && !!mediaPermission?.granted;

  const request = async () => {
    let camera = !!hasPermission;
    let media = !!mediaPermission?.granted;

    if (!camera) camera = !!(await requestPermission());
    if (!media) {
      const result = await requestMedia();
      media = !!result?.granted;
    }
    // Si algo quedó negado, PermissionGate mostrará "Abrir ajustes"
    setDenied(!(camera && media));
  };

  return { granted, loading, canAskAgain: !denied, request };
}