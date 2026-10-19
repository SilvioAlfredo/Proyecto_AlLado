import React, { useCallback, useState } from 'react';
import { Text, ScrollView, StyleSheet } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppInput, AppButton, Note } from '../../../src/components/ui';
import { empresaService, resolveIdByEmail } from '../../../src/api/services';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Empresa() {
  const { email, userId } = useAuth();
  const [eid, setEid] = useState<number | null>(userId);
  const [nombre, setNombre] = useState('');
  const [cuit, setCuit] = useState('');
  const [desc, setDesc] = useState('');
  const [web, setWeb] = useState('');
  const [verificado, setVerificado] = useState(false);
  const [msg, setMsg] = useState('');

  useFocusEffect(useCallback(() => {
    (async () => {
      const id = userId ?? (await resolveIdByEmail(email, 'EMPLEADOR'));
      setEid(id);
      if (!id) return;
      const p = await empresaService.perfil(id).catch(() => null);
      if (p) { setNombre(String((p as Record<string, unknown>).nombre ?? '')); setCuit(String((p as Record<string, unknown>).cuit ?? '')); setDesc(String((p as Record<string, unknown>).descripcion ?? '')); setWeb(String((p as Record<string, unknown>).sitioWeb ?? '')); setVerificado(Boolean((p as Record<string, unknown>).verificado)); }
    })();
  }, [email, userId]));

  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="Tu empresa en AlLado" subtitle={verificado ? 'Empresa verificada' : 'Sin verificar (verifica el admin)'} />
      <AppInput label="Nombre *" value={nombre} onChangeText={setNombre} />
      <AppInput label="CUIT" value={cuit} onChangeText={setCuit} />
      <AppInput label="Descripción" value={desc} onChangeText={setDesc} multiline />
      <AppInput label="Sitio web" value={web} onChangeText={setWeb} autoCapitalize="none" />
      <AppButton title="Guardar perfil empresarial" onPress={async () => { if (eid == null) return; await empresaService.guardar(eid, { nombre, cuit, descripcion: desc, sitioWeb: web }); setMsg('Guardado.'); }} />
      {msg ? <Text style={styles.ok}>{msg}</Text> : null}
      <Note>Solo lectura para verificación. El admin marca verificado.</Note>
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({ ok: { color: colors.success } });
