'use client';

import { useEffect, type ReactNode } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '../hooks/useAuth';

interface AuthGuardProps {
  children: ReactNode;
  allowedRoles?: string[];
  fallbackUrl?: string;
}

export function AuthGuard({
  children,
  allowedRoles,
  fallbackUrl = '/login',
}: AuthGuardProps) {
  const { user, loading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (loading) return;

    if (!user) {
      router.replace(fallbackUrl);
      return;
    }

    if (
      allowedRoles &&
      allowedRoles.length > 0 &&
      !allowedRoles.some((role) => user.roles.includes(role))
    ) {
      router.replace('/');
    }
  }, [user, loading, allowedRoles, fallbackUrl, router]);

  if (loading) {
    return (
      <div className="flex h-screen w-full items-center justify-center bg-slate-50">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-amber-500 border-t-transparent" />
      </div>
    );
  }

  if (!user) return null;

  if (
    allowedRoles &&
    allowedRoles.length > 0 &&
    !allowedRoles.some((role) => user.roles.includes(role))
  ) {
    return null;
  }

  return <>{children}</>;
}
