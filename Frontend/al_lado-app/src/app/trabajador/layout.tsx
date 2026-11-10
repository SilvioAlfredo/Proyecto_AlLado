
'use client';

import React from 'react';
import Sidebar from '@/components/layout/Sidebar';
import BottomNav from '@/components/layout/BottomNav';
import { AuthGuard } from '@/features/auth/components/AuthGuard';

export default function TrabajadorLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <AuthGuard allowedRoles={['ROLE_TRABAJADOR', 'TRABAJADOR']}>
      <div className="flex min-h-[calc(100vh-5rem)] flex-col gap-6 bg-allado-bg text-zinc-100 md:flex-row">
        <Sidebar />

        <div className="min-w-0 w-full flex-1 pb-20 md:pb-6">
          {children}
        </div>

        <BottomNav />
      </div>
    </AuthGuard>
  );
}
