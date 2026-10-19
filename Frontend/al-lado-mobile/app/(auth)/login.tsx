import React, { useState } from 'react';
import { View, Text, ScrollView, StyleSheet } from 'react-native';
import { Link, router } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton, Note } from '../../src/components/ui';
import { useAuth, roleHome } from '../../src/auth/AuthContext';
import { errMsg } from '../../src/api/client';
import { colors } from '../../src/theme';

export default function Login() {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const onSubmit = async () => {
    setError('');
    setLoading(true);
    try {
      const res = await login(email.trim(), password);
      router.replace(roleHome(res.roles));
    } catch (e) {
      const s = (e as { status?: number }).status;
      setError(s === 401 ? 'Revisá tu email y contraseña.' : errMsg(e));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Screen>
      <ScrollView showsVerticalScrollIndicator={false}>
        <Brand />
        <Title eyebrow="Bienvenido" title="Tu oficio. Tu barrio." subtitle="Hacemos barrio." />
        <AppInput label="Email" value={email} onChangeText={setEmail} autoCapitalize="none" keyboardType="email-address" placeholder="juan@mail.com" />
        <AppInput label="Contraseña" value={password} onChangeText={setPassword} secureTextEntry placeholder="••••••••" />
        {error ? <Text style={styles.error}>{error}</Text> : null}
        <AppButton title="Ingresar" onPress={onSubmit} loading={loading} />
        <Note>Solo cuentas registradas. El panel ADMIN no tiene registro público.</Note>
        <View style={styles.links}>
          <Link href="/(auth)/register" style={styles.link}>Crear cuenta</Link>
          <Link href="/(auth)/forgot-password" style={styles.link}>¿Olvidaste tu contraseña?</Link>
        </View>
      </ScrollView>
    </Screen>
  );
}

const styles = StyleSheet.create({
  error: { color: colors.danger, marginVertical: 8 },
  links: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 14 },
  link: { color: colors.yellow, fontWeight: '600' },
});
