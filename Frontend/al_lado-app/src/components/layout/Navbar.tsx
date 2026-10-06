'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { Button } from '@/components/ui/button';

export function Navbar() {
  const { user, logout } = useAuth();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

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
            <span className="block text-[10px] tracking-[0.2em] text-zinc-500">
              SERVICIOS LOCALES
            </span>
          </span>
        </Link>
        <nav className="flex items-center gap-2 sm:gap-4">
          <Link href="/" className="hidden sm:block text-sm text-zinc-400 hover:text-white transition">
            Inicio
          </Link>
          <Link href="/foro" className="hidden sm:block text-sm text-zinc-400 hover:text-white transition">
            Foro
          </Link>
          <Link href="/publicar" className="hidden sm:inline-flex items-center rounded-full bg-[#ffc107] px-4 py-2 text-sm font-bold text-black hover:bg-[#eab308]">
            + Publicar
          </Link>
          {mounted && user ? (
            <>
              <Link href={getDashboardLink()} className="text-sm font-medium text-zinc-300 hover:text-[#ffc107]">
                Mi Panel
              </Link>
              <span className="hidden md:inline text-sm text-zinc-500">| {user.email}</span>
              <Button variant="ghost" size="sm" onClick={logout}>
                Salir
              </Button>
            </>
          ) : (
            <>
              <Link href="/login">
                <Button variant="ghost" size="sm">Ingresar</Button>
              </Link>
              <Link href="/register">
                <Button size="sm">Registrarse</Button>
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}