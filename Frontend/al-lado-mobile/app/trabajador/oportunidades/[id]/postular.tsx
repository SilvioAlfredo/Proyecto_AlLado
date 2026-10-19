import React, { useState } from 'react';
import { Text, ScrollView, StyleSheet } from 'react-native';
import { router, useLocalSearchParams } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton, Note } from '../../../../src/components/ui';
import { postulacionService, resolveIdByEmail } from '../../../../src/api/services';
import { errMsg, param } from '../../../../src/api/client';
import { useAuth } from '../../../../src/auth/AuthContext';
import { colors } from '../../../../src/theme';

export default function Postular() {
  const { id } = useLocalSearchParams();
  const { email, userId } = useAuth();
  const [titulo, setTitulo] = useState('');
  const [contenido, setContenido] = useState('');
  const [monto, setMonto] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const onSubmit = async () => {
    setError('');
    if (!titulo.trim() || !contenido.trim()) {
      setError('Título y contenido son obligatorios.');
      return;
    }
    setLoading(true);
    try {
      const autorId = userId ?? (await resolveIdByEmail(email, 'TRABAJADOR'));
      await postulacionService.crear({
        titulo: titulo.trim(),
        contenido: contenido.trim(),
        remuneracionPretendida: monto ? Number(monto) : undefined,
        oportunidadLaboral: { id: Number(param(id)) },
        autor: { id: autorId },
      });
      router.replace('/trabajador/postulaciones');
    } catch (e) {
      setError(errMsg(e));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Screen>
      <ScrollView>
        <Brand />
        <Title title="Presentá tu propuesta" subtitle={`Oferta #${param(id)}`} />
        <AppInput label="Título de la propuesta *" value={titulo} onChangeText={setTitulo} maxLength={150} />
        <AppInput label="Contenido *" value={contenido} onChangeText={setContenido} multiline numberOfLines={4} style={{ minHeight: 90, textAlignVertical: 'top' }} />
        <AppInput label="Remuneración pretendida (opcional)" value={monto} onChangeText={setMonto} keyboardType="numeric" placeholder="50000" />
        {error ? <Text style={styles.error}>{error}</Text> : null}
        <AppButton title="Enviar postulación" onPress={onSubmit} loading={loading} />
        <Note>Se envía como Pendiente. Solo el empleador actualiza estados.</Note>
      </ScrollView>
    </Screen>
  );
}

const styles = StyleSheet.create({
  error: { color: colors.danger, marginVertical: 8 },
});
