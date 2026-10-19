'use client';

import { useState } from 'react';
import Link from 'next/link';
import { authService } from '@/features/auth/services/authService';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [enviado, setEnviado] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      // Backend: POST /auth/password/forgot {email}
      await authService.forgotPassword(email);
      setEnviado(true);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'No se pudo enviar el email.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-md">
      <div className="text-center mb-8">
        <div className="flex items-center justify-center gap-2 mb-6">
          <span className="flex h-10 w-10 items-center justify-center rounded-full bg-[#ffc107] text-black font-black text-xl">
            ◉
          </span>
          <span className="text-2xl font-extrabold text-white">
            Al<span className="text-[#ffc107]">L</span>ado
          </span>
        </div>
        <h1 className="text-3xl font-extrabold text-white mb-2">
          Recuperar contraseña
        </h1>
        <p className="text-zinc-400">
          Ingresá tu email y te enviaremos un enlace para restablecer tu contraseña.
        </p>
      </div>

      {enviado ? (
        <div className="bg-emerald-500/10 border border-emerald-500/30 rounded-xl p-4 text-center">
          <p className="text-emerald-400 font-medium">
            ¡Email enviado! Revisá tu bandeja de entrada.
          </p>
          <Link href="/login" className="inline-block mt-4 text-[#ffc107] font-medium hover:underline">
            Volver al inicio de sesión
          </Link>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-5">
          {error && (
            <div className="p-3 rounded-lg bg-red-500/10 border border-red-500/30 text-red-400 text-sm">
              {error}
            </div>
          )}
          <div>
            <label className="block text-sm font-medium text-zinc-300 mb-2">Email</label>
            <Input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="tu@email.com"
            />
          </div>
          <Button type="submit" disabled={loading} className="w-full">
            {loading ? 'Enviando...' : 'Enviar enlace de recuperación'}
          </Button>
          <p className="text-center text-sm text-zinc-400">
            ¿Recordaste tu contraseña?{' '}
            <Link href="/login" className="text-[#ffc107] font-medium hover:underline">
              Iniciar sesión
            </Link>
          </p>
        </form>
      )}
    </div>
  );
}
