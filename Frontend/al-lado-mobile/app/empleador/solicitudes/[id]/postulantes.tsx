import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, StyleSheet, TouchableOpacity } from 'react-native';
import { router, useLocalSearchParams, useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppButton, Badge, Empty } from '../../../../src/components/ui';
import { postulacionService } from '../../../../src/api/services';
import { param } from '../../../../src/api/client';
import type { Postulacion } from '../../../../src/types';
import { colors } from '../../../../src/theme';

export default function Postulantes() {
  const { id } = useLocalSearchParams();
  const [items, setItems] = useState<Postulacion[]>([]);
  const [f, setF] = useState('Todos');
  const load = async () => {
    const all = await postulacionService.listar().catch(() => []);
    setItems((Array.isArray(all) ? all : []).filter((p) => String(p.oportunidadLaboral?.id) === param(id)));
  };
  useFocusEffect(useCallback(() => { load(); }, [id]));
  const list = f === 'Todos' ? items : items.filter((p) => p.estado === f);
  const act = async (fn: (id: number | string) => Promise<unknown>, pid: number | string) => { await fn(pid); load(); };

  return (
    <Screen>
      <Brand />
      <Title title={`Postulantes · ${items.length}`} />
      <View style={styles.chips}>
        {['Todos', 'PENDIENTE', 'SELECCIONADA', 'RECHAZADA', 'COMPLETADA'].map((x) => (
          <TouchableOpacity key={x} onPress={() => setF(x)} style={[styles.chip, f === x && styles.a]}>
            <Text style={f === x ? styles.ta : styles.t2}>{x}</Text>
          </TouchableOpacity>
        ))}
      </View>
      <FlatList data={list} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Sin propuestas en este estado." />}
        renderItem={({ item }) => (
          <AppCard>
            <Badge active={item.estado === 'SELECCIONADA'}>{item.estado}</Badge>
            <Text style={styles.t}>{item.titulo}</Text>
            <Text style={styles.b} numberOfLines={2}>{item.contenido}</Text>
            <Text style={styles.m}>Pretensión: {item.remuneracionPretendida ?? '—'}</Text>
            <View style={styles.row}>
              <View style={{ flex: 1 }}><AppButton title="Seleccionar" onPress={() => act(postulacionService.seleccionar, item.id)} /></View>
              <View style={{ flex: 1 }}><AppButton title="Rechazar" variant="outline" onPress={() => act(postulacionService.rechazar, item.id)} /></View>
            </View>
            <AppButton title="Marcar completado" variant="outline" onPress={() => act(postulacionService.completar, item.id)} />
          </AppCard>
        )} />
    </Screen>
  );
}
const styles = StyleSheet.create({
  chips: { flexDirection: 'row', flexWrap: 'wrap', gap: 6, marginBottom: 10 },
  chip: { borderWidth: 1, borderColor: colors.border, borderRadius: 999, paddingHorizontal: 8, paddingVertical: 4 },
  a: { backgroundColor: colors.yellow }, t2: { color: colors.muted, fontSize: 10 }, ta: { color: '#000', fontSize: 10, fontWeight: '700' },
  t: { color: '#fff', fontWeight: '700', marginTop: 6 }, b: { color: colors.muted }, m: { color: colors.yellow, fontSize: 12, marginTop: 4 },
  row: { flexDirection: 'row', gap: 8 },
});
