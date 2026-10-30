import { Tabs, Redirect } from 'expo-router';
import { useAuth } from '../../src/auth/AuthContext';
import { etiquetaBadge, useNoLeidas } from '../../src/notificaciones/NoLeidasContext';
import { colors } from '../../src/theme';

export default function TrabajadorLayout() {
  const { loading, token, isTrabajador, isAdmin } = useAuth();
  const { noLeidas } = useNoLeidas();
  if (loading) return null;
  if (!token) return <Redirect href="/(auth)/login" />;
  if (!isTrabajador && !isAdmin) return <Redirect href="/" />;
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarStyle: { backgroundColor: colors.card, borderTopColor: colors.border },
        tabBarActiveTintColor: colors.yellow,
        tabBarInactiveTintColor: colors.muted,
      }}
    >
      <Tabs.Screen name="oportunidades" options={{ title: 'Oportunidades' }} />
      <Tabs.Screen name="postulaciones" options={{ title: 'Postulaciones' }} />
      <Tabs.Screen
        name="notificaciones"
        options={{
          title: 'Avisos',
          tabBarBadge: etiquetaBadge(noLeidas),
          tabBarBadgeStyle: { backgroundColor: colors.yellow, color: colors.bg },
        }}
      />
      <Tabs.Screen name="comunidad" options={{ title: 'Comunidad' }} />
      <Tabs.Screen name="perfil" options={{ title: 'Perfil' }} />
    </Tabs>
  );
}
