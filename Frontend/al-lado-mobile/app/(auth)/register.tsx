import React, { useState } from 'react';
import { Text, ScrollView, StyleSheet, View, TouchableOpacity } from 'react-native';
import { Link, router } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton } from '../../src/components/ui';
import { useAuth, roleHome } from '../../src/auth/AuthContext';
import { errMsg } from '../../src/api/client';
import type { Rol } from '../../src/types';
import { colors } from '../../src/theme';

export default function Register() {
  const { register } = useAuth();
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [telefono, setTelefono] = useState('');
  const [password, setPassword] = useState('');
  const [rol, setRol] = useState<Rol>('TRABAJADOR');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const onSubmit = async () => {
    setError('');
    if (!nombre.trim() || !email.trim() || password.length < 8) {
      setError('Completá nombre, email y contraseña de mínimo 8 caracteres.');
      return;
    }
    setLoading(true);
    try {
      const res = await register({ nombre: nombre.trim(), email: email.trim(), password, telefono, rol });
      router.replace(roleHome(res.roles));
    } catch (e) {
      setError(errMsg(e));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Screen>
      <ScrollView showsVerticalScrollIndicator={false}>
        <Brand />
        <Title title="Creá tu cuenta" subtitle="Empezá por acá." />
        <View style={styles.rolRow}>
          {(['TRABAJADOR', 'EMPLEADOR'] as Rol[]).map((r) => (
            <TouchableOpacity
              key={r}
              onPress={() => setRol(r)}
              style={[styles.rol, rol === r && styles.rolActive]}
            >
              <Text style={[styles.rolText, rol === r && { color: '#000' }]}>
                {r === 'TRABAJADOR' ? 'Trabajador' : 'Empleador'}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
        <AppInput label="Nombre" value={nombre} onChangeText={setNombre} placeholder="Juan Pérez" />
        <AppInput label="Email" value={email} onChangeText={setEmail} autoCapitalize="none" keyboardType="email-address" placeholder="juan@mail.com" />
        <AppInput label="Teléfono" value={telefono} onChangeText={setTelefono} keyboardType="phone-pad" placeholder="+54 ..." />
        <AppInput label="Contraseña (mín. 8)" value={password} onChangeText={setPassword} secureTextEntry placeholder="••••••••" />
        {error ? <Text style={styles.error}>{error}</Text> : null}
        <AppButton title="Crear mi cuenta" onPress={onSubmit} loading={loading} />
        <Link href="/(auth)/login" style={styles.link}>Ya tengo cuenta</Link>
      </ScrollView>
    </Screen>
  );
}

const styles = StyleSheet.create({
  rolRow: { flexDirection: 'row', gap: 8, marginBottom: 12 },
  rol: { flex: 1, borderWidth: 1, borderColor: colors.border, borderRadius: 10, padding: 10, alignItems: 'center' },
  rolActive: { backgroundColor: colors.yellow, borderColor: colors.yellow },
  rolText: { color: '#fff', fontWeight: '700' },
  error: { color: colors.danger, marginVertical: 8 },
  link: { color: colors.yellow, fontWeight: '600', marginTop: 14, textAlign: 'center' },
});
