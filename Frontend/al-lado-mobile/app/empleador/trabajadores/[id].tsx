import React, { useCallback, useState } from 'react';
import { Text, ScrollView, StyleSheet } from 'react-native';
import { useLocalSearchParams, useFocusEffect } from 'expo-router';
import { Screen, Brand, AppCard, AppButton, Note } from '../../../src/components/ui';
import { trabajadorService, comunidadService, perfilService } from '../../../src/api/services';
import { param } from '../../../src/api/client';
import type { Horario, Trabajador, Valoracion } from '../../../src/types';
import { colors } from '../../../src/theme';

export default function PerfilTrabajador() {
  const { id } = useLocalSearchParams();
  const [t, setT] = useState<Trabajador | null>(null);
  const [prom, setProm] = useState<number | null>(null);
  const [vals, setVals] = useState<Valoracion[]>([]);
  const [hor, setHor] = useState<Horario[]>([]);
  useFocusEffect(useCallback(() => {
    (async () => {
      const sid = param(id);
      setT(await trabajadorService.detalle(sid).catch(() => null));
      setProm(await comunidadService.promedio(sid).catch(() => null));
      setVals(await comunidadService.valoracionesDe(sid).catch(() => []));
      setHor(await perfilService.horarios(sid).catch(() => []));
    })();
  }, [id]));
  if (!t) return <Screen><Brand /><Text style={styles.m}>Cargando...</Text></Screen>;
  return (
    <Screen><ScrollView>
      <Brand />
      <Text style={styles.t}>{t.nombre}</Text>
      <Text style={styles.m}>{(t.especialidades ?? []).join(' · ')} · ★ {typeof prom === 'number' ? prom.toFixed(1) : '—'}</Text>
      <AppCard><Text style={styles.b}>{t.biografia ?? 'Sin bio.'}</Text>
      <Text style={styles.m}>Contacto: {t.email} · {t.telefono ?? ''}</Text></AppCard>
      <Text style={styles.s}>Horarios</Text>
      {(Array.isArray(hor) ? hor : []).map((h) => <Text key={h.id} style={styles.m}>{h.diaSemana} {h.horaInicio}–{h.horaFin}</Text>)}
      <Text style={styles.s}>Opiniones</Text>
      {(Array.isArray(vals) ? vals : ((vals as unknown as { content?: Valoracion[] }).content ?? [])).slice(0, 5).map((v: Valoracion) => (
        <AppCard key={v.id}><Text style={styles.t2}>★ {v.puntuacion}/5</Text><Text style={styles.b}>{v.comentario}</Text></AppCard>
      ))}
      <Note>Coordiná por email/teléfono. Los horarios están en el perfil.</Note>
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  t: { color: '#fff', fontSize: 22, fontWeight: '800' }, t2: { color: '#fff', fontWeight: '700' },
  m: { color: colors.muted, marginTop: 4 }, b: { color: '#fff' }, s: { color: '#fff', fontWeight: '700', marginTop: 12 },
});
