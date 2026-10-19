import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppButton, Badge, Empty } from '../../src/components/ui';
import { postulacionService } from '../../src/api/services';
import type { Postulacion } from '../../src/types';
import { colors } from '../../src/theme';

export default function Contratos() {
  const [items, setItems] = useState<Postulacion[]>([]);
  const [f, setF] = useState('Todos');
  const load = async () => {
    const all = await postulacionService.listar().catch(() => []);
    setItems((Array.isArray(all) ? all : []).filter((p) => ['SELECCIONADA', 'COMPLETADA'].includes(p.estado)));
  };
  useFocusEffect(useCallback(() => { load(); }, []));
  const list = f === 'Todos' ? items : items.filter((p) => p.estado === (f === 'En curso' ? 'SELECCIONADA' : 'COMPLETADA'));
  return (
    <Screen>
      <Brand />
      <Title title="Tus trabajos" subtitle="No se generan contratos ni pagos en app." />
      <View style={styles.chips}>
        {['Todos', 'En curso', 'Completados'].map((x) => (
          <TouchableOpacity key={x} onPress={() => setF(x)} style={[styles.chip, f === x && styles.a]}>
            <Text style={f === x ? styles.ta : styles.t2}>{x}</Text>
          </TouchableOpacity>
        ))}
      </View>
      <FlatList data={list} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Sin trabajos." />}
        renderItem={({ item }) => (
          <AppCard>
            <Badge active={item.estado === 'SELECCIONADA'}>{item.estado}</Badge>
            <Text style={styles.t}>{item.titulo}</Text>
            {item.estado === 'SELECCIONADA' ? (
              <>
                <AppButton title="Marcar como completado" onPress={async () => { await postulacionService.completar(item.id); load(); }} />
                <TouchableOpacity onPress={() => router.push(`/empleador/contratos-valorar?postulacion=${item.id}`)}>
                  <Text style={styles.link}>Valorar →</Text>
                </TouchableOpacity>
              </>
            ) : (
              <TouchableOpacity onPress={() => router.push(`/empleador/contratos-valorar?postulacion=${item.id}`)}>
                <Text style={styles.link}>Valorar →</Text>
              </TouchableOpacity>
            )}
          </AppCard>
        )} />
    </Screen>
  );
}
const styles = StyleSheet.create({
  chips: { flexDirection: 'row', gap: 6, marginBottom: 10 },
  chip: { borderWidth: 1, borderColor: colors.border, borderRadius: 999, paddingHorizontal: 12, paddingVertical: 6 },
  a: { backgroundColor: colors.yellow }, t2: { color: colors.muted }, ta: { color: '#000', fontWeight: '700' },
  t: { color: '#fff', fontWeight: '700', marginTop: 6 }, link: { color: colors.orange, marginTop: 8, fontWeight: '600' },
});
