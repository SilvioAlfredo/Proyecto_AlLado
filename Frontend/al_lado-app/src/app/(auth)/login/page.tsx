import { LoginForm } from '@/features/auth/components/LoginForm';
import Link from 'next/link';

export default function LoginPage() {
  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center py-12">
      <div className="w-full max-w-md">
        {/* Header estilo mockup */}
        <div className="text-center mb-8">
          <div className="flex items-center justify-center gap-2 mb-6">
            <span className="flex h-10 w-10 items-center justify-center rounded-full bg-[#ffc107] text-black font-black text-xl">
              ◉
            </span>
            <span className="text-2xl font-extrabold text-white">
              Al<span className="text-[#ffc107]">L</span>ado
            </span>
          </div>
          <h1 className="text-3xl font-extrabold text-white mb-2">
            Tu oficio. Tu barrio.
          </h1>
          <p className="text-zinc-400">
            Conectá lo que sabés hacer con quienes lo necesitan.
          </p>
        </div>

        {/* Tarjeta amarilla */}
        <div className="bg-[#ffc107] rounded-2xl p-6 mb-8 text-center">
          <h2 className="text-2xl font-extrabold text-black mb-1">
            Hacemos barrio.
          </h2>
          <p className="text-black/70 text-sm">
            Un trabajo a la vez.
          </p>
        </div>

        {/* Formulario */}
        <LoginForm />

        {/* Link a registro */}
        <div className="mt-6 text-center">
          <p className="text-zinc-400 text-sm mb-3">
            ¿Todavía no tenés una cuenta?
          </p>
          <Link
            href="/register"
            className="inline-block w-full py-3 px-4 border border-[#2e2e33] text-white font-medium rounded-xl hover:border-zinc-600 transition"
          >
            Crear cuenta de trabajador
          </Link>
        </div>
      </div>
    </div>
  );
}
