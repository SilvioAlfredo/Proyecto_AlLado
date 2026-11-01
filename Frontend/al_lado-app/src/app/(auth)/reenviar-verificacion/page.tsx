'use client';

import { useState } from 'react';
import Link from 'next/link';
import { authService } from '@/features/auth/services/authService';
import { Button } from '@/components/ui/button';
import { Feedback } from '@/components/ui/Feedback';

export default function ReenviarVerificacionPage() {
  const [estado, setEstado] = useState<'idle' | 'cargando' | 'ok' | 'error'>('idle');
  const [error, setError] = useState<string | null>(null);

  const reenviar = async () => {
    setEstado('cargando');
    setError(null);
    try {
      await authService.reenviarVerificacionEmail();
      setEstado('ok');
    } catch (err: unknown) {
      setEstado('error');
      setError(
        err instanceof Error ? err.message : 'No se pudo reenviar el enlace de verificación.'
      );
    }
  };

  return (
    <div className="flex min-h-[calc(100vh-4rem)] items-center justify-center py-12">
      <div className="w-full max-w-md space-y-6">
        <div className="mb-6 flex items-center justify-center gap-2">
          <span className="flex h-10 w-10 items-center justify-center rounded-full bg-[#ffc107] font-black text-xl text-black">
            ◉
          </span>
          <span className="text-2xl font-extrabold text-white">
            Al<span className="text-[#ffc107]">L</span>ado
          </span>
        </div>

        <h1 className="text-2xl font-extrabold text-white text-center">
          Reenviar verificación de email
        </h1>

        {estado === 'cargando' && (
          <p className="text-zinc-400 text-center">Enviando enlace de verificación...</p>
        )}

        {estado === 'ok' && (
          <Feedback variant="success">
            Te enviamos un enlace de verificación a tu email. Revisá tu bandeja de entrada.
          </Feedback>
        )}

        {estado === 'error' && error && (
          <Feedback variant="error">{error}</Feedback>
        )}

        <Button type="button" onClick={reenviar} disabled={estado === 'cargando'} className="w-full">
          {estado === 'cargando' ? 'Enviando...' : 'Enviar enlace de verificación'}
        </Button>

        <p className="text-sm text-zinc-500 text-center">
          ¿Ya tenés el enlace?{' '}
          <Link href="/login" className="font-medium text-[#ffc107] hover:underline">
            Iniciá sesión
          </Link>
        </p>
      </div>
    </div>
  );
}
