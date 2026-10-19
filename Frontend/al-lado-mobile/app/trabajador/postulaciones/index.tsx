import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, Badge, Empty } from '../../../src/components/ui';
import { postulacionService } from '../../../src/api/services';
import { errMsg } from '../../../src/api/client';
import type { Postulacion } from '../../../src/types';
import { colors } from '../../../src/theme';

const FILTROS = ['Todas', 'PENDIENTE', 'SELECCIONADA', 'RECHAZADA', 'COMPLETADA'];

export default function MisPostulaciones() {
  const [items, setItems] = useState<Postulacion[]>([]);
  const [f, setF] = useState('Todas');
  const [error, setError] = useState('');

  useFocusEffect(useCallback(() => {
    postulacionService.listar().then((d) => setItems(Array.isArray(d) ? d : [])).catch((e) => setError(errMsg(e)));
  }, []));

  const list = f === 'Todas' ? items : items.filter((p) => p.estado === f);

  return (
    <Screen>
      <Brand />
      <Title eyebrow="Mis postulaciones" title="Tus propuestas" />
      <View style={styles.chips}>
        {FILTROS.map((x) => (
          <TouchableOpacity key={x} onPress={() => setF(x)} style={[styles.chip, f === x && styles.active]}>
            <Text style={f === x ? styles.tActive : styles.t}>{x}</Text>
          </TouchableOpacity>
        ))}
      </View>
      {error ? <Text style={styles.error}>{error}</Text> : null}
      <FlatList
        data={list}
        keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Todavía no te postulaste." />}
        renderItem={({ item }) => (
          <TouchableOpacity onPress={() => router.push(`/trabajador/postulaciones/${item.id}`)}>
            <AppCard>
              <Badge active={item.estado === 'SELECCIONADA'}>{item.estado}</Badge>
              <Text style={styles.t2}>{item.titulo}</Text>
              <Text style={styles.m}>Pretensión: {item.remuneracionPretendida ?? '—'}</Text>
            </AppCard>
          </TouchableOpacity>
        )}
      />
    </Screen>
  );
}

const styles = StyleSheet.create({
  chips: { flexDirection: 'row', flexWrap: 'wrap', gap: 6, marginBottom: 10 },
  chip: { borderWidth: 1, borderColor: colors.border, borderRadius: 999, paddingHorizontal: 10, paddingVertical: 5 },
  active: { backgroundColor: colors.yellow, borderColor: colors.yellow },
  t: { color: colors.muted, fontSize: 11, fontWeight: '700' },
  tActive: { color: '#000', fontSize: 11, fontWeight: '700' },
  t2: { color: '#fff', fontWeight: '700', marginTop: 6 },
  m: { color: colors.yellow, marginTop: 4, fontSize: 12 },
  error: { color: colors.danger },
});
