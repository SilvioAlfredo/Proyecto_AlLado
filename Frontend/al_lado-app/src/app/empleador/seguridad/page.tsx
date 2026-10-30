'use client';

import { CambiarPasswordForm } from '@/features/auth/components/CambiarPasswordForm';
import { PageHeader } from '@/components/ui/shared';

export default function SeguridadEmpleadorPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Mi cuenta"
        title="Seguridad"
        subtitle="Mantené tu cuenta protegida y revisá tus credenciales con frecuencia."
      />
      <section className="rounded-2xl border border-allado-border bg-allado-card p-5">
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-allado-yellow">
          Estado de seguridad
        </p>
        <div className="mt-3 flex flex-wrap items-end justify-between gap-3">
          <div>
            <p className="text-2xl font-black text-white">Protegida</p>
            <p className="text-sm text-zinc-400">tu contraseña es la primera línea de defensa</p>
          </div>
          <span className="rounded-full bg-allado-yellow/10 px-2.5 py-1 text-xs font-bold text-allado-yellow">
            Recomendado revisar cada 3 meses
          </span>
        </div>
      </section>
      <CambiarPasswordForm />
    </div>
  );
}
