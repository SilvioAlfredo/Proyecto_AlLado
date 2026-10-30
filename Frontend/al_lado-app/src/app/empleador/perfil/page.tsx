'use client';
import { useState } from 'react';
import Link from 'next/link';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { empleadorService } from '@/features/usuario/services/empleadorService';
import { CambiarPasswordForm } from '@/features/auth/components/CambiarPasswordForm';
import { PageHeader, Avatar } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import {
  Field,
  Feedback,
  QueryStatus,
  panel,
  control,
} from '@/features/empleador/components/EmployerUI';
import type { EmpleadorDTO } from '@/types/usuario';
function Datos({
  data,
  onSaved,
}: {
  data: EmpleadorDTO;
  onSaved: () => Promise<unknown>;
}) {
  const [nombre, setNombre] = useState(data.nombre);
  const [telefono, setTelefono] = useState(data.telefono ?? '');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<unknown>(null);
  const [ok, setOk] = useState('');
  return (
    <form
      className={`${panel} space-y-4`}
      onSubmit={async (e) => {
        e.preventDefault();
        setBusy(true);
        setError(null);
        setOk('');
        try {
          await empleadorService.actualizarPerfil(data.id, {
            nombre: nombre.trim(),
            telefono: telefono.trim(),
          });
          await onSaved();
          setOk('Tus datos se guardaron.');
        } catch (err) {
          setError(err);
        } finally {
          setBusy(false);
        }
      }}
    >
      <h2 className="font-bold">Datos personales</h2>
      <Feedback error={error} success={ok} />
      <Field label="Nombre y apellido">
        <input
          className={control}
          required
          value={nombre}
          onChange={(e) => setNombre(e.target.value)}
        />
      </Field>
      <Field label="Teléfono">
        <input
          className={control}
          type="tel"
          required
          value={telefono}
          onChange={(e) => setTelefono(e.target.value)}
        />
      </Field>
      <Field label="Correo electrónico">
        <input className={control} type="email" readOnly value={data.email} />
      </Field>
      <Button disabled={busy} type="submit">
        {busy ? 'Guardando…' : 'Guardar perfil'}
      </Button>
    </form>
  );
}
export default function PerfilPage() {
  const { perfil, refresh } = useEmpleador();
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Tu cuenta"
        title="Mi perfil y cuenta"
        subtitle="Actualizá tus datos personales y la seguridad de tu cuenta."
      />
      <QueryStatus pending={perfil.isPending} error={perfil.error} />
      {perfil.data && (
        <>
          <div className="flex items-center gap-3">
            <Avatar nombre={perfil.data.nombre} />
            <div>
              <p className="font-bold">{perfil.data.nombre}</p>
              <p className="text-sm text-zinc-400">Empleador</p>
            </div>
          </div>
          <div className="grid items-start gap-5 lg:grid-cols-[1.4fr_1fr]">
            <Datos key={perfil.data.id} data={perfil.data} onSaved={refresh} />
            <CambiarPasswordForm />
          </div>
          <section className={panel}>
            <h2 className="mb-4 font-bold">Más de tu cuenta</h2>
            <div className="grid gap-3 sm:grid-cols-3">
              {[
                ['Perfil empresarial', 'empresa'],
                ['Mis anuncios', 'anuncios'],
                ['Mi plan', 'plan'],
              ].map(([label, path]) => (
                <Link
                  key={path}
                  href={`/empleador/${path}`}
                  className="rounded-xl border border-[#30302f] p-4 text-sm hover:border-[#ffc107]"
                >
                  {label} →
                </Link>
              ))}
            </div>
          </section>
        </>
      )}
    </div>
  );
}
