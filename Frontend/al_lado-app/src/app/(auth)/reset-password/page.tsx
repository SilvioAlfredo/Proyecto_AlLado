'use client';

import { Suspense, useState } from 'react';
import Link from 'next/link';
import { useSearchParams } from 'next/navigation';
import { authService } from '@/features/auth/services/authService';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

function ResetPasswordForm() {
  const searchParams = useSearchParams();
  const token = searchParams.get('token') ?? '';
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      alert('Las contraseñas no coinciden');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      // Backend: POST /auth/password/reset {token, newPassword} (token viene en ?token= del email)
      await authService.resetPassword(token, password);
      setSuccess(true);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'No se pudo actualizar la contraseña.');
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
          Nueva contraseña
        </h1>
        <p className="text-zinc-400">
          Ingresá tu nueva contraseña para completar la recuperación.
        </p>
      </div>

      {success ? (
        <div className="bg-emerald-500/10 border border-emerald-500/30 rounded-xl p-4 text-center">
          <p className="text-emerald-400 font-medium">
            ¡Contraseña actualizada exitosamente!
          </p>
          <Link href="/login" className="inline-block mt-4 text-[#ffc107] font-medium hover:underline">
            Iniciar sesión
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
            <label className="block text-sm font-medium text-zinc-300 mb-2">Nueva contraseña</label>
            <Input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={8}
              placeholder="Mínimo 8 caracteres"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-zinc-300 mb-2">Confirmar contraseña</label>
            <Input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
              placeholder="Repetí tu nueva contraseña"
            />
          </div>
          <Button type="submit" disabled={loading} className="w-full">
            {loading ? 'Actualizando...' : 'Actualizar contraseña'}
          </Button>
        </form>
      )}
    </div>
  );
}

export default function ResetPasswordPage() {
  return (
    <Suspense fallback={<p className="text-zinc-500">Cargando...</p>}>
      <ResetPasswordForm />
    </Suspense>
  );
}
