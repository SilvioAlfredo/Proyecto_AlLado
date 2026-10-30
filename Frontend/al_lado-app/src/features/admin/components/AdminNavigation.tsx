'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import {
  AlertTriangle,
  BadgeDollarSign,
  BriefcaseBusiness,
  Building2,
  LayoutDashboard,
  LogOut,
  Megaphone,
  MessageSquareMore,
  Newspaper,
  ShieldCheck,
  Tags,
  UserRound,
  Users,
  Wallet,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { useAuth } from '@/features/auth/hooks/useAuth';

const groups = [
  {
    title: 'Inicio',
    icon: LayoutDashboard,
    links: [['Resumen', '/admin/dashboard', LayoutDashboard]],
  },
  {
    title: 'Atención',
    icon: AlertTriangle,
    links: [
      ['Personas', '/admin/personas', Users],
      ['Reportes', '/admin/reportes', ShieldCheck],
    ],
  },
  {
    title: 'Comunidad',
    icon: Users,
    links: [
      ['Empresas', '/admin/empresas', Building2],
      ['Foro', '/admin/foro', MessageSquareMore],
    ],
  },
  {
    title: 'Finanzas',
    icon: Wallet,
    links: [
      ['Transferencias', '/admin/donaciones', BadgeDollarSign],
      ['Suscripciones', '/admin/suscripciones', BriefcaseBusiness],
    ],
  },
  {
    title: 'Gestión',
    icon: Newspaper,
    links: [
      ['Comunicados', '/admin/comunicados', Newspaper],
      ['Oportunidades', '/admin/oportunidades', BriefcaseBusiness],
      ['Publicidad', '/admin/publicidad', Megaphone],
      ['Etiquetas', '/admin/etiquetas', Tags],
    ],
  },
];

export function AdminNavigation() {
  const pathname = usePathname();
  const { logout } = useAuth();

  return (
    <aside
      className={
        'w-full shrink-0 border-b border-[#2e2e33] bg-[#111] p-3 ' +
        'md:sticky md:top-16 md:h-[calc(100vh-4rem)] md:w-64 ' +
        'md:overflow-y-auto md:border-r md:border-b-0 md:p-4'
      }
    >
      <div className="mb-4 flex items-center gap-3 rounded-2xl border border-[#2e2e33] bg-[#171718] px-3 py-2.5">
        <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-[#ffc107]/15 text-[#ffc107]">
          <ShieldCheck className="h-4 w-4" />
        </div>
        <div>
          <p className="text-[10px] font-bold uppercase tracking-[0.2em] text-zinc-400">
            Panel
          </p>
          <p className="text-sm font-bold text-white">Administración</p>
        </div>
      </div>

      <nav
        aria-label="Administración"
        className="flex gap-3 overflow-x-auto pb-1 md:block md:space-y-4 md:pb-0"
      >
        {groups.map((group) => {
          const GroupIcon = group.icon;

          return (
            <div key={group.title} className="min-w-[150px] md:min-w-0">
              <div className="mb-2 flex items-center gap-2 px-2 text-zinc-400">
                <GroupIcon className="h-3.5 w-3.5 text-[#ffc107]" />
                <p className="text-[10px] font-bold uppercase tracking-[0.2em]">
                  {group.title}
                </p>
              </div>

              <div className="space-y-1.5">
                {group.links.map(([label, href, Icon]) => {
                  const active = pathname === href || pathname.startsWith(`${href}/`);
                  const ItemIcon = Icon as typeof UserRound;

                  return (
                    <Link
                      key={href}
                      href={href}
                      aria-current={active ? 'page' : undefined}
                      className={cn(
                        'flex items-center gap-2.5 rounded-xl px-2.5 py-2 transition-all',
                        active
                          ? 'bg-[#ffc107] text-black shadow-md shadow-[#ffc107]/20'
                          : 'text-zinc-300 hover:bg-[#26262a] hover:text-white',
                      )}
                    >
                      <span
                        className={cn(
                          'flex h-7 w-7 items-center justify-center rounded-lg border',
                          active
                            ? 'border-black/10 bg-black/5 text-black'
                            : 'border-[#2e2e33] bg-[#1a1a1c] text-zinc-400',
                        )}
                      >
                        <ItemIcon className="h-3.5 w-3.5" />
                      </span>
                      <span className="text-sm font-medium">{label}</span>
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
