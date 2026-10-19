'use client';

import { cn } from '@/lib/utils';

import Link from 'next/link';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { usePathname } from 'next/navigation';
import {
  Briefcase,
  FileText,
  User,
  Calendar,
  FolderOpen,
  Activity,
  Star,
  Users,
  Settings,
  ShieldCheck,
  LogOut,
} from 'lucide-react';

const MENU_ITEMS = [
  {
    seccion: 'Explorar',
    items: [
      { name: 'Oportunidades', href: '/trabajador/oportunidades', icon: Briefcase },
      { name: 'Mis Postulaciones', href: '/trabajador/postulaciones', icon: FileText },
    ],
  },
  {
    seccion: 'Mi Espacio',
    items: [
      { name: 'Mi Perfil', href: '/trabajador/perfil', icon: User },
      { name: 'Mi Portafolio', href: '/trabajador/portafolio', icon: FolderOpen },
      { name: 'Disponibilidad', href: '/trabajador/disponibilidad', icon: Calendar },
    ],
  },
  {
    seccion: 'Rendimiento',
    items: [
      { name: 'Mi Actividad', href: '/trabajador/actividad', icon: Activity },
      { name: 'Valoraciones', href: '/trabajador/valoraciones', icon: Star },
      { name: 'Suscripción PRO', href: '/trabajador/suscripcion', icon: ShieldCheck },
    ],
  },
  {
    seccion: 'Comunidad & Ajustes',
    items: [
      { name: 'Comunidad', href: '/trabajador/comunidad', icon: Users },
      { name: 'Preferencias', href: '/trabajador/preferencias', icon: Settings },
    ],
  },
];

export default function TrabajadorSidebar() {
  const { logout } = useAuth();
  const pathname = usePathname();

  return (
    <aside className="w-56 shrink-0 bg-[#111] text-zinc-300 flex-col hidden md:flex sticky top-20 h-[calc(100dvh-6rem)] overflow-y-auto rounded-2xl border border-allado-border">
      <div className="p-6">
        <h2 className="text-2xl font-black text-white tracking-tight flex items-center gap-2">
          <div className="w-8 h-8 bg-allado-yellow rounded-lg flex items-center justify-center text-black">
            A
          </div>
          AlLado
        </h2>
      </div>

      <nav className="flex-1 px-4 pb-4 space-y-6">
        {MENU_ITEMS.map((grupo) => (
          <div key={grupo.seccion}>
            <h3 className="text-[10px] font-bold uppercase tracking-widest text-zinc-400 mb-2 px-3">
              {grupo.seccion}
            </h3>
            <ul className="space-y-1">
              {grupo.items.map((item) => {
                const isActive = pathname.startsWith(item.href);
                const Icon = item.icon;
                return (
                  <li key={item.name}>
                    <Link
                      href={item.href}
                      className={`flex items-center gap-3 px-3 py-2.5 rounded-xl transition-all text-sm font-medium ${
                        isActive
                          ? 'bg-allado-yellow text-black shadow-md'
                          : 'hover:bg-allado-card2 hover:text-white'
                      }`}
                    >
                      <Icon
                        className={`w-4 h-4 ${isActive ? 'text-black' : 'text-zinc-400'}`}
                      />
                      {item.name}
                    </Link>
                  </li>
                );
              })}
            </ul>
          </div>
        ))}
      </nav>

      <div className="p-4 border-t border-allado-border">
        <Link
          href="/login"
          onClick={(event) => {
            event.preventDefault();
            void logout();
          }}
          className="flex items-center gap-3 px-3 py-2.5 rounded-xl transition-all text-sm font-medium text-rose-400 hover:bg-rose-500/10"
        >
          <LogOut className="w-4 h-4" /> Cerrar Sesión
        </Link>
      </div>
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
