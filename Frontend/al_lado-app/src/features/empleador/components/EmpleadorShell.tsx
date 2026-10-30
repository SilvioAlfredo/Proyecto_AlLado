'use client';
import { useEffect, useState, type ReactNode } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import {
  BriefcaseBusiness,
  Building2,
  CreditCard,
  FileText,
  Heart,
  LayoutDashboard,
  LogOut,
  Menu,
  Megaphone,
  Plus,
  Search,
  ShieldCheck,
  Star,
  Users,
  X,
} from 'lucide-react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { cn } from '@/lib/utils';

const MENU_ITEMS = [
  {
    seccion: 'Inicio',
    icon: LayoutDashboard,
    items: [
      { name: 'Dashboard', href: '/empleador/dashboard', icon: LayoutDashboard },
      { name: 'Nueva solicitud', href: '/empleador/solicitudes/nueva', icon: Plus },
      { name: 'Mis solicitudes', href: '/empleador/solicitudes', icon: FileText },
    ],
  },
  {
    seccion: 'Gestión',
    icon: Users,
    items: [
      { name: 'Postulantes', href: '/empleador/candidatos', icon: Users },
      { name: 'Contratos', href: '/empleador/contratos', icon: BriefcaseBusiness },
      { name: 'Buscar', href: '/empleador/trabajadores', icon: Search },
      { name: 'Favoritos', href: '/empleador/favoritos', icon: Heart },
    ],
  },
  {
    seccion: 'Perfil',
    icon: Building2,
    items: [
      { name: 'Empresa', href: '/empleador/empresa', icon: Building2 },
      { name: 'Anuncios', href: '/empleador/anuncios', icon: Megaphone },
      { name: 'Plan', href: '/empleador/plan', icon: CreditCard },
    ],
  },
  {
    seccion: 'Cuenta',
    icon: ShieldCheck,
    items: [
      { name: 'Transferencias', href: '/empleador/transferencias', icon: CreditCard },
      { name: 'Valoraciones', href: '/empleador/valoraciones', icon: Star },
      { name: 'Seguridad', href: '/empleador/seguridad', icon: ShieldCheck },
    ],
  },
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
      <aside className="hidden w-full shrink-0 border-b border-[#2e2e33] bg-[#111] p-3 md:sticky md:top-16 md:h-[calc(100vh-4rem)] md:w-64 md:overflow-y-auto md:border-r md:border-b-0 md:p-4 lg:flex lg:flex-col">
        <div className="mb-4 flex items-center gap-3 rounded-2xl border border-[#2e2e33] bg-[#171718] px-3 py-2.5">
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-[#ffc107]/15 text-[#ffc107]">
            <Building2 className="h-4 w-4" />
          </div>
          <div>
            <p className="text-[10px] font-bold uppercase tracking-[0.2em] text-zinc-400">
              Espacio
            </p>
            <p className="text-sm font-bold text-white">Empleador</p>
          </div>
        </div>

        <nav className="flex-1 space-y-4">
          {MENU_ITEMS.map((grupo) => {
            const GroupIcon = grupo.icon;
            return (
              <div key={grupo.seccion}>
                <div className="mb-2 flex items-center gap-2 px-2 text-zinc-400">
                  <GroupIcon className="h-3.5 w-3.5 text-[#ffc107]" />
                  <p className="text-[10px] font-bold uppercase tracking-[0.2em]">
                    {grupo.seccion}
                  </p>
                </div>
                <div className="space-y-1.5">
                  {grupo.items.map((item) => {
                    const isActive = pathname === item.href || pathname.startsWith(`${item.href}/`);
                    const Icon = item.icon;
                    return (
                      <Link
                        key={item.href}
                        href={item.href}
                        aria-current={isActive ? 'page' : undefined}
                        className={cn(
                          'flex items-center gap-2.5 rounded-xl px-2.5 py-2 transition-all',
                          isActive
                            ? 'bg-[#ffc107] text-black shadow-md shadow-[#ffc107]/20'
                            : 'text-zinc-300 hover:bg-[#26262a] hover:text-white',
                        )}
                      >
                        <span
                          className={cn(
                            'flex h-7 w-7 items-center justify-center rounded-lg border',
                            isActive
                              ? 'border-black/10 bg-black/5 text-black'
                              : 'border-[#2e2e33] bg-[#1a1a1c] text-zinc-400',
                          )}
                        >
                          <Icon className="h-3.5 w-3.5" />
                        </span>
                        <span className="text-sm font-medium">{item.name}</span>
                      </Link>
                    );
                  })}
                </div>
              </div>
            );
          })}
        </nav>

        <button
          type="button"
          onClick={() => void logout()}
          className="mt-6 flex w-full items-center gap-2 rounded-xl border border-rose-500/20 bg-rose-500/5 px-3 py-2.5 text-sm font-medium text-rose-400 transition-colors hover:bg-rose-500/10"
        >
          <LogOut className="h-4 w-4" />
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
              className="mt-3 grid grid-cols-2 gap-2 rounded-xl bg-[#171716] p-3"
            >
              {MENU_ITEMS.flatMap((grupo) =>
                grupo.items.map((item) => {
                  const isActive = pathname === item.href || pathname.startsWith(`${item.href}/`);
                  const Icon = item.icon;
                  return (
                    <Link
                      onClick={() => setOpen(false)}
                      key={item.href}
                      href={item.href}
                      className={`flex items-center gap-2 rounded-lg p-2 text-xs font-medium ${
                        isActive ? 'bg-[#ffc107]/10 font-bold text-[#ffc107]' : 'text-zinc-300'
                      }`}
                    >
                      <Icon className="h-4 w-4" />
                      <span>{item.name}</span>
                    </Link>
                  );
                }),
              )}
            </nav>
          )}
        </div>
        {children}
      </div>
    </div>
  );
}
