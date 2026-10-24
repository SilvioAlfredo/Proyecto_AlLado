import React, { useEffect, useState } from 'react';
import { Text, ScrollView, StyleSheet, TouchableOpacity } from 'react-native';
import { router } from 'expo-router';
import type { Href } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton, AppCard } from '../../../src/components/ui';
import { empleadorService, authService } from '../../../src/api/services';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Cuenta() {
  const { email, userId, logout } = useAuth();
  const [id, setId] = useState<number | null>(userId);
  const [nombre, setNombre] = useState('');
  const [telefono, setTelefono] = useState('');
  const [cur, setCur] = useState('');
  const [nw, setNw] = useState('');
  const [msg, setMsg] = useState('');

  useEffect(() => {
    (async () => {
      const rid = userId;
      setId(rid);
      if (!rid) return;
      const p = await empleadorService.detalle(rid).catch(() => null);
      if (p) { setNombre(p.nombre ?? ''); setTelefono(p.telefono ?? ''); }
    })();
  }, []);

  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="Mi perfil y cuenta" subtitle={email ?? undefined} />
      <AppInput label="Nombre" value={nombre} onChangeText={setNombre} />
      <AppInput label="Teléfono" value={telefono} onChangeText={setTelefono} />
      <AppButton title="Guardar perfil" onPress={async () => { if (id == null) return; await empleadorService.actualizar(id, { nombre, telefono }); setMsg('Perfil guardado.'); }} />
      {msg ? <Text style={styles.ok}>{msg}</Text> : null}
      <Text style={styles.s}>Cambiar contraseña</Text>
      <AppInput label="Actual" value={cur} onChangeText={setCur} secureTextEntry />
      <AppInput label="Nueva (mín 8)" value={nw} onChangeText={setNw} secureTextEntry />
      <AppButton title="Actualizar contraseña" variant="outline" onPress={() => authService.changePassword(email ?? '', cur, nw).then(() => setMsg('Contraseña actualizada.'))} />
      {[
        ['Perfil empresarial', '/empleador/cuenta/empresa'],
        ['Mis anuncios', '/empleador/cuenta/anuncios'],
        ['Mi plan', '/empleador/cuenta/plan'],
        ['Favoritos', '/empleador/cuenta/favoritos'],
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
  s: { color: '#fff', fontWeight: '700', marginTop: 14 },
  link: { color: '#fff', fontWeight: '600' },
  ok: { color: colors.success, marginTop: 8 },
});
