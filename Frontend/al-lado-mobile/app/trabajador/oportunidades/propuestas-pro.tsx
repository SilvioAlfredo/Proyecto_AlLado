import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, RefreshControl } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, Badge, Empty, AppButton, Note } from '../../../src/components/ui';
import { oportunidadService, resolveIdByEmail } from '../../../src/api/services';
import { ApiError, errMsg } from '../../../src/api/client';
import type { Oportunidad } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

// HU trabajador "Acceder a propuestas pro": requiere suscripción PRO vigente.
// Endpoint: GET /oportunidades/propuestas-pro?trabajadorId=
// Espejo de app/trabajador/propuestas (web).
export default function PropuestasPro() {
  const { email, userId } = useAuth();
  const [items, setItems] = useState<Oportunidad[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [sinPlan, setSinPlan] = useState(false);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setError('');
    setSinPlan(false);
    try {
      const id = userId ?? (await resolveIdByEmail(email, 'TRABAJADOR'));
      if (!id) throw new Error('No se pudo identificar tu perfil de trabajador.');
      const data = await oportunidadService.propuestasPro(id);
      setItems(Array.isArray(data) ? data : []);
    } catch (e) {
      if (e instanceof ApiError && (e.status === 400 || e.status === 403)) {
        setSinPlan(true);
        setItems([]);
      } else {
        setError(errMsg(e));
      }
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [email, userId]);

  useFocusEffect(useCallback(() => { load(); }, [load]));

  if (!loading && sinPlan) {
    return (
      <Screen>
        <Brand />
        <Title eyebrow="PRO" title="Propuestas PRO" />
        <AppCard>
          <Text style={styles.cardTitle}>Necesitás el plan PRO</Text>
          <Text style={styles.cardDesc}>Las propuestas compatibles con tus especialidades (destacadas primero) son un beneficio PRO.</Text>
        </AppCard>
        <AppButton title="Ver mi suscripción" variant="outline" onPress={() => router.push('/trabajador/perfil/suscripcion')} />
      </Screen>
    );
  }

  return (
    <Screen>
      <Brand />
      <Title eyebrow="PRO" title="Propuestas PRO" subtitle="Compatibles con tus especialidades, destacadas primero." />
      {error ? <Text style={styles.error}>{error}</Text> : null}
      <Note>Beneficio del plan PRO con suscripción vigente.</Note>
      <FlatList
        data={items}
        keyExtractor={(i) => String(i.id)}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); load(); }} tintColor={colors.yellow} />}
        ListEmptyComponent={!loading ? <Empty text="Todavía no hay propuestas compatibles con tu perfil." /> : undefined}
        renderItem={({ item }) => (
          <TouchableOpacity onPress={() => router.push(`/trabajador/oportunidades/${item.id}`)}>
            <AppCard>
              <View style={styles.row}>
                {item.destacada ? <Badge active>Destacada</Badge> : null}
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
  cardTitle: { color: '#fff', fontWeight: '700', fontSize: 16 },
  cardDesc: { color: colors.muted, marginTop: 4 },
  meta: { color: colors.yellow, fontWeight: '600', marginTop: 6, fontSize: 12 },
  link: { color: colors.orange, marginTop: 6, fontWeight: '600' },
  error: { color: colors.danger, marginVertical: 8 },
});
