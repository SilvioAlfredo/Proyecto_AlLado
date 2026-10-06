import { RegisterForm } from '@/features/auth/components/RegisterForm';
import Link from 'next/link';

export default function RegisterPage() {
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
            Creá tu cuenta
          </h1>
          <p className="text-zinc-400">
            Sumate con tu oficio. Empezá por tus datos.
          </p>
        </div>

        {/* Formulario */}
        <RegisterForm />

        {/* Link a login */}
        <div className="mt-6 text-center">
          <p className="text-zinc-400 text-sm">
            ¿Ya tenés cuenta?{' '}
            <Link href="/login" className="text-[#ffc107] font-medium hover:underline">
              Iniciar sesión
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
