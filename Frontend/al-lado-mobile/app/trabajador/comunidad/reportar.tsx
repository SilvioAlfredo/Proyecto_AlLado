import React, { useState } from 'react';
import { Text, ScrollView, StyleSheet } from 'react-native';
import { router } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton, Note } from '../../../src/components/ui';
import { comunidadService, resolveIdByEmail } from '../../../src/api/services';
import { errMsg } from '../../../src/api/client';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Reportar() {
  const { email, userId } = useAuth();
  const [motivo, setMotivo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');
  const onSubmit = async () => {
    setError(''); setOk('');
    try {
      const autorId = userId ?? (await resolveIdByEmail(email));
      await comunidadService.reportar({ motivo: motivo.trim(), descripcion: descripcion.trim(), autor: { id: autorId } });
      setOk('Reporte enviado. El admin lo revisa.');
      setTimeout(() => router.back(), 800);
    } catch (e) { setError(errMsg(e)); }
  };
  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="Ayudanos a cuidar" subtitle="Reportar usuario" />
      <AppInput label="Motivo *" value={motivo} onChangeText={setMotivo} placeholder="Contenido inapropiado" />
      <AppInput label="Descripción *" value={descripcion} onChangeText={setDescripcion} multiline numberOfLines={4} style={{ minHeight: 90, textAlignVertical: 'top' }} />
      {error ? <Text style={styles.e}>{error}</Text> : null}
      {ok ? <Text style={styles.ok}>{ok}</Text> : null}
      <AppButton title="Enviar reporte" onPress={onSubmit} />
      <Note>Sin seguimiento en perfil. El admin revisa cada reporte.</Note>
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({ e: { color: colors.danger }, ok: { color: colors.success } });
