import React, { useCallback, useState } from 'react';
import { Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppButton, Empty } from '../../../src/components/ui';
import { comunidadService } from '../../../src/api/services';
import { errMsg } from '../../../src/api/client';
import type { ForoPost } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Comunidad() {
  const { email } = useAuth();
  const [items, setItems] = useState<ForoPost[]>([]);
  const [error, setError] = useState('');
  useFocusEffect(useCallback(() => {
    comunidadService.foro().then((d) => setItems(Array.isArray(d) ? d : [])).catch((e) => setError(errMsg(e)));
  }, []));
  return (
    <Screen>
      <Brand />
      <Title eyebrow="Comunidad" title="Lo construimos entre todos" />
      <AppButton title="Nueva publicación" onPress={() => router.push('/trabajador/comunidad/nueva')} />
      <AppButton title="Reportar usuario" variant="outline" onPress={() => router.push('/trabajador/comunidad/reportar')} />
      {error ? <Text style={styles.e}>{error}</Text> : null}
      <FlatList
        data={items} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Todavía no hay publicaciones." />}
        renderItem={({ item }) => (
          <AppCard>
            <Text style={styles.t}>{item.titulo}</Text>
            <Text style={styles.b} numberOfLines={3}>{item.contenido}</Text>
            <Text style={styles.m}>{item.autor?.nombre ?? item.autor?.email ?? ''}</Text>
            {item.autor?.email === email ? (
              <TouchableOpacity onPress={async () => { await comunidadService.eliminarPost(item.id); setItems((s) => s.filter((x) => x.id !== item.id)); }}>
                <Text style={styles.del}>Eliminar mi publicación</Text>
              </TouchableOpacity>
            ) : null}
          </AppCard>
        )}
      />
    </Screen>
  );
}
const styles = StyleSheet.create({
  t: { color: '#fff', fontWeight: '700' }, b: { color: colors.muted, marginTop: 4 },
  m: { color: colors.muted, fontSize: 12, marginTop: 6 }, e: { color: colors.danger },
  del: { color: colors.danger, marginTop: 8, fontWeight: '600' },
});
