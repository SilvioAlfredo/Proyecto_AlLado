import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { AuthProvider } from '../src/auth/AuthContext';
import { NoLeidasProvider } from '../src/notificaciones/NoLeidasContext';

export default function RootLayout() {
  return (
    <AuthProvider>
      <NoLeidasProvider>
        <StatusBar style="light" />
        <Stack screenOptions={{ headerShown: false, contentStyle: { backgroundColor: '#0b0b0c' } }}>
          <Stack.Screen name="index" />
          <Stack.Screen name="(auth)" />
          <Stack.Screen name="trabajador" />
          <Stack.Screen name="empleador" />
          <Stack.Screen name="admin" />
        </Stack>
      </NoLeidasProvider>
    </AuthProvider>
  );
}
