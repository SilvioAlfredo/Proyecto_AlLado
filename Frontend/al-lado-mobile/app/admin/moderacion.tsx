import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppButton, Empty } from '../../src/components/ui';
import { adminService } from '../../src/api/services';
import type { Reporte } from '../../src/types';
import { useAuth } from '../../src/auth/AuthContext';
import { colors } from '../../src/theme';

const ESTADOS = ['PENDIENTE', 'EN_REVISION', 'RESUELTO', 'DESCARTADO'];

export default function Moderacion() {
  const { userId } = useAuth();
  const [items, setItems] = useState<Reporte[]>([]);
  const [f, setF] = useState('PENDIENTE');
  const load = async () => {
    const d = await adminService.reportes().catch(() => [] as Reporte[]);
    setItems(Array.isArray(d) ? d : (d as unknown as { content?: Reporte[] })?.content ?? (d as Reporte[]));
  };
  useFocusEffect(useCallback(() => { load(); }, []));
  const list = items.filter((r) => !r.estado || r.estado === f);

  const setEstado = async (id: number, estado: string) => {
    await adminService.resolverReporte(id, userId, estado);
    load();
  };

  return (
    <Screen>
      <Brand admin />
      <Title eyebrow="Admin" title="Reportes" subtitle="Resolver/descartar cierra definitivo." />
      <View style={styles.chips}>
        {ESTADOS.map((e) => (
          <TouchableOpacity key={e} onPress={() => setF(e)} style={[styles.chip, f === e && styles.a]}>
            <Text style={f === e ? styles.ta : styles.t2}>{e}</Text>
          </TouchableOpacity>
        ))}
      </View>
      <FlatList data={list} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Sin reportes en este estado." />}
        renderItem={({ item }) => (
          <AppCard>
            <Text style={styles.t}>{item.motivo ?? item.titulo ?? `#${item.id}`}</Text>
            <Text style={styles.b} numberOfLines={3}>{item.descripcion ?? item.contenido ?? ''}</Text>
            <View style={styles.row}>
              <View style={{ flex: 1 }}><AppButton title="En revisión" variant="outline" onPress={() => setEstado(item.id, 'EN_REVISION')} /></View>
              <View style={{ flex: 1 }}><AppButton title="Resolver" onPress={() => setEstado(item.id, 'RESUELTO')} /></View>
            </View>
            <AppButton title="Descartar" variant="danger" onPress={() => setEstado(item.id, 'DESCARTADO')} />
          </AppCard>
        )} />
    </Screen>
  );
}
const styles = StyleSheet.create({
  chips: { flexDirection: 'row', flexWrap: 'wrap', gap: 6, marginBottom: 10 },
  chip: { borderWidth: 1, borderColor: colors.border, borderRadius: 999, paddingHorizontal: 8, paddingVertical: 4 },
  a: { backgroundColor: colors.yellow }, t2: { color: colors.muted, fontSize: 10 }, ta: { color: '#000', fontSize: 10, fontWeight: '700' },
  t: { color: '#fff', fontWeight: '700' }, b: { color: colors.muted, marginTop: 4 },
  row: { flexDirection: 'row', gap: 8 },
});
