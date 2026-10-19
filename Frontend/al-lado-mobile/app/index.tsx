import { useEffect } from 'react';
import { ActivityIndicator, View } from 'react-native';
import { Redirect } from 'expo-router';
import { useAuth, roleHome } from '../src/auth/AuthContext';
import { colors } from '../src/theme';

export default function Index() {
  const { loading, token, roles } = useAuth();
  if (loading) {
    return (
      <View style={{ flex: 1, backgroundColor: colors.bg, justifyContent: 'center' }}>
        <ActivityIndicator color={colors.yellow} />
      </View>
    );
  }
  if (!token) return <Redirect href="/(auth)/login" />;
  return <Redirect href={roleHome(roles)} />;
}
