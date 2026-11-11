import React, { useCallback, useState } from 'react';
import { Text, ScrollView, StyleSheet, TouchableOpacity } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import type { Href } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppButton } from '../../../src/components/ui';
import { trabajadorService, comunidadService, resolveIdByEmail } from '../../../src/api/services';
import type { Trabajador } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function MiPerfil() {
  const { email, userId, logout } = useAuth();
  const [p, setP] = useState<Trabajador | null>(null);
  const [prom, setProm] = useState<number | null>(null);

  useFocusEffect(useCallback(() => {
    (async () => {
      const id = userId ?? (await resolveIdByEmail(email, 'TRABAJADOR'));
      if (!id) return;
      const data = await trabajadorService.detalle(id).catch(() => null);
      setP(data);
      if (data?.id) {
        const avg = await comunidadService.promedio(data.id).catch(() => null);
        setProm(avg);
      }
    })();
  }, [email, userId]));

  const initials = (p?.nombre ?? email ?? '?').slice(0, 2).toUpperCase();

  return (
    <Screen><ScrollView>
      <Brand />
      <Title eyebrow="Mi perfil" title={p?.nombre ?? 'Mi cuenta'} subtitle={(p?.especialidades ?? []).join(' · ')} />
      <AppCard>
        <Text style={styles.avatar}>{initials}</Text>
        <Text style={styles.rating}>★ {typeof prom === 'number' ? prom.toFixed(1) : '—'}</Text>
        <Text style={styles.bio}>{p?.biografia ?? 'Sin biografía.'}</Text>
        <Text style={styles.m}>{p?.email} · {p?.telefono ?? ''}</Text>
      </AppCard>
      {[
        ['Editar mi perfil', '/trabajador/perfil/editar'],
        ['Disponibilidad semanal', '/trabajador/perfil/disponibilidad'],
        ['Mi portafolio', '/trabajador/perfil/portafolio'],
        ['Valoraciones recibidas', '/trabajador/perfil/valoraciones'],
        ['Mi suscripción y aportes', '/trabajador/perfil/suscripcion'],
        ['Preferencias', '/trabajador/perfil/preferencias'],
      ].map(([t, href]) => (
        <TouchableOpacity key={href} onPress={() => router.push(href as Href)}>
          <AppCard><Text style={styles.link}>{t} →</Text></AppCard>
        </TouchableOpacity>
      ))}
      <AppButton title="Cerrar sesión" variant="outline" onPress={async () => { await logout(); router.replace('/(auth)/login'); }} />
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  avatar: { fontSize: 28, fontWeight: '800', color: colors.orange },
  rating: { color: colors.yellow, fontWeight: '700', marginTop: 4 },
  bio: { color: '#fff', marginTop: 8 }, m: { color: colors.muted, marginTop: 4 },
  link: { color: '#fff', fontWeight: '600' },
});
