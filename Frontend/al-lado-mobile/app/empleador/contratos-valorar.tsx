import React, { useState } from 'react';
import { Text, ScrollView, StyleSheet, TouchableOpacity, View } from 'react-native';
import { router, useLocalSearchParams } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton, Note } from '../../src/components/ui';
import { comunidadService, postulacionService, resolveIdByEmail } from '../../src/api/services';
import { errMsg, param } from '../../src/api/client';
import { useAuth } from '../../src/auth/AuthContext';
import { colors } from '../../src/theme';

export default function Valorar() {
  const { postulacion } = useLocalSearchParams();
  const { email, userId } = useAuth();
  const [stars, setStars] = useState(5);
  const [comentario, setComentario] = useState('');
  const [msg, setMsg] = useState('');
  const [error, setError] = useState('');

  const onSubmit = async () => {
    setError(''); setMsg('');
    try {
      const autorId = userId ?? (await resolveIdByEmail(email, 'EMPLEADOR'));
      // El trabajador es el autor de la postulación
      const p = await postulacionService.detalle(param(postulacion));
      await comunidadService.valorar({
        puntuacion: stars, comentario,
        autor: { id: autorId },
        trabajador: { id: p.autor?.id ?? p.trabajador?.id },
      });
      setMsg('Valoración publicada.');
      setTimeout(() => router.back(), 700);
    } catch (e) { setError(errMsg(e)); }
  };

  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="¿Cómo fue el trabajo?" />
      <View style={styles.stars}>
        {[1, 2, 3, 4, 5].map((s) => (
          <TouchableOpacity key={s} onPress={() => setStars(s)}>
            <Text style={[styles.star, s <= stars && styles.starOn]}>★</Text>
          </TouchableOpacity>
        ))}
      </View>
      <AppInput label="Comentario (opcional)" value={comentario} onChangeText={setComentario} multiline />
      {error ? <Text style={styles.e}>{error}</Text> : null}
      {msg ? <Text style={styles.ok}>{msg}</Text> : null}
      <AppButton title="Publicar valoración" onPress={onSubmit} />
      <Note>Una sola valoración por profesional, no editable.</Note>
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({
  stars: { flexDirection: 'row', gap: 4, marginVertical: 10 },
  star: { fontSize: 36, color: colors.border }, starOn: { color: colors.yellow },
  e: { color: colors.danger }, ok: { color: colors.success },
});
