import React, { useCallback, useState } from 'react';
import { Text, ScrollView, StyleSheet } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppInput, AppButton, Note } from '../../../src/components/ui';
import { suscripcionService } from '../../../src/api/services';
import type { Suscripcion } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function MiPlan() {
  const { userId } = useAuth();
  const [subs, setSubs] = useState<Suscripcion[]>([]);
  const [monto, setMonto] = useState('');
  const [msg, setMsg] = useState('');
  const [uid, setUid] = useState<number | null>(null);

  useFocusEffect(useCallback(() => {
    (async () => {
      const id = userId;
      setUid(id);
      if (!id) return;
      const s = await suscripcionService.porUsuario(id).catch(() => []);
      setSubs(Array.isArray(s) ? s : [s].filter(Boolean));
    })();
  }, [userId]));

  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="Un plan para tus proyectos" />
      {subs.map((s) => (
        <AppCard cream key={s.id}>
          <Text style={styles.c}>{s.nivel} · {s.activa ? 'Activa' : 'Inactiva'}</Text>
          <Text style={styles.c2}>{s.fechaInicio} → {s.fechaFin ?? '—'}</Text>
        </AppCard>
      ))}
      <AppCard>
        <Text style={styles.t}>Básico: límites (3 ofertas/mes). PRO: ilimitadas.</Text>
      </AppCard>
      <AppInput label="Monto transferencia" value={monto} onChangeText={setMonto} keyboardType="numeric" />
      <AppButton title="Registrar transferencia" onPress={async () => { if (uid == null) return; await suscripcionService.donar({ monto: Number(monto), asunto: 'Transferencia plan de empleador', usuario: { id: uid } }); setMsg('Registrada como Pendiente.'); setMonto(''); }} />
      {msg ? <Text style={styles.ok}>{msg}</Text> : null}
      <Note>La suscripción PRO se activa al verificar transferencia SUSCRIPCION (admin).</Note>
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  c: { color: '#000', fontWeight: '800' }, c2: { color: '#000' },
  t: { color: '#fff' }, ok: { color: colors.success, marginTop: 8 },
});
