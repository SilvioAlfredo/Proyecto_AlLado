'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { administradoresService } from '@/features/usuario/services/administradoresService';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { PageHeader, Note } from '@/components/ui/shared';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Feedback } from '@/features/admin/components/Feedback';
import { errorMessage } from '@/lib/errors';

export default function AgregarAdministradorPage() {
  const [form, setForm] = useState({
    nombre: '',
    email: '',
    telefono: '',
    password: '',
    nivelAcceso: 'Gestión de comunidad',
  });
  const { userId } = useAuth();
  const client = useQueryClient();
  const mutation = useMutation({
    mutationFn: () =>
      administradoresService.crear({
        ...form,
        nombre: form.nombre.trim(),
        email: form.email.trim(),
        telefono: form.telefono.trim(),
      }),
    onSuccess: () => {
      client.invalidateQueries({ queryKey: ['admin-personas', 'administradores'] });
      setForm((previous) => ({ ...previous, password: '' }));
    },
  });

  return (
    <div className="space-y-6">
      <Link
        href="/admin/personas?rol=ADMIN"
        className="text-sm text-zinc-400 hover:text-white"
      >
        ← Volver a Personas
      </Link>
      <PageHeader
        eyebrow="Personas"
        title="Agregar administrador"
        subtitle="Creá una cuenta de acceso al panel."
      />
      <div className="grid gap-5 lg:grid-cols-[1fr_280px]">
        <form
          className="space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-6"
          onSubmit={(event) => {
            event.preventDefault();
            mutation.mutate();
          }}
        >
          {mutation.isError && <Feedback message={errorMessage(mutation.error)} />}
          {mutation.isSuccess && (
            <Feedback
              success
              message="Administrador creado. Ya puede iniciar sesión con su email y contraseña."
            />
          )}
          <div className="grid gap-4 sm:grid-cols-2">
            {(['nombre', 'email', 'telefono', 'password'] as const).map((key) => (
              <label key={key} className="space-y-2 text-sm">
                <span>
                  {
                    {
                      nombre: 'Nombre',
                      email: 'Email',
                      telefono: 'Teléfono',
                      password: 'Contraseña',
                    }[key]
                  }
                </span>
                <Input
                  required
                  type={
                    key === 'password'
                      ? 'password'
                      : key === 'email'
                        ? 'email'
                        : key === 'telefono'
                          ? 'tel'
                          : 'text'
                  }
                  autoComplete={key === 'password' ? 'new-password' : 'off'}
                  minLength={key === 'password' ? 8 : undefined}
                  maxLength={
                    key === 'nombre'
                      ? 100
                      : key === 'email'
                        ? 150
                        : key === 'telefono'
                          ? 20
                          : undefined
                  }
                  value={form[key]}
                  onChange={(event) => {
                    mutation.reset();
                    setForm({ ...form, [key]: event.target.value });
                  }}
                />
              </label>
            ))}
          </div>
          <label className="block space-y-2 text-sm">
            <span>Nivel de acceso · texto</span>
            <Input
              required
              value={form.nivelAcceso}
              onChange={(event) => setForm({ ...form, nivelAcceso: event.target.value })}
            />
          </label>
          <Note>
            El nivel de acceso es una descripción de la cuenta. No modifica sus permisos:
            todas las cuentas ADMIN tienen acceso al panel.
          </Note>
          <div className="flex gap-3">
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending ? 'Creando…' : 'Crear administrador'}
            </Button>
            <Link href="/admin/personas?rol=ADMIN">
              <Button type="button" variant="outline">
                Volver
              </Button>
            </Link>
          </div>
        </form>
        <aside className="h-fit space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
          <p className="text-xs font-bold uppercase text-[#ffc107]">
            Acceso administrativo
          </p>
          <h2 className="font-bold">Permisos del panel</h2>
          <p className="text-sm text-zinc-400">
            Gestionar personas, revisar reportes y administrar contenido de la comunidad.
          </p>
          <p className="text-sm text-zinc-400">
            Usá un email que no pertenezca a otra cuenta y una contraseña de al menos 8
            caracteres.
          </p>
          {userId && (
            <p className="text-xs text-zinc-500">Sesión de administrador identificada.</p>
          )}
        </aside>
      </div>
    </div>
  );
}
