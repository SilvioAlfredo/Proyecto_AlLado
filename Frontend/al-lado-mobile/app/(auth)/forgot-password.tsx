import React, { useState } from 'react';
import { Text, ScrollView, StyleSheet } from 'react-native';
import { Screen, Brand, Title, AppInput, AppButton, Note } from '../../src/components/ui';
import { authService } from '../../src/api/services';
import { errMsg } from '../../src/api/client';
import { colors } from '../../src/theme';

export default function ForgotPassword() {
  const [email, setEmail] = useState('');
  const [msg, setMsg] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const onSubmit = async () => {
    setError('');
    setMsg('');
    setLoading(true);
    try {
      await authService.forgot(email.trim());
      setMsg('Si el email existe, te enviamos instrucciones.');
    } catch (e) {
      setError(errMsg(e));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Screen>
      <ScrollView>
        <Brand />
        <Title title="Recuperar contraseña" subtitle="Te enviamos un token de 24 h." />
        <AppInput label="Email" value={email} onChangeText={setEmail} autoCapitalize="none" keyboardType="email-address" />
        {error ? <Text style={styles.error}>{error}</Text> : null}
        {msg ? <Text style={styles.ok}>{msg}</Text> : null}
        <AppButton title="Enviar instrucciones" onPress={onSubmit} loading={loading} />
        <Note>El token se usa en POST /auth/password/reset.</Note>
      </ScrollView>
    </Screen>
  );
}

const styles = StyleSheet.create({
  error: { color: colors.danger, marginVertical: 8 },
  ok: { color: colors.success, marginVertical: 8 },
});
