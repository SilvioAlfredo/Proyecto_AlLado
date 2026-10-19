import React from 'react';
import { Text } from 'react-native';
import { Screen, Brand, Title } from '../../../src/components/ui';
import { NotificacionesList } from '../../../src/components/notificaciones';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

// Espejo de app/trabajador/notificaciones (web).
export default function TrabajadorNotificaciones() {
  const { userId } = useAuth();
  if (!userId) {
    return (
      <Screen>
        <Brand />
        <Text style={{ color: colors.muted }}>Cargando notificaciones...</Text>
      </Screen>
    );
  }
  return (
    <Screen>
      <Brand />
      <Title eyebrow="Avisos" title="Notificaciones" />
      <NotificacionesList usuarioId={userId} />
    </Screen>
  );
}
