import React, { useCallback, useState } from 'react';
import { Text, FlatList, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppInput, AppButton, Note, Empty } from '../../../src/components/ui';
import { perfilService } from '../../../src/api/services';
import { errMsg } from '../../../src/api/client';
import type { PortafolioItem } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Portafolio() {
  const { userId } = useAuth();
  const [tid, setTid] = useState<number | null>(userId);
  const [items, setItems] = useState<PortafolioItem[]>([]);
  const [titulo, setTitulo] = useState('');
  const [desc, setDesc] = useState('');
  const [url, setUrl] = useState('');
  const [error, setError] = useState('');

  const load = async (id: number): Promise<void> => {
    const list = await perfilService.portafolio(id).catch(() => []);
    setItems(Array.isArray(list) ? list : []);
  };
  useFocusEffect(useCallback(() => {
    (async () => {
      const id = userId;
      setTid(id);
      if (id) await load(id);
    })();
  }, [userId]));

  const agregar = async () => {
    setError('');
    if (!titulo.trim()) { setError('Título obligatorio (máx 120).'); return; }
    if (tid == null) { setError('Sin sesión.'); return; }
    try {
      await perfilService.agregarPortafolio(tid, { titulo: titulo.trim(), descripcion: desc, urlArchivo: url || undefined });
      setTitulo(''); setDesc(''); setUrl('');
      await load(tid);
    } catch (e) { setError(errMsg(e)); }
  };

  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="Mi portafolio" subtitle="Trabajos que hablan por vos." />
      <AppInput label="Título *" value={titulo} onChangeText={setTitulo} maxLength={120} placeholder={`${titulo.length}/120`} />
      <AppInput label="Descripción" value={desc} onChangeText={setDesc} multiline />
      <AppInput label="URL archivo (opcional)" value={url} onChangeText={setUrl} autoCapitalize="none" />
      {error ? <Text style={styles.e}>{error}</Text> : null}
      <AppButton title="Agregar al portafolio" onPress={agregar} />
      <Note>No se suben archivos, solo enlace.</Note>
      {items.map((it) => (
        <AppCard key={it.id}>
          <Text style={styles.t}>{it.titulo}</Text>
          <Text style={styles.b}>{it.descripcion ?? ''}</Text>
          <TouchableOpacity onPress={async () => { await perfilService.eliminarPortafolio(it.id); if (tid != null) load(tid); }}>
            <Text style={styles.del}>Eliminar trabajo</Text>
          </TouchableOpacity>
        </AppCard>
      ))}
      {items.length === 0 ? <Empty text="Sin trabajos en portafolio." /> : null}
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  t: { color: '#fff', fontWeight: '700' }, b: { color: colors.muted, marginTop: 4 },
  del: { color: colors.danger, marginTop: 8 }, e: { color: colors.danger },
});
