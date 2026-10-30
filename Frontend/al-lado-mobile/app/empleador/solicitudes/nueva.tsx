import React, { useEffect, useState } from 'react';
import { Text, ScrollView, StyleSheet, TouchableOpacity, View } from 'react-native';
import { router } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton, Note } from '../../../src/components/ui';
import { oportunidadService, etiquetasService } from '../../../src/api/services';
import { errMsg } from '../../../src/api/client';
import type { Etiqueta } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function NuevaSolicitud() {
  const { userId } = useAuth();
  const [titulo, setTitulo] = useState('');
  const [contenido, setContenido] = useState('');
  const [ubicacion, setUbicacion] = useState('');
  const [modalidad, setModalidad] = useState('PRESENCIAL');
  const [rango, setRango] = useState('');
  const [etiquetas, setEtiquetas] = useState<Etiqueta[]>([]);
  const [sel, setSel] = useState<string[]>([]);
  const [error, setError] = useState('');

  useEffect(() => {
    etiquetasService.listar().then((d) => setEtiquetas(Array.isArray(d) ? d : [])).catch(() => {});
  }, []);

  const onSubmit = async () => {
    setError('');
    try {
      if (userId == null) throw new Error('No hay sesión activa.');
      await oportunidadService.crear({
        titulo: titulo.trim(), contenido: contenido.trim(),
        ubicacion, modalidad, rangoSalarial: rango,
        activa: true, autor: { id: userId },
        etiquetas: sel.map((nombre) => ({ nombre })),
      });
      router.back();
    } catch (e) { setError(errMsg(e)); }
  };

  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="Contanos qué necesitás" />
      <AppInput label="Título *" value={titulo} onChangeText={setTitulo} maxLength={150} />
      <AppInput label="Descripción *" value={contenido} onChangeText={setContenido} multiline numberOfLines={4} style={{ minHeight: 90, textAlignVertical: 'top' }} />
      <AppInput label="Ubicación" value={ubicacion} onChangeText={setUbicacion} />
      <Text style={styles.l}>Modalidad</Text>
      <View style={styles.row}>
        {['PRESENCIAL', 'REMOTO', 'HIBRIDO'].map((m) => (
          <TouchableOpacity key={m} onPress={() => setModalidad(m)} style={[styles.chip, modalidad === m && styles.a]}>
            <Text style={modalidad === m ? styles.ta : styles.t2}>{m}</Text>
          </TouchableOpacity>
        ))}
      </View>
      <AppInput label="Rango remuneración" value={rango} onChangeText={setRango} />
      <Text style={styles.l}>Especialidades</Text>
      <View style={styles.row}>
        {etiquetas.map((e) => (
          <TouchableOpacity key={e.id ?? e.nombre} onPress={() => setSel((s) => s.includes(e.nombre) ? s.filter((x) => x !== e.nombre) : [...s, e.nombre])} style={[styles.chip, sel.includes(e.nombre) && styles.a]}>
            <Text style={sel.includes(e.nombre) ? styles.ta : styles.t2}>{e.nombre}</Text>
          </TouchableOpacity>
        ))}
      </View>
      {error ? <Text style={styles.e}>{error}</Text> : null}
      <AppButton title="Publicar solicitud" onPress={onSubmit} />
      <Note>Queda activa y recibe postulaciones. Plan Básico: máx 3/mes.</Note>
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  l: { color: '#fff', fontWeight: '700', marginVertical: 6 },
  row: { flexDirection: 'row', flexWrap: 'wrap', gap: 6, marginBottom: 8 },
  chip: { borderWidth: 1, borderColor: colors.border, borderRadius: 999, paddingHorizontal: 10, paddingVertical: 6 },
  a: { backgroundColor: colors.yellow, borderColor: colors.yellow },
  t2: { color: colors.muted, fontSize: 12 }, ta: { color: '#000', fontSize: 12, fontWeight: '700' },
  e: { color: colors.danger },
});
