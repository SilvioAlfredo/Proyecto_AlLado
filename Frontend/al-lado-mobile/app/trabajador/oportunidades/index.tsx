import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, RefreshControl } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppCard, Badge, Empty, AppButton } from '../../../src/components/ui';
import { oportunidadService } from '../../../src/api/services';
import { errMsg } from '../../../src/api/client';
import type { Oportunidad } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Oportunidades() {
  const { email } = useAuth();
  const [items, setItems] = useState<Oportunidad[]>([]);
  const [q, setQ] = useState('');
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');

  const load = async () => {
    setError('');
    try {
      const data = await oportunidadService.listar();
      setItems(Array.isArray(data) ? data : []);
    } catch (e) {
      setError(errMsg(e));
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useFocusEffect(useCallback(() => { load(); }, []));

  const filtered = items.filter((o) =>
    `${o.titulo} ${o.contenido} ${o.ubicacion ?? ''}`.toLowerCase().includes(q.toLowerCase()),
  );

  return (
    <Screen>
      <Brand />
      <Title eyebrow="Oportunidades" title="Encontrá tu próximo trabajo" subtitle={email ? `Hola, ${email}` : ''} />
      <AppInput placeholder="Oficio, título o palabra clave" value={q} onChangeText={setQ} />
      <AppButton title="Filtrar oportunidades" variant="outline" onPress={() => router.push('/trabajador/oportunidades/filtros')} />
      <AppButton title="Propuestas PRO" onPress={() => router.push('/trabajador/oportunidades/propuestas-pro')} />
      {error ? <Text style={styles.error}>{error}</Text> : null}
      <Text style={styles.section}>Ofertas activas ({filtered.length})</Text>
      <FlatList
        data={filtered}
        keyExtractor={(i) => String(i.id)}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); load(); }} tintColor={colors.yellow} />}
        ListEmptyComponent={!loading ? <Empty text="Todavía no hay ofertas activas." /> : undefined}
        renderItem={({ item }) => (
          <TouchableOpacity onPress={() => router.push(`/trabajador/oportunidades/${item.id}`)}>
            <AppCard>
              <View style={styles.row}>
                <Badge active>{item.activa ? 'Activa' : 'Cerrada'}</Badge>
                {item.modalidad ? <Badge>{item.modalidad}</Badge> : null}
              </View>
              <Text style={styles.cardTitle}>{item.titulo}</Text>
              <Text style={styles.cardDesc} numberOfLines={2}>{item.contenido}</Text>
              <Text style={styles.meta}>{item.ubicacion ?? ''} · {item.rangoSalarial ?? ''}</Text>
              <Text style={styles.link}>Ver oportunidad →</Text>
            </AppCard>
          </TouchableOpacity>
        )}
      />
    </Screen>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', gap: 6, marginBottom: 8 },
  section: { color: '#fff', fontWeight: '700', marginVertical: 10 },
  cardTitle: { color: '#fff', fontWeight: '700', fontSize: 16 },
  cardDesc: { color: colors.muted, marginTop: 4 },
  meta: { color: colors.yellow, fontWeight: '600', marginTop: 6, fontSize: 12 },
  link: { color: colors.orange, marginTop: 6, fontWeight: '600' },
  error: { color: colors.danger, marginVertical: 8 },
});
