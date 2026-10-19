'use client';

import { useAuth } from '@/features/auth/hooks/useAuth';
import MemberHome from '@/features/home/components/MemberHome';
import { PublicHome } from '@/features/home/components/PublicHome';

export default function HomePage() {
  const { user } = useAuth();
  return user ? <MemberHome /> : <PublicHome />;
}
