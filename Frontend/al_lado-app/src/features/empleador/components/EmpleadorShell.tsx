'use client';
import { useEffect, useState, type ReactNode } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { Menu, X, Plus, LogOut } from 'lucide-react';
import { useAuth } from '@/features/auth/hooks/useAuth';

const links = [
  ['Inicio', 'dashboard'],
  ['Mis solicitudes', 'solicitudes'],
  ['Buscar trabajadores', 'trabajadores'],
  ['Favoritos', 'favoritos'],
  ['Contratos y trabajos', 'contratos'],
  ['Postulantes recibidos', 'candidatos'],
  ['Mi perfil y cuenta', 'perfil'],
  ['Perfil empresarial', 'empresa'],
  ['Mis anuncios', 'anuncios'],
  ['Mi plan', 'plan'],
  ['Transferencias', 'transferencias'],
  ['Valoraciones', 'valoraciones'],
  ['Notificaciones', 'notificaciones'],
];
export function EmpleadorShell({ children }: { children: ReactNode }) {
  const { user, userId, loading, logout } = useAuth();
  const pathname = usePathname();
  const router = useRouter();
  const [open, setOpen] = useState(false);
  const allowed = user?.roles.includes('ROLE_EMPLEADOR');
  useEffect(() => {
    if (!loading && !allowed)
      router.replace(
        !user
          ? '/login'
          : user.roles.includes('ROLE_ADMIN')
            ? '/admin/dashboard'
            : '/trabajador/dashboard',
      );
  }, [loading, allowed, user, router]);
  if (loading || !allowed) return <p role="status">Verificando acceso…</p>;
  if (!userId)
    return <p role="alert">No pudimos identificar tu cuenta. Volvé a iniciar sesión.</p>;
  return (
    <div className="flex min-h-[calc(100vh-7rem)] gap-6">
      <aside className="hidden w-56 shrink-0 border-r border-[#30302f] pr-5 md:block">
        <p className="mb-6 text-xs font-semibold uppercase text-zinc-400">
          Espacio empleador
        </p>
        <Link
          href="/empleador/solicitudes/nueva"
          className="mb-7 flex items-center justify-center gap-2 rounded-xl bg-[#ffc107] p-3 text-sm font-bold text-black"
        >
          <Plus size={18} />
          Nueva solicitud
        </Link>
        <nav aria-label="Espacio empleador" className="space-y-1">
          {links.map(([label, path]) => (
            <Link
              key={path}
              href={`/empleador/${path}`}
              aria-current={
                pathname.startsWith(`/empleador/${path}`) ? 'page' : undefined
              }
              className={`block rounded-lg px-3 py-2.5 text-sm ${pathname.startsWith(`/empleador/${path}`) ? 'bg-[#ffc107]/10 font-bold text-[#ffc107]' : 'text-zinc-400 hover:bg-white/5 hover:text-white'}`}
            >
              {label}
            </Link>
          ))}
        </nav>
        <p className="mt-8 break-all text-xs text-zinc-500">{user?.email}</p>
        <button
          onClick={() => void logout()}
          className="mt-4 flex gap-2 text-sm text-zinc-400"
        >
          <LogOut size={16} />
          Cerrar sesión
        </button>
      </aside>
      <div className="min-w-0 flex-1 space-y-5">
        <div className="md:hidden">
          <button
            aria-expanded={open}
            aria-controls="employer-mobile-nav"
            onClick={() => setOpen(!open)}
            className="flex items-center gap-2 rounded-xl border border-[#30302f] p-3"
          >
            {open ? <X size={18} /> : <Menu size={18} />}Espacio empleador
          </button>
          {open && (
            <nav
              id="employer-mobile-nav"
              className="mt-3 grid grid-cols-2 gap-1 rounded-xl bg-[#171716] p-3"
            >
              {links.map(([label, path]) => (
                <Link
                  onClick={() => setOpen(false)}
                  key={path}
                  href={`/empleador/${path}`}
                  className="p-2 text-sm text-zinc-300"
                >
                  {label}
                </Link>
              ))}
            </nav>
          )}
        </div>
        {children}
      </div>
    </div>
  );
}
