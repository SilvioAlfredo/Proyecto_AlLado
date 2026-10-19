'use client';

import { useState } from 'react';
import { ArrowRight, Eye, EyeOff, LoaderCircle } from 'lucide-react';
import { errorMessage } from '@/lib/errors';
import { useAuth } from '../hooks/useAuth';
import { RoleSelector } from './RoleSelector';
import { RolUsuario } from '@/types/usuario';

export function RegisterForm({
  initialRole = 'TRABAJADOR',
}: {
  initialRole?: 'TRABAJADOR' | 'EMPLEADOR';
}) {
  const { register } = useAuth();
  const [nombre, setNombre] = useState('');
  const [apellido, setApellido] = useState('');
  const [email, setEmail] = useState('');
  const [telefono, setTelefono] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [rol, setRol] = useState<RolUsuario>(initialRole);
  const [visible, setVisible] = useState(false);
  const [pending, setPending] = useState(false);
  const [creado, setCreado] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setCreado(false);
    setPending(true);
    try {
      await register({
        nombre,
        apellido,
        email: email.trim(),
        telefono,
        contrasena,
        rol,
      });
      setCreado(true);
    } catch (err: unknown) {
      setError(errorMessage(err, 'Error al registrarse'));
    } finally {
      setPending(false);
    }
  };

  const field =
    'w-full rounded-xl border border-allado-border bg-allado-bg/70 px-4 py-3 text-sm text-white placeholder:text-zinc-600 focus:outline-none focus:border-allado-yellow focus:ring-1 focus:ring-allado-yellow disabled:opacity-50';
  return (
    <form onSubmit={handleSubmit} className="w-full space-y-6">
      {creado && (
        <p
          role="status"
          className="rounded-xl border border-emerald-400/20 bg-emerald-400/10 p-4 text-sm text-emerald-300"
        >
          Cuenta creada correctamente.
        </p>
      )}
      {error && (
        <div
          role="alert"
          className="rounded-xl border border-red-400/20 bg-red-400/10 p-4 text-sm text-red-300"
        >
          {error}
        </div>
      )}
      <fieldset disabled={pending} className="space-y-3">
        <legend className="mb-3 text-sm font-semibold text-zinc-200">
          ¿Cómo querés usar AlLado?
        </legend>
        <RoleSelector selectedRole={rol} onSelectRole={setRol} />
      </fieldset>
      <div className="border-t border-allado-border pt-6">
        <h2 className="mb-4 text-sm font-semibold text-zinc-200">Tus datos personales</h2>
        <div className="grid gap-4 sm:grid-cols-2">
          <label className="space-y-2 text-xs font-medium text-zinc-300">
            <span className="block">Nombre</span>
            <input
              autoComplete="given-name"
              required
              disabled={pending}
              className={field}
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              placeholder="Tu nombre"
            />
          </label>
          <label className="space-y-2 text-xs font-medium text-zinc-300">
            <span className="block">Apellido</span>
            <input
              autoComplete="family-name"
              required
              disabled={pending}
              className={field}
              value={apellido}
              onChange={(e) => setApellido(e.target.value)}
              placeholder="Tu apellido"
            />
          </label>
          <label className="space-y-2 text-xs font-medium text-zinc-300">
            <span className="block">Correo electrónico</span>
            <input
              autoComplete="email"
              type="email"
              required
              disabled={pending}
              className={field}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="nombre@ejemplo.com"
            />
          </label>
          <label className="space-y-2 text-xs font-medium text-zinc-300">
            <span className="block">Teléfono</span>
            <input
              autoComplete="tel"
              type="tel"
              required
              maxLength={20}
              disabled={pending}
              className={field}
              value={telefono}
              onChange={(e) => setTelefono(e.target.value)}
              placeholder="Ej.: +54 351 555 1234"
            />
          </label>
        </div>
      </div>
      <div className="block space-y-2 text-xs font-medium text-zinc-300">
        <label htmlFor="register-password" className="block">
          Creá una contraseña
        </label>
        <div className="relative">
          <input
            id="register-password"
            autoComplete="new-password"
            type={visible ? 'text' : 'password'}
            aria-describedby="register-password-help"
            required
            minLength={8}
            disabled={pending}
            className={`${field} pr-12`}
            value={contrasena}
            onChange={(e) => setContrasena(e.target.value)}
            placeholder="Elegí tu contraseña"
          />
          <button
            type="button"
            onClick={() => setVisible(!visible)}
            aria-label={visible ? 'Ocultar contraseña' : 'Mostrar contraseña'}
            aria-pressed={visible}
            className="absolute right-3 top-1/2 -translate-y-1/2 rounded p-1 text-zinc-400 hover:text-white focus-visible:outline-2 focus-visible:outline-allado-yellow"
          >
            {visible ? <EyeOff size={18} /> : <Eye size={18} />}
          </button>
        </div>
        <span
          id="register-password-help"
          className="block text-xs font-normal text-zinc-500"
        >
          Usá al menos 8 caracteres.
        </span>
      </div>
      <button
        type="submit"
        disabled={pending || creado}
        className="flex w-full items-center justify-center gap-2 rounded-xl bg-allado-yellow px-4 py-3.5 text-sm font-bold text-black transition hover:bg-allado-yellow-hover focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-allado-yellow disabled:opacity-50"
      >
        {pending && <LoaderCircle size={17} className="animate-spin" />}
        {pending ? 'Creando cuenta…' : creado ? 'Cuenta creada' : 'Crear mi cuenta'}
        {!pending && !creado && <ArrowRight size={17} />}
      </button>
      <p className="text-center text-xs leading-relaxed text-zinc-500">
        Después podés completar tu perfil con más información sobre vos.
      </p>
    </form>
  );
}
