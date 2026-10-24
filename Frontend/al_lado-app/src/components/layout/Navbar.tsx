'use client';

import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Bell } from 'lucide-react';
import { notificacionesService } from '@/features/notificaciones/services/notificacionesService';

export function Navbar() {
  const { user, userId, logout } = useAuth();
  const notices = useQuery({
    queryKey: ['notificaciones-count', userId],
    enabled: userId != null,
    queryFn: () => notificacionesService.noLeidas(userId!),
    staleTime: 30000,
  });
  const noLeidas = notices.data ?? 0;

  const getDashboardLink = () => {
    if (!user) return '/';
    if (user.roles?.includes('ROLE_ADMIN')) return '/admin/dashboard';
    if (user.roles?.includes('ROLE_EMPLEADOR')) return '/empleador/dashboard';
    if (user.roles?.includes('ROLE_TRABAJADOR')) return '/trabajador/dashboard';
    return '/';
  };

  return (
    <header className="border-b border-[#222] bg-[#0b0b0c] sticky top-0 z-30">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <Link href="/" className="flex items-center gap-2">
          <span className="flex h-9 w-9 items-center justify-center rounded-full bg-[#ffc107] text-black font-black text-lg">
            ◉
          </span>
          <span className="leading-none">
            <span className="block font-extrabold text-xl tracking-tight text-white">
              Al<span className="text-[#ffc107]">L</span>ado
            </span>
            <span className="block text-[10px] tracking-[0.2em] text-zinc-400">
              SERVICIOS LOCALES
            </span>
          </span>
        </Link>
        <nav
          aria-label="Navegación principal"
          className="flex items-center gap-2 sm:gap-4"
        >
          {user ? (
            <>
              <Link
                href="/"
                className="hidden sm:block text-sm text-zinc-400 hover:text-white transition"
              >
                Inicio
              </Link>
              <Link
                href="/foro"
                className="hidden sm:block text-sm text-zinc-400 hover:text-white transition"
              >
                Foro
              </Link>
              <Link
                href={
                  user?.roles.includes('ROLE_EMPLEADOR')
                    ? '/empleador/solicitudes/nueva'
                    : '/publicar'
                }
                className="hidden sm:inline-flex items-center rounded-full bg-[#ffc107] px-4 py-2 text-sm font-bold text-black hover:bg-[#eab308]"
              >
                + Publicar
              </Link>
            </>
          ) : (
            <>
              <Link
                href="/#oportunidades"
                className="hidden text-sm text-zinc-400 transition hover:text-white md:block"
              >
                Oportunidades
              </Link>
              <Link
                href="/#como-funciona"
                className="hidden text-sm text-zinc-400 transition hover:text-white lg:block"
              >
                Cómo funciona
              </Link>
            </>
          )}
          {user ? (
            <>
              <Link
                href={
                  user.roles?.includes('ROLE_ADMIN')
                    ? '/admin/reportes'
                    : user.roles?.includes('ROLE_EMPLEADOR')
                      ? '/empleador/notificaciones'
                      : '/trabajador/notificaciones'
                }
                className="relative rounded-full p-2 text-zinc-300 hover:text-[#ffc107]"
                aria-label={
                  user.roles.includes('ROLE_ADMIN')
                    ? 'Reportes de la comunidad'
                    : 'Notificaciones'
                }
              >
                <Bell size={18} />
                {noLeidas > 0 && (
                  <span className="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-[#ffc107] px-1 text-[10px] font-black text-black">
                    {noLeidas > 9 ? '9+' : noLeidas}
                  </span>
                )}
              </Link>
              <Link
                href={getDashboardLink()}
                className="text-sm font-medium text-zinc-300 hover:text-[#ffc107]"
              >
                Mi Panel
              </Link>
              <span className="hidden md:inline text-sm text-zinc-500">
                | {user.email}
              </span>
              <Button variant="ghost" size="sm" onClick={logout}>
                Salir
              </Button>
            </>
          ) : (
            <>
              <Link href="/login">
                <Button variant="ghost" size="sm">
                  Ingresar
                </Button>
              </Link>
              <Link href="/register">
                <Button size="sm">Sumate</Button>
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
