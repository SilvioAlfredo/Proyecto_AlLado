'use client';

import { Suspense, useEffect, useState } from 'react';
import Link from 'next/link';
import { useSearchParams } from 'next/navigation';
import { authService } from '@/features/auth/services/authService';

function VerifyEmailContent() {
  const searchParams = useSearchParams();
  const token = searchParams.get('token') ?? '';
  const [estado, setEstado] = useState<'cargando' | 'ok' | 'error'>('cargando');

  useEffect(() => {
    if (!token) {
      setEstado('error');
      return;
    }
    // Backend: GET /auth/verify-email?token=
    authService
      .verifyEmail(token)
      .then(() => setEstado('ok'))
      .catch(() => setEstado('error'));
  }, [token]);

  return (
    <div className="w-full max-w-md text-center">
      <div className="mb-6 flex items-center justify-center gap-2">
        <span className="flex h-10 w-10 items-center justify-center rounded-full bg-[#ffc107] font-black text-xl text-black">
          ◉
        </span>
        <span className="text-2xl font-extrabold text-white">
          Al<span className="text-[#ffc107]">L</span>ado
        </span>
      </div>
      {estado === 'cargando' && <p className="text-zinc-400">Verificando tu email...</p>}
      {estado === 'ok' && (
        <div className="rounded-xl border border-emerald-500/30 bg-emerald-500/10 p-4">
          <p className="font-medium text-emerald-400">¡Email verificado! Ya podés usar tu cuenta.</p>
          <Link href="/login" className="mt-4 inline-block font-medium text-[#ffc107] hover:underline">
            Iniciar sesión
          </Link>
        </div>
      )}
      {estado === 'error' && (
        <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4">
          <p className="font-medium text-red-400">El enlace es inválido o expiró.</p>
          <Link href="/login" className="mt-4 inline-block font-medium text-[#ffc107] hover:underline">
            Volver al inicio de sesión
          </Link>
        </div>
      )}
    </div>
  );
}

export default function VerifyEmailPage() {
  return (
    <div className="flex min-h-[calc(100vh-4rem)] items-center justify-center py-12">
      <Suspense fallback={<p className="text-zinc-500">Cargando...</p>}>
        <VerifyEmailContent />
      </Suspense>
    </div>
  );
}
