import React, { useCallback, useState } from 'react';
import { Text, FlatList, StyleSheet } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, Empty, Note } from '../../../src/components/ui';
import { comunidadService } from '../../../src/api/services';
import type { Valoracion } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Valoraciones() {
  const { userId } = useAuth();
  const [items, setItems] = useState<Valoracion[]>([]);
  const [prom, setProm] = useState<number | null>(null);
  useFocusEffect(useCallback(() => {
    (async () => {
      const id = userId;
      if (!id) return;
      const [l, a] = await Promise.all([
        comunidadService.valoracionesDe(id).catch(() => []),
        comunidadService.promedio(id).catch(() => null),
      ]);
      setItems(Array.isArray(l) ? l : []);
      setProm(a);
    })();
  }, [userId]));
  return (
    <Screen>
      <Brand />
      <Title title="Valoraciones recibidas" subtitle={typeof prom === 'number' ? `${prom.toFixed(1)} sobre 5` : 'Lo que dicen los vecinos'} />
      <FlatList data={items} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Todavía no tenés valoraciones." />}
        renderItem={({ item }) => (
          <AppCard><Text style={styles.t}>★ {item.puntuacion}/5 — {item.autor?.nombre ?? ''}</Text>
          <Text style={styles.b}>{item.comentario ?? ''}</Text></AppCard>
        )} />
      <Note>Las dejan los empleadores. Una sola por profesional.</Note>
    </Screen>
  );
}
const styles = StyleSheet.create({ t: { color: '#fff', fontWeight: '700' }, b: { color: colors.muted, marginTop: 4 } });
