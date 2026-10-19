'use client';

import { useState } from 'react';
import { authService } from '@/features/auth/services/authService';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

// Backend: POST /auth/password {email, currentPassword, newPassword} (autenticado)
export function CambiarPasswordForm() {
  const { user } = useAuth();
  const [actual, setActual] = useState('');
  const [nueva, setNueva] = useState('');
  const [ok, setOk] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  return (
    <form
      className="max-w-md space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-6"
      onSubmit={async (e) => {
        e.preventDefault();
        setError(null);
        setOk(false);
        if (!user?.email) {
          setError('Iniciá sesión de nuevo.');
          return;
        }
        setLoading(true);
        try {
          await authService.changePassword(user.email, actual, nueva);
          setOk(true);
          setActual('');
          setNueva('');
        } catch (err: any) {
          setError(err?.response?.data?.message || 'No se pudo cambiar la contraseña.');
        } finally {
          setLoading(false);
        }
      }}
    >
      <h3 className="font-bold text-white">Cambiar contraseña</h3>
      {error && (
        <div className="rounded-lg border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-400">{error}</div>
      )}
      {ok && (
        <div className="rounded-lg border border-emerald-500/30 bg-emerald-500/10 p-3 text-sm text-emerald-400">
          Contraseña actualizada.
        </div>
      )}
      <div>
        <label className="mb-2 block text-sm font-medium text-zinc-300">Contraseña actual</label>
        <Input type="password" required value={actual} onChange={(e) => setActual(e.target.value)} />
      </div>
      <div>
        <label className="mb-2 block text-sm font-medium text-zinc-300">Nueva contraseña</label>
        <Input
          type="password"
          required
          minLength={8}
          value={nueva}
          onChange={(e) => setNueva(e.target.value)}
          placeholder="Mínimo 8 caracteres"
        />
      </div>
      <Button type="submit" disabled={loading} className="w-full">
        {loading ? 'Guardando...' : 'Actualizar contraseña'}
      </Button>
    </form>
  );
}
