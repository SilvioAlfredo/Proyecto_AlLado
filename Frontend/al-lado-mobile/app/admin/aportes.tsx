import React, { useCallback, useState } from 'react';
import { Text, FlatList, StyleSheet } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppCard, AppInput, AppButton, Empty, Note } from '../../src/components/ui';
import { adminService } from '../../src/api/services';
import type { Donacion } from '../../src/types';
import { colors } from '../../src/theme';

export default function Aportes() {
  const [items, setItems] = useState<Donacion[]>([]);
  const [asunto, setAsunto] = useState('');
  const [monto, setMonto] = useState('');
  const [sel, setSel] = useState<number | null>(null);
  const [msg, setMsg] = useState('');
  const load = async () => {
    const d = await adminService.donaciones().catch(() => [] as Donacion[]);
    setItems(Array.isArray(d) ? d : (d as unknown as { content?: Donacion[] })?.content ?? (d as Donacion[]));
  };
  useFocusEffect(useCallback(() => { load(); }, []));

  const verificar = async () => {
    setMsg('');
    if (sel == null) return;
    await adminService.verificarDonacion(sel, asunto, Number(monto));
    setMsg('Verificada. Si es SUSCRIPCION, activa PRO 1 año.');
    setSel(null); setAsunto(''); setMonto('');
    load();
  };

  return (
    <Screen>
      <Brand admin />
      <Title eyebrow="Admin" title="Transferencias y donaciones" />
      <FlatList data={items} keyExtractor={(i) => String(i.id)}
        ListEmptyComponent={<Empty text="Sin transferencias." />}
        renderItem={({ item }) => (
          <AppCard>
            <Text style={styles.t}>${item.monto} · {item.estado}</Text>
            <Text style={styles.m}>{item.asunto ?? ''} · {item.usuario?.email ?? ''}</Text>
            <AppButton title={sel === item.id ? 'Seleccionada ✓' : 'Verificar'} variant="outline" onPress={() => setSel(item.id)} />
          </AppCard>
        )} />
      {sel ? (
        <>
          <AppInput label="Asunto observado (idéntico)" value={asunto} onChangeText={setAsunto} autoCapitalize="characters" />
          <AppInput label="Monto observado" value={monto} onChangeText={setMonto} keyboardType="numeric" />
          <AppButton title="Verificar transferencia" onPress={verificar} />
        </>
      ) : null}
      {msg ? <Text style={styles.ok}>{msg}</Text> : null}
      <Note>Asunto idéntico (mayúsculas/espacios) y monto numérico. Si no coincide, queda pendiente.</Note>
    </Screen>
  );
}
const styles = StyleSheet.create({
  t: { color: '#fff', fontWeight: '700' }, m: { color: colors.muted, fontSize: 12 },
  ok: { color: colors.success, marginTop: 8 },
});
