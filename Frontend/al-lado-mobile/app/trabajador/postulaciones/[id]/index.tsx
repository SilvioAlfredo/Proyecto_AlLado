import React, { useCallback, useState } from 'react';
import { Text, ScrollView, StyleSheet, Alert } from 'react-native';
import { useLocalSearchParams, useFocusEffect } from 'expo-router';
import { Screen, Brand, AppCard, AppButton, Badge, Note } from '../../../../src/components/ui';
import { postulacionService } from '../../../../src/api/services';
import { errMsg, param } from '../../../../src/api/client';
import type { Postulacion } from '../../../../src/types';
import { colors } from '../../../../src/theme';

// Endpoints: GET /postulaciones/{id} · PATCH /postulaciones/{id}/retirar
// Espejo de app/trabajador/postulaciones/[id] (web).
export default function DetallePostulacion() {
  const { id } = useLocalSearchParams();
  const [p, setP] = useState<Postulacion | null>(null);
  const [error, setError] = useState('');
  const [retirando, setRetirando] = useState(false);
  useFocusEffect(useCallback(() => {
    postulacionService.detalle(Number(param(id))).then(setP).catch((e) => setError(errMsg(e)));
  }, [id]));

  const retirar = () => {
    Alert.alert(
      'Retirar postulación',
      '¿Retirar tu postulación? Ya no podrás participar de esta oportunidad.',
      [
        { text: 'Cancelar', style: 'cancel' },
        {
          text: 'Retirar', style: 'destructive', onPress: async () => {
            setRetirando(true);
            setError('');
            try {
              const actualizada = await postulacionService.retirar(Number(param(id)));
              setP(actualizada);
            } catch (e) {
              setError(errMsg(e));
            } finally {
              setRetirando(false);
            }
          },
        },
      ],
    );
  };

  if (error) return <Screen><Brand /><Text style={styles.e}>{error}</Text></Screen>;
  if (!p) return <Screen><Brand /><Text style={styles.m}>Cargando...</Text></Screen>;
  const puedeRetirar = p.estado === 'PENDIENTE' || p.estado === 'SELECCIONADA';
  return (
    <Screen><ScrollView>
      <Brand />
      <Badge active={p.estado === 'SELECCIONADA'}>{p.estado}</Badge>
      <Text style={styles.t}>{p.titulo}</Text>
      <Text style={styles.b}>{p.contenido}</Text>
      <AppCard cream><Text style={styles.c}>Pretensión: {p.remuneracionPretendida ?? '—'}</Text></AppCard>
      {puedeRetirar ? (
        <AppButton title={retirando ? 'Retirando...' : 'Retirar postulación'} variant="outline" onPress={retirar} loading={retirando} />
      ) : (
        <Note>Solo lectura: esta postulación ya está cerrada ({p.estado}). Solo las pendientes o seleccionadas pueden retirarse.</Note>
      )}
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  t: { color: '#fff', fontSize: 20, fontWeight: '800', marginTop: 8 },
  b: { color: colors.muted, marginTop: 8 },
  c: { color: '#000', fontWeight: '700' },
  m: { color: colors.muted }, e: { color: colors.danger },
});
