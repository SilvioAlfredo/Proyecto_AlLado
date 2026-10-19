import React, { useCallback, useState } from 'react';
import { View, Text, ScrollView, StyleSheet } from 'react-native';
import { router, useLocalSearchParams, useFocusEffect } from 'expo-router';
import { Screen, Brand, AppCard, Badge, AppButton, Note } from '../../../../src/components/ui';
import { oportunidadService } from '../../../../src/api/services';
import { errMsg, param } from '../../../../src/api/client';
import type { Oportunidad } from '../../../../src/types';
import { colors } from '../../../../src/theme';

export default function DetalleOportunidad() {
  const { id } = useLocalSearchParams();
  const [o, setO] = useState<Oportunidad | null>(null);
  const [error, setError] = useState('');

  useFocusEffect(useCallback(() => {
    oportunidadService.detalle(Number(param(id))).then(setO).catch((e) => setError(errMsg(e)));
  }, [id]));

  if (error) return <Screen><Brand /><Text style={styles.error}>{error}</Text></Screen>;
  if (!o) return <Screen><Brand /><Text style={styles.muted}>Cargando...</Text></Screen>;

  return (
    <Screen>
      <ScrollView>
        <Brand />
        <View style={styles.row}>
          <Badge active>{o.activa ? 'Activa' : 'Cerrada'}</Badge>
          {o.modalidad ? <Badge>{o.modalidad}</Badge> : null}
        </View>
        <Text style={styles.title}>{o.titulo}</Text>
        {(o.etiquetas ?? []).map((t) => (
          <Text key={typeof t === 'string' ? t : String(t.id ?? t.nombre)} style={styles.tag}>· {typeof t === 'string' ? t : t.nombre}</Text>
        ))}
        <AppCard cream>
          <Text style={styles.cream}>{o.rangoSalarial ?? 'A convenir'}</Text>
          <Text style={styles.cream}>{o.ubicacion ?? ''}</Text>
        </AppCard>
        <Text style={styles.section}>Sobre el trabajo</Text>
        <Text style={styles.body}>{o.contenido}</Text>
        <AppCard>
          <Text style={styles.muted}>Publicado por {o.autor?.nombre ?? o.autor?.email ?? '—'}</Text>
        </AppCard>
        <AppButton title="Postularme" onPress={() => router.push(`/trabajador/oportunidades/${id}/postular`)} />
        <AppButton title="Volver" variant="outline" onPress={() => router.back()} />
        <Note>1 postulación por oferta activa. La postulación nace Pendiente, no editable ni retirable.</Note>
      </ScrollView>
    </Screen>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', gap: 6, marginBottom: 8 },
  title: { color: '#fff', fontSize: 22, fontWeight: '800' },
  tag: { color: colors.yellow, marginTop: 2 },
  cream: { color: '#000', fontWeight: '700' },
  section: { color: '#fff', fontWeight: '700', marginTop: 12 },
  body: { color: colors.muted, marginTop: 6 },
  muted: { color: colors.muted },
  error: { color: colors.danger },
});
