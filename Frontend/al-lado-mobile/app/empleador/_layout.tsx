import { Tabs, Redirect } from 'expo-router';
import { useAuth } from '../../src/auth/AuthContext';
import { colors } from '../../src/theme';

export default function EmpleadorLayout() {
  const { loading, token, isEmpleador, isAdmin } = useAuth();
  if (loading) return null;
  if (!token) return <Redirect href="/(auth)/login" />;
  if (!isEmpleador && !isAdmin) return <Redirect href="/" />;
  return (
    <Tabs screenOptions={{ headerShown: false, tabBarStyle: { backgroundColor: colors.card, borderTopColor: colors.border }, tabBarActiveTintColor: colors.yellow, tabBarInactiveTintColor: colors.muted }}>
      <Tabs.Screen name="index" options={{ title: 'Inicio' }} />
      <Tabs.Screen name="solicitudes" options={{ title: 'Solicitudes' }} />
      <Tabs.Screen name="trabajadores" options={{ title: 'Profesionales' }} />
      <Tabs.Screen name="contratos" options={{ title: 'Contratos' }} />
      <Tabs.Screen name="notificaciones" options={{ title: 'Avisos' }} />
      <Tabs.Screen name="cuenta" options={{ title: 'Cuenta' }} />
    </Tabs>
  );
}
