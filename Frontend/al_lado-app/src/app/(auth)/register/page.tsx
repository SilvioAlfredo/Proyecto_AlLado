import { RegisterForm } from '@/features/auth/components/RegisterForm';
import Link from 'next/link';
import { ArrowLeft, ArrowUpRight } from 'lucide-react';

export default async function RegisterPage({
  searchParams,
}: {
  searchParams: Promise<{ rol?: string }>;
}) {
  const { rol } = await searchParams;
  const initialRole = rol === 'EMPLEADOR' ? 'EMPLEADOR' : 'TRABAJADOR';
  return (
    <div className="w-full max-w-4xl py-5 md:py-9">
      <div className="mb-7 flex items-center justify-between gap-4">
        <Link
          href="/"
          className="inline-flex items-center gap-2 text-xs text-zinc-400 hover:text-white"
        >
          <ArrowLeft size={14} />
          Volver al inicio
        </Link>
        <p className="text-xs text-zinc-400">
          ¿Ya tenés cuenta?{' '}
          <Link
            href="/login"
            className="font-semibold text-allado-yellow hover:underline"
          >
            Ingresá
          </Link>
        </p>
      </div>
      <section className="overflow-hidden rounded-[2rem] border border-allado-border bg-allado-card/40">
        <header className="relative overflow-hidden border-b border-allado-border px-6 py-7 sm:px-10 sm:py-8">
          <ArrowUpRight
            aria-hidden="true"
            className="absolute -right-5 -top-5 hidden h-48 w-48 text-allado-yellow/10 sm:block"
            strokeWidth={1}
          />
          <p className="relative text-[10px] font-semibold uppercase tracking-[0.2em] text-allado-yellow">
            Tu próximo paso empieza acá
          </p>
          <h1 className="relative mt-3 text-3xl font-extrabold tracking-tight sm:text-4xl">
            Hacete un lugar en AlLado.
          </h1>
          <p className="relative mt-3 max-w-lg text-sm leading-relaxed text-zinc-400">
            Creá tu cuenta para ofrecer tus servicios o encontrar a la persona indicada
            para tu próximo trabajo.
          </p>
        </header>
        <div className="px-6 py-7 sm:px-10 sm:py-8">
          <RegisterForm key={initialRole} initialRole={initialRole} />
        </div>
      </section>
    </div>
  );
}
