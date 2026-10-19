'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { comunicadosService } from '@/features/comunidad/services/moderacionService';
import { Feedback } from '@/features/admin/components/Feedback';
import { PageHeader, Note } from '@/components/ui/shared';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';

export default function NuevoComunicadoPage() {
  const { user, userId } = useAuth();
  const router = useRouter();
  const client = useQueryClient();
  const [form, setForm] = useState({ titulo: '', contenido: '', alcance: 'Comunidad' });
  const mutation = useMutation({
    mutationFn: async () => {
      if (!userId || !user) {
        throw new Error('No se pudo identificar tu cuenta. Volvé a iniciar sesión.');
      }
      return comunicadosService.crear({
        titulo: form.titulo.trim(),
        contenido: form.contenido.trim(),
        alcance: form.alcance.trim(),
        emisor: { id: userId, nombre: user.email, email: user.email },
      });
    },
    onSuccess: () => {
      client.invalidateQueries({ queryKey: ['admin-comunicados'] });
      router.push('/admin/comunicados');
    },
  });

  return (
    <div className="space-y-6">
      <Link href="/admin/comunicados" className="text-sm text-zinc-400">
        ← Volver a Comunicados
      </Link>
      <PageHeader
        eyebrow="Gestión"
        title="Nuevo comunicado"
        subtitle="Prepará un mensaje para la comunidad."
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
          <label className="block space-y-2 text-sm">
            <span>Título</span>
            <Input
              required
              maxLength={150}
              value={form.titulo}
              onChange={(event) => setForm({ ...form, titulo: event.target.value })}
            />
          </label>
          <label className="block space-y-2 text-sm">
            <span>Contenido</span>
            <textarea
              required
              rows={7}
              className="w-full rounded-xl border border-[#3a3a3f] bg-[#26262a] p-3"
              value={form.contenido}
              onChange={(event) => setForm({ ...form, contenido: event.target.value })}
            />
          </label>
          <label className="block space-y-2 text-sm">
            <span>Alcance</span>
            <Input
              required
              maxLength={50}
              value={form.alcance}
              onChange={(event) => setForm({ ...form, alcance: event.target.value })}
            />
          </label>
          <Note>
            Se publica con la fecha actual. No se programa ni se edita después de crearlo.
          </Note>
          <div className="flex gap-3">
            <Button type="submit" disabled={mutation.isPending || !userId}>
              {mutation.isPending ? 'Publicando…' : 'Crear comunicado'}
            </Button>
            <Link href="/admin/comunicados">
              <Button type="button" variant="outline">
                Volver sin crear
              </Button>
            </Link>
          </div>
        </form>
        <aside className="h-fit space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
          <p className="text-xs font-bold uppercase text-[#ffc107]">Emisor actual</p>
          <p className="break-all text-sm">{user?.email}</p>
          <p className="text-sm text-zinc-400">
            Se registrará el administrador de esta sesión.
          </p>
          {!userId && (
            <Feedback
              message={
                'Esperando identificación de la cuenta. ' +
                'Si no se completa, volvé a iniciar sesión.'
              }
            />
          )}
        </aside>
      </div>
    </div>
  );
}
