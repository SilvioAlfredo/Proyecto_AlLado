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
  const [asunto, setAsunto] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [msg, setMsg] = useState('');

  const cargarDatos = useCallback(async () => {
    const id = userId ?? (await resolveIdByEmail(email));
    setUid(id);
    if (!id) return;
    const [s, d] = await Promise.all([
      suscripcionService.porUsuario(id).catch(() => null),
      suscripcionService.donacionesDe(id).catch(() => []),
    ]);
    setSubs(s ? [s] : []);
    setDons(Array.isArray(d) ? d : []);
  }, [email, userId]);

  useFocusEffect(useCallback(() => {
    cargarDatos();
  }, [cargarDatos]));

  const registrar = async () => {
    setMsg('');
    setErrorMsg('');

    const montoNum = Number(monto);
    if (!monto || isNaN(montoNum) || montoNum <= 0) {
      setErrorMsg('Ingresá un monto válido mayor a 0.');
      return;
    }
    if (!asunto.trim()) {
      setErrorMsg('El comprobante o asunto de la transferencia es obligatorio.');
      return;
    }
    if (!uid) {
      setErrorMsg('No se pudo identificar al usuario emisor.');
      return;
    }

    try {
      // AL-MOB-002: Envío de payload verificable completo (monto, asunto, usuario)
      await suscripcionService.donar({
        monto: montoNum,
        asunto: asunto.trim(),
        usuario: { id: uid },
      });
      setMsg('Transferencia registrada como Pendiente de verificación.');
      setMonto('');
      setAsunto('');
      await cargarDatos();
    } catch (e: any) {
      setErrorMsg(e?.response?.data?.message || 'Error al registrar la transferencia.');
    }
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
        <Note>El plan PRO con suscripción vigente habilita propuestas PRO, ofertas destacadas y beneficios de visibilidad. El registro queda en estado PENDIENTE hasta que el administrador verifique el comprobante.</Note>

        <AppInput label="Monto transferencia *" value={monto} onChangeText={setMonto} keyboardType="numeric" />
        <AppInput label="Asunto / Comprobante de transferencia *" value={asunto} onChangeText={setAsunto} placeholder="Ej: Transf. Santander #48291" />
        <AppButton title="Registrar transferencia" onPress={registrar} />

        {msg ? <Text style={styles.ok}>{msg}</Text> : null}
        {errorMsg ? <Text style={styles.err}>{errorMsg}</Text> : null}

        <Title title="Historial de aportes" subtitle="Aportes enviados para verificación" />
        {dons.length === 0 ? (
            <Note>No registraste aportes previos.</Note>
        ) : (
            dons.map((d) => (
                <AppCard key={d.id}>
                  <Text style={styles.t}>${d.monto} · {d.estado}</Text>
                  {d.asunto ? <Text style={styles.subText}>Comprobante: {d.asunto}</Text> : null}
                </AppCard>
            ))
        )}
      </ScrollView></Screen>
  );
}

const styles = StyleSheet.create({
  c: { color: '#000', fontWeight: '700' },
  t: { color: '#fff', fontWeight: '600' },
  subText: { color: '#ddd', fontSize: 12, marginTop: 4 },
  ok: { color: colors.success, marginTop: 8 },
  err: { color: '#ff4d4f', marginTop: 8 },
});