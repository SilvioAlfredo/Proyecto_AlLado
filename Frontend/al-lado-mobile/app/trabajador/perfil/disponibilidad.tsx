import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppInput, AppButton, Note, Empty } from '../../../src/components/ui';
import { perfilService, resolveIdByEmail } from '../../../src/api/services';
import { errMsg } from '../../../src/api/client';
import type { Horario } from '../../../src/types';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

const DIAS = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'];

export default function Disponibilidad() {
  const { email, userId } = useAuth();
  const [tid, setTid] = useState<number | null>(userId);
  const [items, setItems] = useState<Horario[]>([]);
  const [dia, setDia] = useState('LUNES');
  const [ini, setIni] = useState('09:00');
  const [fin, setFin] = useState('13:00');
  const [error, setError] = useState('');

  const load = async (id: number): Promise<void> => {
    const list = await perfilService.horarios(id).catch(() => []);
    setItems(Array.isArray(list) ? list : []);
  };

  useFocusEffect(useCallback(() => {
    (async () => {
      const id = userId ?? (await resolveIdByEmail(email, 'TRABAJADOR'));
      setTid(id);
      if (id) await load(id);
    })();
  }, [email, userId]));

  const agregar = async () => {
    setError('');
    if (tid == null) { setError('Sin sesión.'); return; }
    try {
      await perfilService.agregarHorario(tid, { diaSemana: dia, horaInicio: ini, horaFin: fin });
      await load(tid);
    } catch (e) { setError(errMsg(e)); }
  };

  return (
    <Screen>
      <Brand />
      <Title title="Disponibilidad semanal" subtitle="Se repite cada semana, no son reservas." />
      <FlatList
        data={items} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Sin franjas cargadas." />}
        renderItem={({ item }) => (
          <AppCard>
            <View style={styles.row}>
              <Text style={styles.t}>{item.diaSemana} {item.horaInicio}–{item.horaFin}</Text>
              <TouchableOpacity onPress={async () => { await perfilService.eliminarHorario(item.id); if (tid != null) load(tid); }}>
                <Text style={styles.del}>Eliminar</Text>
              </TouchableOpacity>
            </View>
          </AppCard>
        )}
      />
      <Text style={styles.label}>Día: {dia}</Text>
      <View style={styles.chips}>
        {DIAS.map((d) => (
          <TouchableOpacity key={d} onPress={() => setDia(d)} style={[styles.chip, dia === d && styles.active]}>
            <Text style={dia === d ? styles.ta : styles.t2}>{d.slice(0, 3)}</Text>
          </TouchableOpacity>
        ))}
      </View>
      <AppInput label="Inicio (HH:MM)" value={ini} onChangeText={setIni} />
      <AppInput label="Fin (HH:MM)" value={fin} onChangeText={setFin} />
      {error ? <Text style={styles.e}>{error}</Text> : null}
      <AppButton title="Agregar franja" onPress={agregar} />
      <Note>Sin solapamientos el mismo día, inicio menor que fin.</Note>
    </Screen>
  );
}
const styles = StyleSheet.create({
  row: { flexDirection: 'row', justifyContent: 'space-between' },
  t: { color: '#fff', fontWeight: '600' }, del: { color: colors.danger },
  label: { color: '#fff', fontWeight: '700', marginTop: 10 },
  chips: { flexDirection: 'row', flexWrap: 'wrap', gap: 6, marginVertical: 8 },
  chip: { borderWidth: 1, borderColor: colors.border, borderRadius: 8, padding: 6 },
  active: { backgroundColor: colors.yellow },
  t2: { color: colors.muted, fontSize: 11 }, ta: { color: '#000', fontSize: 11, fontWeight: '700' },
  e: { color: colors.danger },
});
