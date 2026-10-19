import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppCard, Empty } from '../../src/components/ui';
import { trabajadorService, empleadorService } from '../../src/api/services';
import type { Trabajador, Empleador } from '../../src/types';
import { colors } from '../../src/theme';

export default function Personas() {
  const [tab, setTab] = useState('Trabajadores');
  const [items, setItems] = useState<Trabajador[] | Empleador[]>([]);
  const [q, setQ] = useState('');
  const load = async () => {
    const fn = tab === 'Trabajadores' ? trabajadorService.listar : empleadorService.listar;
    const d = await fn().catch(() => []);
    setItems(Array.isArray(d) ? d : []);
  };
  useFocusEffect(useCallback(() => { load(); }, [tab]));
  const list = items.filter((u) => `${u.nombre} ${u.email}`.toLowerCase().includes(q.toLowerCase()));
  return (
    <Screen>
      <Brand admin />
      <Title eyebrow="Admin" title="Personas" />
      <View style={styles.tabs}>
        {['Trabajadores', 'Empleadores'].map((t) => (
          <TouchableOpacity key={t} onPress={() => setTab(t)} style={[styles.chip, tab === t && styles.a]}>
            <Text style={tab === t ? styles.ta : styles.t2}>{t}</Text>
          </TouchableOpacity>
        ))}
      </View>
      <AppInput placeholder="Buscar nombre/email" value={q} onChangeText={setQ} />
      <FlatList data={list} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Sin resultados." />}
        renderItem={({ item }) => (
          <AppCard><Text style={styles.t}>{item.nombre}</Text><Text style={styles.m}>{item.email} · {item.telefono ?? ''}</Text></AppCard>
        )} />
    </Screen>
  );
}
const styles = StyleSheet.create({
  tabs: { flexDirection: 'row', gap: 6, marginBottom: 10 },
  chip: { borderWidth: 1, borderColor: colors.border, borderRadius: 999, paddingHorizontal: 12, paddingVertical: 6 },
  a: { backgroundColor: colors.yellow }, t2: { color: colors.muted }, ta: { color: '#000', fontWeight: '700' },
  t: { color: '#fff', fontWeight: '700' }, m: { color: colors.muted, fontSize: 12 },
});
