import { Tabs, Redirect } from 'expo-router';
import { useAuth } from '../../src/auth/AuthContext';
import { colors } from '../../src/theme';

export default function AdminLayout() {
  const { loading, token, isAdmin } = useAuth();
  if (loading) return null;
  if (!token) return <Redirect href="/(auth)/login" />;
  if (!isAdmin) return <Redirect href="/" />;
  return (
    <Tabs screenOptions={{ headerShown: false, tabBarStyle: { backgroundColor: colors.card, borderTopColor: colors.border }, tabBarActiveTintColor: colors.yellow, tabBarInactiveTintColor: colors.muted }}>
      <Tabs.Screen name="dashboard" options={{ title: 'Inicio' }} />
      <Tabs.Screen name="personas" options={{ title: 'Personas' }} />
      <Tabs.Screen name="moderacion" options={{ title: 'Moderación' }} />
      <Tabs.Screen name="aportes" options={{ title: 'Aportes' }} />
      <Tabs.Screen name="gestion" options={{ title: 'Gestión' }} />
    </Tabs>
  );
}
