import React, { useState } from 'react';
import { Text, ScrollView, StyleSheet } from 'react-native';
import { router } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton, Note } from '../../../src/components/ui';
import { comunidadService } from '../../../src/api/services';
import { errMsg } from '../../../src/api/client';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function NuevaPublicacion() {
  const { userId } = useAuth();
  const [titulo, setTitulo] = useState('');
  const [contenido, setContenido] = useState('');
  const [error, setError] = useState('');
  const onSubmit = async () => {
    setError('');
    try {
      if (userId == null) throw new Error('No hay sesión activa.');
      await comunidadService.publicar({ titulo: titulo.trim(), contenido: contenido.trim(), autor: { id: userId } });
      router.back();
    } catch (e) { setError(errMsg(e)); }
  };
  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="Compartí con la comunidad" />
      <AppInput label="Título *" value={titulo} onChangeText={setTitulo} maxLength={150} placeholder={`${titulo.length}/150`} />
      <AppInput label="Contenido *" value={contenido} onChangeText={setContenido} multiline numberOfLines={5} style={{ minHeight: 110, textAlignVertical: 'top' }} />
      {error ? <Text style={styles.e}>{error}</Text> : null}
      <AppButton title="Publicar" onPress={onSubmit} />
      <Note>Espacio respetuoso, sin datos ajenos.</Note>
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({ e: { color: colors.danger } });
