import { LoginForm } from '@/features/auth/components/LoginForm';
import Link from 'next/link';
import { ArrowLeft, ArrowUpRight, MapPin, Wrench } from 'lucide-react';

export default function LoginPage() {
  return (
    <div className="grid w-full max-w-6xl gap-10 py-6 md:py-10 lg:grid-cols-[1.1fr_1fr] lg:items-center lg:gap-20">
      <section className="hidden lg:block">
        <p className="mb-6 flex items-center gap-2 text-[11px] font-semibold uppercase tracking-[0.2em] text-zinc-400">
          <span className="h-1.5 w-1.5 rounded-full bg-allado-yellow" />
          Qué bueno tenerte de vuelta
        </p>
        <h2 className="text-6xl font-black leading-[1.07] tracking-[-0.04em]">
          Tu oficio.
          <br />
          Tu gente.
          <br />
          <span className="text-allado-yellow">Tu lugar.</span>
        </h2>
        <p className="mt-6 max-w-sm text-base leading-relaxed text-zinc-400">
          Volvé a conectar con las oportunidades y las personas que hacen crecer tu
          trabajo.
        </p>
        <div
          aria-hidden="true"
          className="relative mt-10 max-w-md overflow-hidden rounded-3xl border border-allado-yellow/20 bg-[#18170f] p-7"
        >
          <div className="absolute -right-8 -top-8 h-36 w-36 rounded-full border-[24px] border-allado-yellow/10" />
          <div className="relative flex items-center gap-4">
            <span className="rounded-2xl bg-allado-yellow p-4 text-black">
              <Wrench size={27} />
            </span>
            <div>
              <p className="text-xs text-zinc-400">Lo que sabés hacer</p>
              <p className="mt-1 text-lg font-bold">Tiene un lugar en tu barrio.</p>
            </div>
          </div>
          <div className="mt-7 flex items-center gap-2 border-t border-allado-yellow/15 pt-5 text-xs text-zinc-400">
            <MapPin size={15} className="text-allado-yellow" />
            Hacemos barrio. Un trabajo a la vez.
          </div>
        </div>
      </section>
      <section className="mx-auto w-full max-w-md rounded-[2rem] border border-allado-border bg-allado-card/50 p-6 sm:p-9">
        <Link
          href="/"
          className="mb-8 inline-flex items-center gap-2 text-xs text-zinc-400 hover:text-white"
        >
          <ArrowLeft size={14} />
          Volver al inicio
        </Link>
        <p className="text-[10px] font-semibold uppercase tracking-[0.2em] text-allado-yellow">
          Tu espacio en AlLado
        </p>
        <h1 className="mt-3 text-3xl font-extrabold tracking-tight sm:text-4xl">
          Iniciá sesión.
        </h1>
        <p className="mb-8 mt-3 text-sm leading-relaxed text-zinc-400">
          Ingresá a tu cuenta y retomá lo que empezaste.
        </p>
        <LoginForm />
        <p className="mt-7 border-t border-allado-border pt-6 text-center text-sm text-zinc-400">
          ¿Todavía no tenés cuenta?{' '}
          <Link
            href="/register"
            className="font-semibold text-allado-yellow hover:underline"
          >
            Sumate a AlLado
          </Link>
        </p>
        <Link
          href="/acceso-admin"
          className="mt-6 flex items-center justify-center gap-1 text-xs text-zinc-500 hover:text-zinc-300"
        >
          Acceso de administración
          <ArrowUpRight size={13} />
        </Link>
      </section>
    </div>
  );
}
