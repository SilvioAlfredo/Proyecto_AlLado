import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppButton, Badge, Empty } from '../../../src/components/ui';
import { oportunidadService } from '../../../src/api/services';
import type { Oportunidad } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function MisSolicitudes() {
  const { email } = useAuth();
  const [items, setItems] = useState<Oportunidad[]>([]);
  const [tab, setTab] = useState('Todas');
  useFocusEffect(useCallback(() => {
    (async () => {
      const all = await oportunidadService.listar().catch(() => []);
      setItems((Array.isArray(all) ? all : []).filter((o) => o.autor?.email === email));
    })();
  }, [email]));
  const list = tab === 'Todas' ? items : items.filter((o) => (tab === 'Activas' ? o.activa : !o.activa));
  return (
    <Screen>
      <Brand />
      <Title title="Administrá tus publicaciones" />
      <View style={styles.tabs}>
        {['Todas', 'Activas', 'Cerradas'].map((t) => (
          <TouchableOpacity key={t} onPress={() => setTab(t)} style={[styles.chip, tab === t && styles.a]}>
            <Text style={tab === t ? styles.ta : styles.t2}>{t}</Text>
          </TouchableOpacity>
        ))}
      </View>
      <AppButton title="Nueva solicitud" onPress={() => router.push('/empleador/solicitudes/nueva')} />
      <FlatList data={list} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Todavía no publicaste." />}
        renderItem={({ item }) => (
          <TouchableOpacity onPress={() => router.push(`/empleador/solicitudes/${item.id}`)}>
            <AppCard><Badge active={item.activa}>{item.activa ? 'Activa' : 'Cerrada'}</Badge>
            <Text style={styles.t}>{item.titulo}</Text></AppCard>
          </TouchableOpacity>
        )} />
    </Screen>
  );
}
const styles = StyleSheet.create({
  tabs: { flexDirection: 'row', gap: 6, marginBottom: 10 },
  chip: { borderWidth: 1, borderColor: colors.border, borderRadius: 999, paddingHorizontal: 12, paddingVertical: 6 },
  a: { backgroundColor: colors.yellow, borderColor: colors.yellow },
  t2: { color: colors.muted }, ta: { color: '#000', fontWeight: '700' },
  t: { color: '#fff', fontWeight: '700', marginTop: 6 },
});
