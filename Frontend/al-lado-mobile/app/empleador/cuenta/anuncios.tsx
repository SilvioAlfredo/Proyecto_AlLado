import React, { useCallback, useState } from 'react';
import { Text, FlatList, TouchableOpacity, StyleSheet, Alert } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppButton, Empty, AppInput, Note } from '../../../src/components/ui';
import { publicidadService } from '../../../src/api/services';
import type { Publicidad } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Anuncios() {
  const { userId } = useAuth();
  const [items, setItems] = useState<Publicidad[]>([]);
  const [titulo, setTitulo] = useState('');
  const [contenido, setContenido] = useState('');

  const load = async () => {
    const all = await publicidadService.listar().catch(() => []);
    const eid = userId;
    setItems((Array.isArray(all) ? all : []).filter((a) => !eid || String(a.anunciante?.id) === String(eid)));
  };
  useFocusEffect(useCallback(() => { load(); }, []));

  const crear = async () => {
    if (userId == null) return;
    await publicidadService.crear({ titulo, contenido, activa: true, anunciante: { id: userId } });
    setTitulo(''); setContenido('');
    load();
  };

  return (
    <Screen>
      <Brand />
      <Title title="Dale visibilidad a tu empresa" />
      <AppInput label="Título *" value={titulo} onChangeText={setTitulo} maxLength={120} />
      <AppInput label="Contenido" value={contenido} onChangeText={setContenido} multiline />
      <AppButton title="Publicar anuncio" onPress={crear} />
      <Note>Fechas y activo se administran al editar (PUT).</Note>
      <FlatList data={items} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Sin anuncios." />}
        renderItem={({ item }) => (
          <AppCard>
            <Text style={styles.t}>{item.titulo}</Text>
            <Text style={styles.b} numberOfLines={2}>{item.contenido}</Text>
            <TouchableOpacity onPress={() => router.push(`/empleador/cuenta/anuncio-editar?id=${item.id}`)}>
              <Text style={styles.link}>Editar anuncio →</Text>
            </TouchableOpacity>
            <AppButton title="Eliminar" variant="danger" onPress={() => Alert.alert('Eliminar', '¿Confirmás?', [{ text: 'No' }, { text: 'Sí', onPress: async () => { await publicidadService.eliminar(item.id); load(); } }])} />
          </AppCard>
        )} />
    </Screen>
  );
}
const styles = StyleSheet.create({
  t: { color: '#fff', fontWeight: '700' }, b: { color: colors.muted },
  link: { color: colors.orange, marginVertical: 6, fontWeight: '600' },
});
