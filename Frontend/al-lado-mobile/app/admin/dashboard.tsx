import React, { useCallback, useState } from 'react';
import { Text, ScrollView, StyleSheet, TouchableOpacity } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import type { Href } from 'expo-router';
import { Screen, Brand, Title, AppCard } from '../../src/components/ui';
import { adminService } from '../../src/api/services';
import { errMsg } from '../../src/api/client';
import type { DashboardMetrics } from '../../src/types';
import { useAuth } from '../../src/auth/AuthContext';
import { colors } from '../../src/theme';

export default function Dashboard() {
  const { email, logout } = useAuth();
  const [m, setM] = useState<DashboardMetrics | null>(null);
  const [error, setError] = useState('');
  useFocusEffect(useCallback(() => {
    adminService.metrics().then(setM).catch((e) => setError(errMsg(e)));
  }, []));
  return (
    <Screen><ScrollView>
      <Brand admin />
      <Title eyebrow="Admin" title="La comunidad, en números" subtitle={`Hola, ${email}`} />
      {error ? <Text style={styles.e}>{error}</Text> : null}
      {m ? (
        <>
          <AppCard cream><Text style={styles.c}>Trabajadores: {m.metricTrabajadores}</Text></AppCard>
          <AppCard><Text style={styles.t}>Postulaciones: {m.metricContratos}</Text></AppCard>
          <AppCard><Text style={styles.t}>Completadas: {m.trabajosCompletados}</Text></AppCard>
          <AppCard><Text style={styles.t}>Promedio: ${m.metricAvg}</Text></AppCard>
          {m.totalIngresos != null ? <AppCard><Text style={styles.t}>Ingresos: {m.totalIngresos}</Text></AppCard> : null}
          {m.dineroComunidad != null ? <AppCard><Text style={styles.t}>Dinero comunidad: {m.dineroComunidad}</Text></AppCard> : null}
          {m.usuariosActivos != null || m.usuariosInactivos != null ? (
            <AppCard><Text style={styles.t}>Usuarios: {m.usuariosActivos ?? '—'} activos · {m.usuariosInactivos ?? '—'} inactivos</Text></AppCard>
          ) : null}
          {m.postulacionesPorEstado ? (
            <AppCard>
              <Text style={styles.t}>Postulaciones por estado</Text>
              {Object.entries(m.postulacionesPorEstado).map(([estado, n]) => (
                <Text key={estado} style={styles.sub}>{estado}: {n}</Text>
              ))}
            </AppCard>
          ) : null}
        </>
      ) : null}
      {[
        ['Personas', '/admin/personas'],
        ['Reportes y empresas', '/admin/moderacion'],
        ['Transferencias y donaciones', '/admin/aportes'],
        ['Etiquetas y comunicados', '/admin/gestion'],
      ].map(([t, href]) => (
        <TouchableOpacity key={href} onPress={() => router.push(href as Href)}>
          <AppCard><Text style={styles.link}>{t} →</Text></AppCard>
        </TouchableOpacity>
      ))}
      <TouchableOpacity onPress={async () => { await logout(); router.replace('/(auth)/login'); }}>
        <Text style={styles.out}>Cerrar sesión</Text>
      </TouchableOpacity>
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  c: { color: '#000', fontWeight: '800', fontSize: 20 },
  t: { color: '#fff', fontWeight: '700' },
  link: { color: '#fff', fontWeight: '600' },
  out: { color: colors.yellow, marginTop: 12, textAlign: 'center' },
  sub: { color: colors.muted, marginTop: 4 },
  e: { color: colors.danger },
});
