'use client';

import { cn } from '@/lib/utils';

import Link from 'next/link';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { usePathname } from 'next/navigation';
import {
  Activity,
  Briefcase,
  Calendar,
  FileText,
  FolderOpen,
  LogOut,
  Settings,
  ShieldCheck,
  Star,
  Users,
} from 'lucide-react';

const MENU_ITEMS = [
  {
    seccion: 'Explorar',
    icon: Briefcase,
    items: [
      { name: 'Oportunidades', href: '/trabajador/oportunidades', icon: Briefcase },
      { name: 'Mis postulaciones', href: '/trabajador/postulaciones', icon: FileText },
    ],
  },
  {
    seccion: 'Mi espacio',
    icon: FolderOpen,
    items: [
      { name: 'Disponibilidad', href: '/trabajador/disponibilidad', icon: Calendar },
      { name: 'Mi portafolio', href: '/trabajador/portafolio', icon: FolderOpen },
    ],
  },
  {
    seccion: 'Rendimiento',
    icon: Activity,
    items: [
      { name: 'Mi actividad', href: '/trabajador/actividad', icon: Activity },
      { name: 'Valoraciones', href: '/trabajador/valoraciones', icon: Star },
      { name: 'Suscripción PRO', href: '/trabajador/suscripcion', icon: ShieldCheck },
    ],
  },
  {
    seccion: 'Cuenta y comunidad',
    icon: Users,
    items: [
      { name: 'Comunidad', href: '/trabajador/comunidad', icon: Users },
      { name: 'Preferencias', href: '/trabajador/preferencias', icon: Settings },
      { name: 'Seguridad', href: '/trabajador/seguridad', icon: ShieldCheck },
    ],
  },
];

export default function TrabajadorSidebar() {
  const { logout } = useAuth();
  const pathname = usePathname();

  return (
    <aside className="w-full shrink-0 border-b border-[#2e2e33] bg-[#111] p-3 md:sticky md:top-16 md:h-[calc(100vh-4rem)] md:w-64 md:overflow-y-auto md:border-r md:border-b-0 md:p-4">
      <div className="mb-4 flex items-center gap-3 rounded-2xl border border-[#2e2e33] bg-[#171718] px-3 py-2.5">
        <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-[#ffc107]/15 text-[#ffc107]">
          <Briefcase className="h-4 w-4" />
        </div>
        <div>
          <p className="text-[10px] font-bold uppercase tracking-[0.2em] text-zinc-400">
            Espacio
          </p>
          <p className="text-sm font-bold text-white">Trabajador</p>
        </div>
      </div>

      <nav className="flex gap-3 overflow-x-auto pb-1 md:block md:space-y-4 md:pb-0">
        {MENU_ITEMS.map((grupo) => {
          const GroupIcon = grupo.icon;
          return (
            <div key={grupo.seccion} className="min-w-[150px] md:min-w-0">
              <div className="mb-2 flex items-center gap-2 px-2 text-zinc-400">
                <GroupIcon className="h-3.5 w-3.5 text-[#ffc107]" />
                <p className="text-[10px] font-bold uppercase tracking-[0.2em]">
                  {grupo.seccion}
                </p>
              </div>

              <div className="space-y-1.5">
                {grupo.items.map((item) => {
                  const isActive = pathname.startsWith(item.href);
                  const Icon = item.icon;
                  return (
                    <Link
                      key={item.name}
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
  );
}
export interface SidebarItem {
  label: string;
  href: string;
}

interface SidebarProps {
  items: SidebarItem[];
}

export function Sidebar({ items }: SidebarProps) {
  const pathname = usePathname();

  return (
    <aside className="w-64 border-r border-[#222] bg-[#111] h-[calc(100vh-4rem)] p-4 hidden md:block shrink-0 sticky top-16">
      <nav className="space-y-1">
        {items.map((item) => {
          const isActive = pathname === item.href;
          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                'flex items-center px-4 py-2.5 text-sm font-medium rounded-xl transition-colors',
                isActive
                  ? 'bg-[#ffc107] text-black font-semibold'
                  : 'text-zinc-400 hover:bg-[#26262a] hover:text-white',
              )}
            >
              {item.label}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}
