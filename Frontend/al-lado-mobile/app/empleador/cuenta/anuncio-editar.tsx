import React, { useEffect, useState } from 'react';
import { Text, ScrollView, StyleSheet } from 'react-native';
import { router, useLocalSearchParams } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton } from '../../../src/components/ui';
import { publicidadService } from '../../../src/api/services';
import { errMsg, param } from '../../../src/api/client';
import { colors } from '../../../src/theme';

export default function EditarAnuncio() {
  const { id } = useLocalSearchParams();
  const [titulo, setTitulo] = useState('');
  const [contenido, setContenido] = useState('');
  const [urlImagen, setUrlImagen] = useState('');
  const [urlDestino, setUrlDestino] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      const all = await publicidadService.listar().catch(() => []);
      const a = (Array.isArray(all) ? all : []).find((x) => String(x.id) === param(id));
      if (a) { setTitulo(a.titulo ?? ''); setContenido(a.contenido ?? ''); setUrlImagen(a.urlImagen ?? ''); setUrlDestino(a.urlDestino ?? ''); }
    })();
  }, [id]);

  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="Editar publicidad" subtitle={`#${param(id)}`} />
      <AppInput label="Título *" value={titulo} onChangeText={setTitulo} maxLength={120} />
      <AppInput label="Contenido" value={contenido} onChangeText={setContenido} multiline />
      <AppInput label="URL imagen" value={urlImagen} onChangeText={setUrlImagen} autoCapitalize="none" />
      <AppInput label="URL destino" value={urlDestino} onChangeText={setUrlDestino} autoCapitalize="none" />
      {error ? <Text style={styles.e}>{error}</Text> : null}
      <AppButton title="Guardar cambios" onPress={async () => { try { await publicidadService.actualizar(param(id), { titulo, contenido, urlImagen, urlDestino }); router.back(); } catch (e) { setError(errMsg(e)); } }} />
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({ e: { color: colors.danger } });
