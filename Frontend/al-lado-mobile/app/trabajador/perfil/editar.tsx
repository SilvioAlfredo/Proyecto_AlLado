import React, { useEffect, useState } from 'react';
import { Text, ScrollView, StyleSheet } from 'react-native';
import { router } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton } from '../../../src/components/ui';
import { trabajadorService, resolveIdByEmail, etiquetasService } from '../../../src/api/services';
import { errMsg } from '../../../src/api/client';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function EditarPerfil() {
  const { email, userId } = useAuth();
  const [id, setId] = useState<number | null>(userId);
  const [nombre, setNombre] = useState('');
  const [telefono, setTelefono] = useState('');
  const [bio, setBio] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      const rid = userId ?? (await resolveIdByEmail(email, 'TRABAJADOR'));
      setId(rid);
      if (!rid) return;
      const p = await trabajadorService.detalle(rid).catch(() => null);
      if (p) { setNombre(p.nombre ?? ''); setTelefono(p.telefono ?? ''); setBio(p.biografia ?? ''); }
    })();
  }, []);

  const onSave = async () => {
    setError('');
    if (id == null) { setError('Sin sesión.'); return; }
    try {
      await trabajadorService.actualizar(id, { nombre, telefono, biografia: bio });
      router.back();
    } catch (e) { setError(errMsg(e)); }
  };

  return (
    <Screen><ScrollView>
      <Brand />
      <Title title="Editar perfil" />
      <AppInput label="Nombre" value={nombre} onChangeText={setNombre} />
      <AppInput label="Teléfono" value={telefono} onChangeText={setTelefono} />
      <AppInput label="Biografía" value={bio} onChangeText={setBio} multiline numberOfLines={4} style={{ minHeight: 90, textAlignVertical: 'top' }} />
      {error ? <Text style={styles.e}>{error}</Text> : null}
      <AppButton title="Guardar cambios" onPress={onSave} />
    </ScrollView></Screen>
  );
}
const styles = StyleSheet.create({ e: { color: colors.danger } });
