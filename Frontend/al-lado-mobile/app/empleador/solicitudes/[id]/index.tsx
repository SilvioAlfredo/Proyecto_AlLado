import React, { useCallback, useState } from 'react';
import { View, Text, ScrollView, StyleSheet, Alert } from 'react-native';
import { router, useLocalSearchParams, useFocusEffect } from 'expo-router';
import { Screen, Brand, AppCard, Badge, AppButton, Note } from '../../../../src/components/ui';
import { oportunidadService, postulacionService } from '../../../../src/api/services';
import { param } from '../../../../src/api/client';
import type { Oportunidad, Postulacion } from '../../../../src/types';
import { colors } from '../../../../src/theme';

export default function GestionSolicitud() {
  const { id } = useLocalSearchParams();
  const [o, setO] = useState<Oportunidad | null>(null);
  const [post, setPost] = useState<Postulacion[]>([]);

  const load = async () => {
    const sid = param(id);
    const d = await oportunidadService.detalle(sid).catch(() => null);
    setO(d);
    const all = await postulacionService.listar().catch(() => []);
    setPost((Array.isArray(all) ? all : []).filter((p) => String(p.oportunidadLaboral?.id) === sid));
  };
  useFocusEffect(useCallback(() => { load(); }, [id]));

  if (!o) return <Screen><Brand /><Text style={styles.m}>Cargando...</Text></Screen>;
  const pend = post.filter((p) => p.estado === 'PENDIENTE').length;

  return (
    <Screen><ScrollView>
      <Brand />
      <Badge active={o.activa}>{o.activa ? 'Activa' : 'Cerrada'}</Badge>
      <Text style={styles.t}>{o.titulo}</Text>
      <Text style={styles.b}>{o.contenido}</Text>
      <AppCard>
        <Text style={styles.m}>Propuestas recibidas: {post.length} ({pend} pendientes)</Text>
        <AppButton title="Ver postulantes" onPress={() => router.push(`/empleador/solicitudes/${param(id)}/postulantes`)} />
      </AppCard>
      <View style={styles.row}>
        <View style={{ flex: 1 }}><AppButton title={o.activa ? 'Cerrar solicitud' : 'Reabrir'} variant="outline" onPress={async () => { await oportunidadService.actualizar(param(id), { activa: !o.activa }); load(); }} /></View>
        <View style={{ flex: 1 }}><AppButton title="Eliminar" variant="danger" onPress={() => Alert.alert('Eliminar', '¿Confirmás?', [{ text: 'No' }, { text: 'Sí', onPress: async () => { await oportunidadService.eliminar(param(id)); router.back(); } }])} /></View>
      </View>
      <Note>Seleccionar no cierra ni rechaza otras. Permite múltiples seleccionados.</Note>
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  t: { color: '#fff', fontSize: 20, fontWeight: '800', marginTop: 8 },
  b: { color: colors.muted, marginTop: 6 }, m: { color: colors.muted },
  row: { flexDirection: 'row', gap: 8 },
});
