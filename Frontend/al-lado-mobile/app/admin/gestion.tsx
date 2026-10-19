import React, { useCallback, useState } from 'react';
import { Text, ScrollView, StyleSheet, TouchableOpacity } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppInput, AppButton, Empty, Note } from '../../src/components/ui';
import { adminService, comunidadService, etiquetasService } from '../../src/api/services';
import type { Etiqueta, Comunicado } from '../../src/types';
import { colors } from '../../src/theme';

export default function Gestion() {
  const [etiquetas, setEtiquetas] = useState<Etiqueta[]>([]);
  const [nombre, setNombre] = useState('');
  const [coms, setComs] = useState<Comunicado[]>([]);
  const [titulo, setTitulo] = useState('');
  const [contenido, setContenido] = useState('');

  const load = async () => {
    const [e, c] = await Promise.all([
      comunidadService.foro().catch(() => []),
      adminService ? comunidadService.comunicados().catch(() => []) : [],
    ]);
    void e;
    setEtiquetas(await etiquetasService.listar().catch(() => [] as Etiqueta[]));
    setComs(Array.isArray(c) ? c : (c as unknown as { content?: Comunicado[] })?.content ?? (c as Comunicado[]));
  };
  useFocusEffect(useCallback(() => { load(); }, []));

  return (
    <Screen><ScrollView>
      <Brand admin />
      <Title eyebrow="Admin" title="Etiquetas y comunicados" />
      <Text style={styles.s}>Etiquetas</Text>
      {(Array.isArray(etiquetas) ? etiquetas : []).map((e) => (
        <AppCard key={e.id ?? e.nombre}>
          <Text style={styles.t}>{e.nombre}</Text>
          <TouchableOpacity onPress={async () => { await adminService.eliminarEtiqueta(e.id as number); load(); }}>
            <Text style={styles.del}>Eliminar</Text>
          </TouchableOpacity>
        </AppCard>
      ))}
      {etiquetas.length === 0 ? <Empty text="Sin etiquetas." /> : null}
      <AppInput label="Nueva etiqueta" value={nombre} onChangeText={setNombre} />
      <AppButton title="Crear etiqueta" onPress={async () => { await adminService.crearEtiqueta(nombre); setNombre(''); load(); }} />
      <Text style={styles.s}>Nuevo comunicado</Text>
      <AppInput label="Título" value={titulo} onChangeText={setTitulo} />
      <AppInput label="Contenido" value={contenido} onChangeText={setContenido} multiline />
      <AppButton title="Crear comunicado" onPress={async () => { await adminService.crearComunicado({ titulo, contenido }); setTitulo(''); setContenido(''); load(); }} />
      <Note>Publica al crear, fecha auto. No se edita ni programa.</Note>
      {coms.map((c) => (
        <AppCard key={c.id}>
          <Text style={styles.t}>{c.titulo}</Text>
          <TouchableOpacity onPress={async () => { await adminService.eliminarComunicado(c.id); load(); }}>
            <Text style={styles.del}>Eliminar comunicado</Text>
          </TouchableOpacity>
        </AppCard>
      ))}
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  s: { color: '#fff', fontWeight: '700', marginTop: 14 },
  t: { color: '#fff', fontWeight: '700' },
  del: { color: colors.danger, marginTop: 6 },
});
