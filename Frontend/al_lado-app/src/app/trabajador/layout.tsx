'use client';

import React from 'react';
import Sidebar from '@/components/layout/Sidebar';
import BottomNav from '@/components/layout/BottomNav';
import { AuthGuard } from '@/features/auth/components/AuthGuard';

export default function TrabajadorLayout({ children }: { children: React.ReactNode }) {
  return (
    <AuthGuard allowedRoles={['ROLE_TRABAJADOR', 'TRABAJADOR']}>
      <div className="flex min-h-[calc(100vh-5rem)] gap-6 bg-allado-bg text-zinc-100">
        <Sidebar />
        <div className="min-w-0 flex-1 pb-20 md:pb-6">{children}</div>
        <BottomNav />
      </div>
    </AuthGuard>
  );
}
