import React, { useCallback, useState } from 'react';
import { Text, ScrollView, StyleSheet } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppInput, AppButton, Note } from '../../../src/components/ui';
import { suscripcionService, resolveIdByEmail } from '../../../src/api/services';
import type { Suscripcion } from '../../../src/types';
import type { Donacion } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Suscripcion() {
  const { email, userId } = useAuth();
  const [uid, setUid] = useState<number | null>(userId);
  const [subs, setSubs] = useState<Suscripcion[]>([]);
  const [dons, setDons] = useState<Donacion[]>([]);
  const [monto, setMonto] = useState('');
  const [msg, setMsg] = useState('');

  useFocusEffect(useCallback(() => {
    (async () => {
      const id = userId ?? (await resolveIdByEmail(email));
      setUid(id);
      if (!id) return;
      const [s, d] = await Promise.all([
        suscripcionService.porUsuario(id).catch(() => null),
        suscripcionService.donacionesDe(id).catch(() => []),
      ]);
      setSubs(s ? [s] : []);
      setDons(Array.isArray(d) ? d : []);
    })();
  }, [email, userId]));

  const registrar = async () => {
    setMsg('');
    await suscripcionService.donar({ monto: Number(monto), usuario: { id: uid } });
    setMsg('Transferencia registrada como Pendiente.');
    setMonto('');
  };

  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="Mi suscripción y aportes" />
      {subs.map((s) => (
        <AppCard cream key={s.id}>
          <Text style={styles.c}>Plan {s.nivel} · {s.activa ? 'Activa' : 'Inactiva'}</Text>
        </AppCard>
      ))}
      <Note>El plan PRO con suscripción vigente habilita propuestas PRO, ofertas destacadas y beneficios de visibilidad. Registrar no paga ni activa solo.</Note>
      <AppInput label="Monto transferencia" value={monto} onChangeText={setMonto} keyboardType="numeric" />
      <AppButton title="Registrar transferencia" onPress={registrar} />
      {msg ? <Text style={styles.ok}>{msg}</Text> : null}
      {dons.map((d) => (
        <AppCard key={d.id}><Text style={styles.t}>${d.monto} · {d.estado}</Text></AppCard>
      ))}
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  c: { color: '#000', fontWeight: '700' }, t: { color: '#fff', fontWeight: '600' },
  ok: { color: colors.success, marginTop: 8 },
});
