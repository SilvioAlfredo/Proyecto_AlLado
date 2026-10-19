'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/utils';

const groups = [
  { title: 'Inicio', links: [['Resumen', '/admin/dashboard']] },
  {
    title: 'Personas',
    links: [
      ['Comunidad', '/admin/personas'],
      ['Usuarios', '/admin/usuarios'],
    ],
  },
  {
    title: 'Moderación',
    links: [
      ['Reportes', '/admin/reportes'],
      ['Foro y valoraciones', '/admin/foro'],
      ['Empresas', '/admin/empresas'],
    ],
  },
  {
    title: 'Aportes',
    links: [
      ['Transferencias', '/admin/donaciones'],
      ['Suscripciones', '/admin/suscripciones'],
    ],
  },
  {
    title: 'Gestión',
    links: [
      ['Comunicados', '/admin/comunicados'],
      ['Oportunidades', '/admin/oportunidades'],
      ['Publicidad', '/admin/publicidad'],
      ['Etiquetas', '/admin/etiquetas'],
    ],
  },
];

export function AdminNavigation() {
  const pathname = usePathname();

  return (
    <aside
      className={
        'w-full shrink-0 border-b border-[#2e2e33] bg-[#111] p-4 ' +
        'md:sticky md:top-16 md:h-[calc(100vh-4rem)] md:w-56 ' +
        'md:overflow-y-auto md:border-r md:border-b-0'
      }
    >
      <p className="mb-4 text-xs font-bold uppercase tracking-widest text-[#ffc107]">
        Panel administrativo
      </p>
      <nav
        aria-label="Administración"
        className="flex gap-4 overflow-x-auto md:block md:space-y-4"
      >
        {groups.map((group) => (
          <div key={group.title} className="min-w-36">
            <p className="mb-1 text-xs font-semibold text-zinc-500">{group.title}</p>
            {group.links.map(([label, href]) => {
              const active = pathname === href || pathname.startsWith(`${href}/`);

              return (
                <Link
                  key={href}
                  href={href}
                  aria-current={active ? 'page' : undefined}
                  className={cn(
                    'block rounded-xl px-3 py-2 text-sm',
                    active
                      ? 'bg-[#ffc107] font-bold text-black'
                      : 'text-zinc-300 hover:bg-[#26262a]',
                  )}
                >
                  {label}
                </Link>
              );
            })}
          </div>
        ))}
      </nav>
    </aside>
  );
}
