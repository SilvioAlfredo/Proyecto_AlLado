import React from 'react';
import type { ReactNode } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  ActivityIndicator,
  type TextInputProps,
  type ViewStyle,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import type { SafeAreaViewProps } from 'react-native-safe-area-context';
import type { ViewProps } from 'react-native';
import { colors, radius } from '../theme';

export function Screen({
  children,
  style,
}: {
  children: ReactNode;
  style?: ViewStyle;
}): React.JSX.Element {
  return (
    <SafeAreaView
      style={[styles.screen, style] as SafeAreaViewProps['style']}
      edges={['top', 'bottom']}
    >
      {children}
    </SafeAreaView>
  );
}

export function Brand({ admin }: { admin?: boolean }): React.JSX.Element {
  return (
    <View style={styles.brandRow}>
      <Text style={styles.brand}>
        AlLado<Text style={{ color: colors.yellow }}>.</Text>
      </Text>
      {admin ? <Text style={styles.brandRole}>ADMIN</Text> : null}
    </View>
  );
}

export function Title({
  eyebrow,
  title,
  subtitle,
}: {
  eyebrow?: string;
  title: string;
  subtitle?: string;
}): React.JSX.Element {
  return (
    <View style={{ marginBottom: 14 }}>
      {eyebrow ? <Text style={styles.eyebrow}>{eyebrow}</Text> : null}
      <Text style={styles.title}>{title}</Text>
      {subtitle ? <Text style={styles.subtitle}>{subtitle}</Text> : null}
    </View>
  );
}

export function AppButton({
  title,
  onPress,
  variant = 'primary',
  disabled,
  loading,
}: {
  title: string;
  onPress: () => void | Promise<void>;
  variant?: 'primary' | 'outline' | 'danger';
  disabled?: boolean;
  loading?: boolean;
}): React.JSX.Element {
  return (
    <TouchableOpacity
      style={[
        styles.btn,
        variant === 'outline' && styles.btnOutline,
        variant === 'danger' && styles.btnDanger,
        disabled && { opacity: 0.5 },
      ]}
      onPress={onPress}
      disabled={disabled || loading}
    >
      {loading ? (
        <ActivityIndicator color={variant === 'outline' ? colors.yellow : '#000'} />
      ) : (
        <Text style={[styles.btnText, variant === 'outline' && { color: colors.yellow }]}>
          {title}
        </Text>
      )}
    </TouchableOpacity>
  );
}

export function AppInput({
  label,
  ...props
}: TextInputProps & { label?: string }): React.JSX.Element {
  return (
    <View style={{ marginBottom: 10 }}>
      {label ? <Text style={styles.label}>{label}</Text> : null}
      <TextInput style={styles.input} placeholderTextColor={colors.muted} {...props} />
    </View>
  );
}

export function AppCard({
  children,
  cream,
  style,
}: {
  children: ReactNode;
  cream?: boolean;
  style?: ViewStyle;
}): React.JSX.Element {
  return (
    <View style={[styles.card, cream && styles.cardCream, style] as ViewProps['style']}>
      {children}
    </View>
  );
}

export function Badge({
  children,
  active,
}: {
  children: ReactNode;
  active?: boolean;
}): React.JSX.Element {
  return (
    <View style={[styles.badge, active && styles.badgeActive]}>
      <Text style={[styles.badgeText, active && { color: '#000' }]}>{children}</Text>
    </View>
  );
}

export function Note({ children }: { children: ReactNode }): React.JSX.Element {
  return (
    <View style={styles.note}>
      <Text style={styles.noteText}>{children}</Text>
    </View>
  );
}

export function Empty({ text }: { text: string }): React.JSX.Element {
  return <Text style={styles.empty}>{text}</Text>;
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.bg, paddingHorizontal: 16, paddingTop: 8 },
  brandRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  brand: { fontSize: 22, fontWeight: '800', color: '#fff' },
  brandRole: { fontSize: 11, fontWeight: '700', color: colors.orange, letterSpacing: 2 },
  eyebrow: {
    fontSize: 11,
    fontWeight: '700',
    color: colors.orange,
    letterSpacing: 1.5,
    textTransform: 'uppercase',
    marginBottom: 4,
  },
  title: { fontSize: 24, fontWeight: '800', color: '#fff' },
  subtitle: { fontSize: 14, color: colors.muted, marginTop: 4 },
  btn: {
    backgroundColor: colors.yellow,
    borderRadius: radius.md,
    paddingVertical: 13,
    alignItems: 'center',
    marginTop: 8,
  },
  btnOutline: { backgroundColor: 'transparent', borderWidth: 1, borderColor: colors.border },
  btnDanger: { backgroundColor: colors.dangerBg, borderWidth: 1, borderColor: colors.dangerBorder },
  btnText: { color: '#000', fontWeight: '700', fontSize: 15 },
  label: { fontSize: 14, fontWeight: '600', color: '#fff', marginBottom: 6 },
  input: {
    backgroundColor: colors.card2,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radius.sm,
    paddingHorizontal: 12,
    paddingVertical: 11,
    color: '#fff',
    fontSize: 15,
  },
  card: {
    backgroundColor: colors.card,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radius.lg,
    padding: 14,
    marginBottom: 10,
  },
  cardCream: { backgroundColor: colors.cream, borderColor: colors.cream },
  badge: {
    backgroundColor: colors.card2,
    borderRadius: radius.pill,
    paddingHorizontal: 10,
    paddingVertical: 4,
    alignSelf: 'flex-start',
  },
  badgeActive: { backgroundColor: colors.yellow },
  badgeText: { fontSize: 11, fontWeight: '700', color: colors.muted, textTransform: 'uppercase' },
  note: {
    backgroundColor: colors.infoBg,
    borderWidth: 1,
    borderColor: colors.infoBorder,
    borderRadius: radius.sm,
    padding: 10,
    marginVertical: 10,
  },
  noteText: { color: colors.infoText, fontSize: 12 },
  empty: { color: colors.muted, textAlign: 'center', marginTop: 20, fontSize: 13 },
});
