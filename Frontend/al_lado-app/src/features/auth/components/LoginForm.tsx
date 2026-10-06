'use client';

import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';

export function LoginForm() {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      const data = await login({ email, contrasena });

      let destino = '/';
      if (data.roles?.includes('ROLE_ADMIN')) destino = '/admin/dashboard';
      else if (data.roles?.includes('ROLE_EMPLEADOR')) destino = '/empleador/dashboard';
      else if (data.roles?.includes('ROLE_TRABAJADOR')) destino = '/trabajador/dashboard';

      // Recarga completa para que el Navbar lea la sesión nueva
      window.location.href = destino;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al iniciar sesión');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-5 w-full max-w-md">
      {error && (
        <div className="p-3 rounded-lg bg-red-500/10 border border-red-500/30 text-red-400 text-sm">
          {error}
        </div>
      )}
      <div>
        <label className="block text-sm font-medium text-zinc-300 mb-2">Email</label>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className="w-full px-4 py-3 rounded-xl bg-[#1a1a1c] border border-[#2e2e33] text-white placeholder-zinc-500 focus:outline-none focus:border-[#ffc107] focus:ring-1 focus:ring-[#ffc107] transition"
          placeholder="tu@email.com"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-zinc-300 mb-2">Contraseña</label>
        <input
          type="password"
          value={contrasena}
          onChange={(e) => setContrasena(e.target.value)}
          required
          className="w-full px-4 py-3 rounded-xl bg-[#1a1a1c] border border-[#2e2e33] text-white placeholder-zinc-500 focus:outline-none focus:border-[#ffc107] focus:ring-1 focus:ring-[#ffc107] transition"
          placeholder="••••••••"
        />
      </div>
      <button
        type="submit"
        className="w-full py-3 px-4 bg-[#ffc107] text-black font-bold rounded-xl hover:bg-[#eab308] transition"
      >
        Ingresar
      </button>
    </form>
  );
}