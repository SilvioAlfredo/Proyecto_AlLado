import Link from 'next/link';
import { LoginForm } from '@/features/auth/components/LoginForm';
import { Note } from '@/components/ui/shared';

export default function Page() {
  return (
    <div className="grid min-h-[calc(100vh-8rem)] gap-8 lg:grid-cols-[3fr_2fr]">
      <section
        className={
          'flex flex-col justify-center rounded-3xl border border-zinc-800 ' +
          'bg-gradient-to-br from-[#202327] to-[#131516] p-8 md:p-12'
        }
      >
        <p className="mb-6 text-xs font-bold tracking-[0.2em] text-[#ffc107]">
          PANEL ADMINISTRATIVO
        </p>
        <h1 className="max-w-xl text-4xl font-bold leading-tight md:text-6xl">
          Un espacio para cuidar la comunidad.
        </h1>
        <p className="mt-6 max-w-md text-zinc-400">
          Gestioná personas, oportunidades y contenidos desde un solo lugar.
        </p>
      </section>
      <section className="flex flex-col justify-center gap-6 p-4 md:p-8">
        <Link href="/" className="text-sm text-zinc-400">
          ← Volver a AlLado
        </Link>
        <div>
          <p className="mb-3 text-xs font-bold tracking-[0.2em] text-[#ffc107]">
            ACCESO SEGURO
          </p>
          <h2 className="text-2xl font-bold">Acceso a administración</h2>
          <p className="mt-3 text-sm text-zinc-400">
            Ingresá con tu cuenta de administración. Este acceso no tiene registro
            público.
          </p>
        </div>
        <LoginForm adminOnly />
        <Note>Solo las cuentas con acceso ADMIN pueden ingresar al panel.</Note>
        <Link href="/login" className="text-sm text-zinc-400">
          Acceso de trabajadores y empleadores →
        </Link>
      </section>
    </div>
  );
}
