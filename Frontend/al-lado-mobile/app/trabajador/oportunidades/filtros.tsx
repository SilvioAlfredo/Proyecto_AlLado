import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView, StyleSheet, TouchableOpacity } from 'react-native';
import { router } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton, Note } from '../../../src/components/ui';
import { etiquetasService, oportunidadService } from '../../../src/api/services';
import type { Etiqueta } from '../../../src/types';
import type { Oportunidad } from '../../../src/types';
import { colors } from '../../../src/theme';

const MODALIDADES = ['PRESENCIAL', 'REMOTO', 'HIBRIDO'];

export default function Filtros() {
  const [texto, setTexto] = useState('');
  const [modalidad, setModalidad] = useState<string | null>(null);
  const [etiquetas, setEtiquetas] = useState<Etiqueta[]>([]);
  const [selected, setSelected] = useState<string[]>([]);
  const [result, setResult] = useState<Oportunidad[] | null>(null);

  useEffect(() => {
    etiquetasService.listar().then((d) => setEtiquetas(Array.isArray(d) ? d : [])).catch(() => {});
  }, []);

  const toggle = (name: string) =>
    setSelected((s) => (s.includes(name) ? s.filter((x) => x !== name) : [...s, name]));

  const aplicar = async () => {
    const data = await oportunidadService.filtrar({
      filtro: texto || undefined,
      modalidad: modalidad || undefined,
      etiquetas: selected,
    });
    setResult(Array.isArray(data) ? data : []);
  };

  return (
    <Screen>
      <ScrollView>
        <Brand />
        <Title title="Buscá a tu manera" subtitle="Filtros de oportunidades" />
        <AppInput label="Texto" value={texto} onChangeText={setTexto} placeholder="Electricidad..." />
        <Text style={styles.label}>Modalidad</Text>
        <View style={styles.chips}>
          {MODALIDADES.map((m) => (
            <TouchableOpacity key={m} onPress={() => setModalidad(modalidad === m ? null : m)} style={[styles.chip, modalidad === m && styles.chipActive]}>
              <Text style={modalidad === m ? styles.chipTextActive : styles.chipText}>{m}</Text>
            </TouchableOpacity>
          ))}
        </View>
        <Text style={styles.label}>Etiquetas</Text>
        <View style={styles.chips}>
          {etiquetas.map((e) => (
            <TouchableOpacity key={e.id ?? e.nombre} onPress={() => toggle(e.nombre)} style={[styles.chip, selected.includes(e.nombre) && styles.chipActive]}>
              <Text style={selected.includes(e.nombre) ? styles.chipTextActive : styles.chipText}>{e.nombre}</Text>
            </TouchableOpacity>
          ))}
        </View>
        <AppButton title="Aplicar filtros" onPress={aplicar} />
        <AppButton title="Volver" variant="outline" onPress={() => router.back()} />
        {result ? (
          <View style={{ marginTop: 12 }}>
            <Text style={styles.label}>{result.length} resultados</Text>
            {result.map((o) => (
              <TouchableOpacity key={o.id} onPress={() => router.push(`/trabajador/oportunidades/${o.id}`)}>
                <Text style={styles.link}>{o.titulo}</Text>
              </TouchableOpacity>
            ))}
          </View>
        ) : null}
        <Note>La ubicación es literal (sin filtro por distancia).</Note>
      </ScrollView>
    </Screen>
  );
}

const styles = StyleSheet.create({
  label: { color: '#fff', fontWeight: '700', marginVertical: 8 },
  chips: { flexDirection: 'row', flexWrap: 'wrap', gap: 8, marginBottom: 8 },
  chip: { borderWidth: 1, borderColor: colors.border, borderRadius: 999, paddingHorizontal: 12, paddingVertical: 7 },
  chipActive: { backgroundColor: colors.yellow, borderColor: colors.yellow },
  chipText: { color: colors.muted, fontWeight: '600' },
  chipTextActive: { color: '#000', fontWeight: '700' },
  link: { color: colors.orange, marginVertical: 4 },
});
