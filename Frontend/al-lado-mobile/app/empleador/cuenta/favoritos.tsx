import React, { useCallback, useState } from 'react';
import { Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppButton, Empty } from '../../../src/components/ui';
import { comunidadService, trabajadorService } from '../../../src/api/services';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Favoritos() {
  const { userId } = useAuth();
  const [items, setItems] = useState<Array<{ id: number; nombre: string; fecha?: unknown }>>([]);
  const [empId, setEmpId] = useState<number | null>(null);
  const load = async () => {
    const eid = userId;
    setEmpId(eid);
    if (!eid) return;
    const f = await comunidadService.favoritosDe(eid).catch(() => []);
    const arr = Array.isArray(f) ? f : [];
    // Enriquecer con nombre si solo trae ids
    const enriched = await Promise.all(arr.map(async (x: Record<string, unknown>) => {
      const tw = x.trabajador as { id?: number; nombre?: string } | undefined;
      const tid = tw?.id ?? (typeof x.id === 'number' ? (x.id as number) : 0);
      if (tw?.nombre) return { id: tid, nombre: tw.nombre, fecha: x.fecha };
      const t = await trabajadorService.detalle(tid).catch(() => null);
      return { id: tid, nombre: t?.nombre ?? `#${tid}` };
    }));
    setItems(enriched);
  };
  useFocusEffect(useCallback(() => { load(); }, []));
  return (
    <Screen>
      <Brand />
      <Title title="Tu gente de confianza" />
      <FlatList data={items} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Sin favoritos." />}
        renderItem={({ item }) => (
          <AppCard>
            <Text style={styles.t}>{item.nombre}</Text>
            <TouchableOpacity onPress={() => router.push(`/empleador/trabajadores/${item.id}`)}>
              <Text style={styles.link}>Ver perfil →</Text>
            </TouchableOpacity>
            <AppButton title="Quitar favorito" variant="outline" onPress={async () => { if (empId == null) return; await comunidadService.quitarFavorito(empId, item.id); load(); }} />
          </AppCard>
        )} />
      <AppButton title="Explorar trabajadores" onPress={() => router.push('/empleador/trabajadores')} />
    </Screen>
  );
}
const styles = StyleSheet.create({
  t: { color: '#fff', fontWeight: '700' }, link: { color: colors.orange, marginVertical: 6, fontWeight: '600' },
});
