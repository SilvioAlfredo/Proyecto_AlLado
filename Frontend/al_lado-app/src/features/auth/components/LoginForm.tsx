'use client';

import { useState } from 'react';
import Link from 'next/link';
import { ArrowRight, Eye, EyeOff, LoaderCircle } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { errorMessage } from '@/lib/errors';
import { useAuth } from '../hooks/useAuth';
import { loginDestination } from '../lib/access';

/**
 * Autentica una cuenta y la dirige al panel correspondiente a sus roles.
 *
 * @param props - Opciones del formulario.
 * @param props.adminOnly - Restringe el acceso a ADMIN antes de persistir la sesión.
 */
export function LoginForm({ adminOnly = false }: { adminOnly?: boolean }) {
  const { login } = useAuth();
  const router = useRouter();
  const [pending, setPending] = useState(false);
  const [email, setEmail] = useState(''); // impusts controladores
  const [contrasena, setContrasena] = useState('');  // use states
  const [visible, setVisible] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // previene la carga por defecto y activa el pending para desabilitar el boton
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setPending(true);
    try {
      const data = await login({ email: email.trim(), contrasena }, { adminOnly });
// si la respuesta es correcta se redirige al usuario a su panel con router.replace
      router.replace(loginDestination(data.roles, adminOnly));
    } catch (err: unknown) {
      setError(errorMessage(err, 'Error al iniciar sesión'));
    } finally {
      setPending(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-5 w-full max-w-md">
      {error && (
        <div
          role="alert"
          className="p-3 rounded-lg bg-red-500/10 border border-red-500/30 text-red-400 text-sm"
        >
          {error}
        </div>
      )}
      <div>
        <label htmlFor="login-email" className="sr-only">
          Correo electrónico
        </label>
        <input
          id="login-email"
          autoComplete="username"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className={
            'w-full px-4 py-3 rounded-xl bg-[#1a1a1c] border ' +
            'border-[#2e2e33] text-white placeholder-zinc-500 ' +
            'focus:outline-none focus:border-[#ffc107] focus:ring-1 ' +
            'focus:ring-[#ffc107] transition'
          }
          placeholder="Correo electrónico"
        />
      </div>
      <div>
        <label htmlFor="login-password" className="sr-only">
          Contraseña
        </label>
        <div className="relative">
          <input
            id="login-password"
            autoComplete="current-password"
            type={visible ? 'text' : 'password'}
            value={contrasena}
            onChange={(e) => setContrasena(e.target.value)}
            required
            className={
              'w-full px-4 py-3 rounded-xl bg-[#1a1a1c] border ' +
              'border-[#2e2e33] text-white placeholder-zinc-500 ' +
              'focus:outline-none focus:border-[#ffc107] focus:ring-1 ' +
              'focus:ring-[#ffc107] transition'
            }
            placeholder="Contraseña"
            style={{ paddingRight: 48 }}
          />
          <button
            type="button"
            aria-label={visible ? 'Ocultar contraseña' : 'Mostrar contraseña'}
            aria-pressed={visible}
            onClick={() => setVisible(!visible)}
            className="absolute right-3 top-1/2 -translate-y-1/2 rounded p-1 text-zinc-400 hover:text-white focus-visible:outline-2 focus-visible:outline-allado-yellow"
          >
            {visible ? <EyeOff size={18} /> : <Eye size={18} />}
          </button>
        </div>
        {!adminOnly && (
          <Link
            href="/forgot-password"
            className="mt-3 inline-block text-xs text-zinc-400 hover:text-allado-yellow"
          >
            ¿Olvidaste tu contraseña?
          </Link>
        )}
      </div>
      <button
        type="submit"
        disabled={pending}
        className={
          'w-full flex items-center justify-center gap-2 py-3.5 px-4 bg-[#ffc107] text-black font-bold ' +
          'rounded-xl hover:bg-[#eab308] transition disabled:opacity-50 focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-allado-yellow'
        }
      >
        {pending && <LoaderCircle size={17} className="animate-spin" />}
        {pending ? 'Ingresando…' : adminOnly ? 'Ingresar al panel' : 'Ingresar'}
        {!pending && <ArrowRight size={17} />}
      </button>
    </form>
  );
}
