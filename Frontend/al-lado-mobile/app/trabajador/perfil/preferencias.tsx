import React, { useCallback, useState } from 'react';
import { Text, ScrollView, StyleSheet, Switch, View } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppButton, Note } from '../../../src/components/ui';
import { perfilService } from '../../../src/api/services';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Preferencias() {
  const { userId } = useAuth();
  const [tid, setTid] = useState<number | null>(userId);
  const [pref, setPref] = useState<{ email: boolean; push: boolean; nuevasOfertas: boolean; nuevasPostulaciones: boolean }>({ email: true, push: false, nuevasOfertas: true, nuevasPostulaciones: false });

  useFocusEffect(useCallback(() => {
    (async () => {
      const id = userId;
      setTid(id);
      if (!id) return;
      const p = await perfilService.preferencias(id).catch(() => null);
      if (p) setPref({ ...pref, ...(p as Partial<typeof pref>) });
    })();
  }, [userId]));

  const row = (k: keyof typeof pref, label: string) => (
    <View style={styles.row}>
      <Text style={styles.t}>{label}</Text>
      <Switch value={!!pref[k]} onValueChange={(v) => setPref((s) => ({ ...s, [k]: v }))} trackColor={{ true: colors.yellow }} />
    </View>
  );

  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="Ajustes y preferencias" />
      {row('email', 'Email')}
      {row('push', 'Push')}
      {row('nuevasOfertas', 'Nuevas ofertas')}
      {row('nuevasPostulaciones', 'Nuevas postulaciones')}
      <AppButton title="Guardar preferencias" onPress={() => { if (tid != null) perfilService.guardarPreferencias(tid, pref); }} />
      <Note>Email/push aún no implementado en backend. Queda en perfil.</Note>
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: colors.border },
  t: { color: '#fff' },
});
