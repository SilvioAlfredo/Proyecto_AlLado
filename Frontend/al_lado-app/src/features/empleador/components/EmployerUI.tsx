import type { ReactNode } from 'react';
import { errorMessage } from '@/lib/errors';

export const panel = 'rounded-2xl border border-[#30302f] bg-[#171716] p-5 md:p-6';
export const control =
  'w-full rounded-xl border border-[#3a3a3f] bg-[#222221] px-4 py-3 text-sm text-white focus:outline-none focus:ring-2 focus:ring-[#ffc107]';
export function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <label className="block space-y-2 text-sm font-medium text-zinc-300">
      <span>{label}</span>
      {children}
    </label>
  );
}
export function Feedback({ error, success }: { error?: unknown; success?: string }) {
  return (
    <>
      {!!error && (
        <p
          role="alert"
          className="rounded-xl border border-red-400/30 bg-red-400/10 p-3 text-sm text-red-300"
        >
          {errorMessage(error)}
        </p>
      )}
      {success && (
        <p
          role="status"
          className="rounded-xl border border-emerald-400/30 bg-emerald-400/10 p-3 text-sm text-emerald-300"
        >
          {success}
        </p>
      )}
    </>
  );
}
export function QueryStatus({ pending, error }: { pending: boolean; error?: unknown }) {
  return pending ? (
    <p role="status" className="py-8 text-sm text-zinc-400">
      Cargando…
    </p>
  ) : (
    <Feedback error={error} />
  );
}
export const fecha = (value?: string) =>
  value
    ? new Date(value.length === 10 ? `${value}T12:00:00` : value).toLocaleDateString(
        'es-AR',
      )
    : 'Sin fecha registrada';
