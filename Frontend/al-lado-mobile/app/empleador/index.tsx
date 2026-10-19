import React, { useCallback, useState } from 'react';
import { Text, ScrollView, StyleSheet, TouchableOpacity } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppButton, Badge } from '../../src/components/ui';
import { oportunidadService, postulacionService } from '../../src/api/services';
import type { Oportunidad } from '../../src/types';
import { useAuth } from '../../src/auth/AuthContext';
import { colors } from '../../src/theme';

export default function InicioEmpleador() {
  const { email } = useAuth();
  const [mias, setMias] = useState<Oportunidad[]>([]);

  useFocusEffect(useCallback(() => {
    (async () => {
      const all = await oportunidadService.listar().catch(() => []);
      setMias((Array.isArray(all) ? all : []).filter((o) => o.autor?.email === email).slice(0, 3));
    })();
  }, [email]));

  return (
    <Screen><ScrollView>
      <Brand />
      <Title title={`Hola, ${email?.split('@')[0] ?? ''}`} subtitle="Publicá, elegí y gestioná." />
      <AppCard cream>
        <Text style={styles.c}>¿Qué necesitás hoy?</Text>
        <AppButton title="Publicar una solicitud" onPress={() => router.push('/empleador/solicitudes/nueva')} />
      </AppCard>
      <Text style={styles.s}>Mis solicitudes activas</Text>
      {mias.map((o) => (
        <TouchableOpacity key={o.id} onPress={() => router.push(`/empleador/solicitudes/${o.id}`)}>
          <AppCard>
            <Badge active={o.activa}>{o.activa ? 'Activa' : 'Cerrada'}</Badge>
            <Text style={styles.t}>{o.titulo}</Text>
          </AppCard>
        </TouchableOpacity>
      ))}
      <AppButton title="Buscar trabajadores" variant="outline" onPress={() => router.push('/empleador/trabajadores')} />
      <AppButton title="Favoritos" variant="outline" onPress={() => router.push('/empleador/cuenta/favoritos')} />
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  c: { color: '#000', fontWeight: '700', marginBottom: 6 },
  s: { color: '#fff', fontWeight: '700', marginVertical: 10 },
  t: { color: '#fff', fontWeight: '700', marginTop: 6 },
});
