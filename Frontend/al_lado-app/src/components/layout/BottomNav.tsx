'use client';

import { cn } from '@/lib/utils';
import type { SidebarItem } from './Sidebar';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { Activity, Briefcase, FileText, Users } from 'lucide-react';

const BOTTOM_NAV_ITEMS = [
  { name: 'Trabajos', href: '/trabajador/oportunidades', icon: Briefcase },
  { name: 'Postulaciones', href: '/trabajador/postulaciones', icon: FileText },
  { name: 'Actividad', href: '/trabajador/actividad', icon: Activity },
  { name: 'Comunidad', href: '/trabajador/comunidad', icon: Users },
];

export default function TrabajadorBottomNav() {
  const pathname = usePathname();

  return (
    <nav className="md:hidden fixed bottom-0 left-0 right-0 bg-allado-card border-t border-allado-border z-50 px-2 sm:px-4 py-2 flex justify-between items-center shadow-[0_-2px_10px_rgba(0,0,0,0.05)]">
      {BOTTOM_NAV_ITEMS.map((item) => {
        const isActive = pathname.startsWith(item.href);
        const Icon = item.icon;

        return (
          <Link
            key={item.name}
            href={item.href}
            className={`flex flex-col items-center justify-center w-full py-1 gap-1 transition-colors ${
              isActive ? 'text-allado-yellow' : 'text-zinc-400 hover:text-white'
            }`}
          >
            <Icon className={`w-5 h-5 ${isActive ? 'fill-allado-yellow/10' : ''}`} />
            <span className="text-[10px] font-medium">{item.name}</span>
          </Link>
        );
      })}
    </nav>
  );
}
interface BottomNavProps {
  items: SidebarItem[];
}

export function BottomNav({ items }: BottomNavProps) {
  const pathname = usePathname();

  return (
    <nav className="fixed bottom-0 left-0 right-0 border-t border-[#222] bg-[#0b0b0c] md:hidden z-30">
      <div className="flex justify-around py-2">
        {items.map((item) => {
          const isActive = pathname === item.href;
          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                'flex flex-col items-center text-xs py-1 px-2 font-medium transition-colors',
                isActive ? 'text-[#ffc107] font-bold' : 'text-zinc-500',
              )}
            >
              {item.label}
            </Link>
          );
        })}
      </div>
    </nav>
  );
}
