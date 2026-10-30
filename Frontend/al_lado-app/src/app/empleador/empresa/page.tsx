'use client';
import { useState } from 'react';
import Link from 'next/link';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { perfilEmpresarialService } from '@/features/comunidad/services/perfilEmpresarialService';
import type { PerfilEmpresarialDTO } from '@/types/comunidad';
import { PageHeader, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import {
  panel,
  control,
  Field,
  Feedback,
  QueryStatus,
} from '@/features/empleador/components/EmployerUI';
function EmpresaForm({
  initial,
  empleadorId,
  onSaved,
}: {
  initial?: PerfilEmpresarialDTO;
  empleadorId: number;
  onSaved: () => Promise<unknown>;
}) {
  const [form, setForm] = useState({
    nombreEmpresa: initial?.nombreEmpresa ?? '',
    cuit: initial?.cuit ?? '',
    descripcion: initial?.descripcion ?? '',
    sitioWeb: initial?.sitioWeb ?? '',
    urlLogo: initial?.urlLogo ?? '',
  });
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
          await perfilEmpresarialService.guardar(empleadorId, form);
          await onSaved();
          setOk('Perfil empresarial guardado.');
        } catch (err) {
          setError(err);
        } finally {
          setBusy(false);
        }
      }}
    >
      <h2 className="font-bold">Información del negocio</h2>
      <Feedback error={error} success={ok} />
      <div className="grid gap-4 sm:grid-cols-2">
        <Field label="Nombre de la empresa">
          <input
            required
            className={control}
            value={form.nombreEmpresa}
            onChange={(e) => setForm({ ...form, nombreEmpresa: e.target.value })}
          />
        </Field>
        <Field label="CUIT">
          <input
            className={control}
            value={form.cuit}
            onChange={(e) => setForm({ ...form, cuit: e.target.value })}
          />
        </Field>
      </div>
      <Field label="Descripción">
        <textarea
          className={control}
          rows={5}
          value={form.descripcion}
          onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
        />
      </Field>
      <Field label="Sitio web">
        <input
          type="url"
          className={control}
          value={form.sitioWeb}
          onChange={(e) => setForm({ ...form, sitioWeb: e.target.value })}
          placeholder="https://…"
        />
      </Field>
      <Field label="URL del logo (opcional)">
        <input
          type="url"
          className={control}
          value={form.urlLogo}
          onChange={(e) => setForm({ ...form, urlLogo: e.target.value })}
        />
      </Field>
      <Button type="submit" disabled={busy}>
        {busy ? 'Guardando…' : 'Guardar perfil empresarial'}
      </Button>
    </form>
  );
}
export default function Page() {
  const { perfil, userId, refresh } = useEmpleador();
  const perfilEmpresa = perfil.data?.perfilEmpresarial;
  const camposObligatorios = [
    perfilEmpresa?.nombreEmpresa,
    perfilEmpresa?.descripcion,
    perfilEmpresa?.cuit,
  ];
  const completado = camposObligatorios.filter(Boolean).length;
  const completitud = Math.round((completado / camposObligatorios.length) * 100);
  const siguientePaso =
    completado < camposObligatorios.length
      ? 'Faltan datos de tu negocio para completar la presentación y generar mayor confianza.'
      : 'Tu perfil empresarial está completo y listo para recibir postulantes.';

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Perfil empresarial"
        title="Tu empresa en AlLado."
        subtitle="Presentá tu negocio y mantené su información al día."
      />
      <QueryStatus pending={perfil.isPending} error={perfil.error} />
      {perfil.data && userId && (
        <>
          <section className={`${panel} border-l-4 border-l-[#ffc107]`}>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-[#ffc107]">
              Estado del perfil
            </p>
            <div className="mt-3 flex flex-wrap items-end justify-between gap-3">
              <div>
                <p className="text-2xl font-black text-white">{completitud}%</p>
                <p className="text-sm text-zinc-400">completitud del negocio</p>
              </div>
              <span className="rounded-full bg-[#ffc107]/10 px-2.5 py-1 text-xs font-bold text-[#ffc107]">
                {perfilEmpresa?.verificado ? 'Verificado' : 'Pendiente de verificación'}
              </span>
            </div>
            <p className="mt-3 text-sm text-zinc-300">{siguientePaso}</p>
          </section>
          <div className="grid items-start gap-5 lg:grid-cols-[1fr_280px]">
            <EmpresaForm
              key={userId}
              initial={perfilEmpresa}
              empleadorId={userId}
              onSaved={refresh}
            />
            <aside className={`${panel} space-y-4`}>
              <h2 className="font-bold">Estado de verificación</h2>
              <p className="text-[#ffc107]">
                {perfilEmpresa?.verificado
                  ? 'Empresa verificada'
                  : 'Empresa sin verificar'}
              </p>
              <p className="text-sm text-zinc-400">
                La administración revisa y verifica la información del negocio.
              </p>
              <Note>
                El perfil empresarial requiere un plan PRO activo.{' '}
                <Link href="/empleador/plan" className="text-[#ffc107]">
                  Ver mi plan →
                </Link>
              </Note>
            </aside>
          </div>
        </>
      )}
    </div>
  );
}
