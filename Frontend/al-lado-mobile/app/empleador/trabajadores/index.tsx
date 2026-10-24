import React, { useCallback, useState } from 'react';
import { Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppCard, AppButton, Empty } from '../../../src/components/ui';
import { trabajadorService, comunidadService, suscripcionService } from '../../../src/api/services';
import type { Trabajador } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function BuscarTrabajadores() {
  const { userId } = useAuth();
  const [items, setItems] = useState<Trabajador[]>([]);
  const [q, setQ] = useState('');
  const [favs, setFavs] = useState<Set<number>>(new Set<number>());
  const [empId, setEmpId] = useState<number | null>(null);

  const load = async () => {
    const list = await trabajadorService.listar().catch(() => []);
    const eid = userId;
    setItems(Array.isArray(list) ? list : []);
    setEmpId(eid);
    if (eid) {
      const f = await comunidadService.favoritosDe(eid).catch(() => []);
      setFavs(new Set<number>((Array.isArray(f) ? f : []).map((x: Record<string, unknown>) => {
        const t = x.trabajador as { id?: number } | undefined;
        const fb = typeof x.id === 'number' ? (x.id as number) : 0;
        return t?.id ?? fb;
      })));
    }
  };
  useFocusEffect(useCallback(() => { load(); }, []));

  const toggleFav = async (tid: number) => {
    if (empId == null) return;
    if (favs.has(tid)) {
      await comunidadService.quitarFavorito(empId, tid);
      setFavs((s) => { const n = new Set(s); n.delete(tid); return n; });
    } else {
      await comunidadService.guardarFavorito(empId, tid);
      setFavs((s) => new Set(s).add(tid));
    }
  };

  const list = items.filter((t) => `${t.nombre} ${(t.especialidades ?? []).join(' ')}`.toLowerCase().includes(q.toLowerCase()));

  return (
    <Screen>
      <Brand />
      <Title title="Encontrá profesionales" />
      <AppInput placeholder="Nombre o especialidad" value={q} onChangeText={setQ} />
      <FlatList data={list} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Sin perfiles." />}
        renderItem={({ item }) => (
          <AppCard>
            <Text style={styles.t}>{item.nombre}</Text>
            <Text style={styles.b}>{(item.especialidades ?? []).join(' · ')}</Text>
            <Text style={styles.m}>{item.email}</Text>
            <TouchableOpacity onPress={() => router.push(`/empleador/trabajadores/${item.id}`)}>
              <Text style={styles.link}>Ver perfil →</Text>
            </TouchableOpacity>
            <AppButton title={favs.has(item.id) ? 'Guardado ✓' : 'Guardar'} variant="outline" onPress={() => toggleFav(item.id)} />
          </AppCard>
        )} />
    </Screen>
  );
}
const styles = StyleSheet.create({
  t: { color: '#fff', fontWeight: '700' }, b: { color: colors.muted },
  m: { color: colors.muted, fontSize: 12 }, link: { color: colors.orange, marginVertical: 6, fontWeight: '600' },
});
