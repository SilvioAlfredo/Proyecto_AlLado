'use client';

import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { RoleSelector } from './RoleSelector';
import { RolUsuario } from '@/types/usuario';

export function RegisterForm() {
  const { register } = useAuth();
  const [nombre, setNombre] = useState('');
  const [apellido, setApellido] = useState('');
  const [email, setEmail] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [rol, setRol] = useState<RolUsuario>('TRABAJADOR');
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      await register({ nombre, apellido, email, contrasena, rol });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al registrarse');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-5 w-full max-w-md">
      {error && (
        <div className="p-3 rounded-lg bg-red-500/10 border border-red-500/30 text-red-400 text-sm">
          {error}
        </div>
      )}
      <RoleSelector selectedRole={rol} onSelectRole={setRol} />
      <div>
        <label className="block text-sm font-medium text-zinc-300 mb-2">Nombre</label>
        <input
          type="text"
          value={nombre}
          onChange={(e) => setNombre(e.target.value)}
          required
          className="w-full px-4 py-3 rounded-xl bg-[#1a1a1c] border border-[#2e2e33] text-white placeholder-zinc-500 focus:outline-none focus:border-[#ffc107] focus:ring-1 focus:ring-[#ffc107] transition"
          placeholder="Juan"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-zinc-300 mb-2">Apellido</label>
        <input
          type="text"
          value={apellido}
          onChange={(e) => setApellido(e.target.value)}
          required
          className="w-full px-4 py-3 rounded-xl bg-[#1a1a1c] border border-[#2e2e33] text-white placeholder-zinc-500 focus:outline-none focus:border-[#ffc107] focus:ring-1 focus:ring-[#ffc107] transition"
          placeholder="Pérez"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-zinc-300 mb-2">Email</label>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className="w-full px-4 py-3 rounded-xl bg-[#1a1a1c] border border-[#2e2e33] text-white placeholder-zinc-500 focus:outline-none focus:border-[#ffc107] focus:ring-1 focus:ring-[#ffc107] transition"
          placeholder="juan@email.com"
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
          placeholder="Mínimo 8 caracteres"
        />
      </div>
      <button
        type="submit"
        className="w-full py-3 px-4 bg-[#ffc107] text-black font-bold rounded-xl hover:bg-[#eab308] transition"
      >
        Crear Cuenta
      </button>
    </form>
  );
}