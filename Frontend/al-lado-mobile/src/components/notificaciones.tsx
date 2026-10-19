import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, RefreshControl } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { AppCard, Badge, Empty, AppButton } from './ui';
import { notificacionService } from '../api/services';
import { errMsg } from '../api/client';
import type { Notificacion } from '../types';
import { colors } from '../theme';

const TIPO_LABEL: Record<string, string> = {
  POSTULACION_RECIBIDA: 'Postulación',
  POSTULACION_ESTADO: 'Estado',
  SISTEMA: 'Sistema',
};

// Lista compartida trabajador/empleador. Espejo de NotificacionesList (web).
export function NotificacionesList({ usuarioId }: { usuarioId: number }): React.JSX.Element {
  const [items, setItems] = useState<Notificacion[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');

  const cargar = useCallback(async () => {
    setError('');
    try {
      const data = await notificacionService.listar(usuarioId);
      setItems(Array.isArray(data) ? data : []);
    } catch (e) {
      setError(errMsg(e));
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [usuarioId]);

  useFocusEffect(useCallback(() => { cargar(); }, [cargar]));

  const leer = async (id?: number) => {
    if (!id) return;
    try {
      await notificacionService.marcarLeida(id);
      setItems((prev) => prev.map((n) => (n.id === id ? { ...n, leida: true } : n)));
    } catch (e) {
      setError(errMsg(e));
    }
  };

  const leerTodas = async () => {
    try {
      await notificacionService.marcarTodasLeidas(usuarioId);
      setItems((prev) => prev.map((n) => ({ ...n, leida: true })));
    } catch (e) {
      setError(errMsg(e));
    }
  };

  return (
    <View style={{ flex: 1 }}>
      {error ? <Text style={styles.error}>{error}</Text> : null}
      {items.some((n) => !n.leida) ? (
        <AppButton title="Marcar todas como leídas" variant="outline" onPress={leerTodas} />
      ) : null}
      <FlatList
        data={items}
        keyExtractor={(i) => String(i.id)}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); cargar(); }} tintColor={colors.yellow} />}
        ListEmptyComponent={!loading ? <Empty text="No tenés notificaciones todavía." /> : undefined}
        renderItem={({ item }) => (
          <AppCard style={!item.leida ? styles.noLeida : undefined}>
            <View style={styles.row}>
              <Text style={styles.title}>{item.titulo}</Text>
              <Badge active={!item.leida}>{TIPO_LABEL[item.tipo ?? ''] ?? item.tipo ?? 'Aviso'}</Badge>
            </View>
            <Text style={styles.msg}>{item.mensaje}</Text>
            <View style={styles.row}>
              <Text style={styles.date}>{item.fechaCreacion ?? ''}</Text>
              {!item.leida ? (
                <TouchableOpacity onPress={() => leer(item.id)}>
                  <Text style={styles.leer}>Marcar como leída</Text>
                </TouchableOpacity>
              ) : null}
            </View>
          </AppCard>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  noLeida: { borderColor: colors.yellow },
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', gap: 8, marginBottom: 6 },
  title: { color: '#fff', fontWeight: '700', fontSize: 15, flex: 1 },
  msg: { color: colors.muted, fontSize: 13 },
  date: { color: colors.muted, fontSize: 11 },
  leer: { color: colors.yellow, fontWeight: '700', fontSize: 12 },
  error: { color: colors.danger, marginVertical: 8 },
});
