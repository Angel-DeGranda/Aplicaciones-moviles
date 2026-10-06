import { useCameraPermissions } from 'expo-camera';
import * as MediaLibrary from 'expo-media-library/legacy';

export default function useAppPermissions() {
  const [cameraPermission, requestCamera] = useCameraPermissions();
  const [mediaPermission, requestMedia] = MediaLibrary.usePermissions({
    writeOnly: true,
  });

  // Mientras los permisos no se han consultado, el valor es null
  const loading = cameraPermission === null || mediaPermission === null;

  const granted =
    !!cameraPermission?.granted && !!mediaPermission?.granted;

  // Si es false, el sistema ya no mostrará el aviso: hay que mandar al usuario a Ajustes
  const canAskAgain =
    (cameraPermission?.canAskAgain ?? true) &&
    (mediaPermission?.canAskAgain ?? true);

  const request = async () => {
    if (!cameraPermission?.granted) await requestCamera();
    if (!mediaPermission?.granted) await requestMedia();
  };

  return { granted, loading, canAskAgain, request };
}