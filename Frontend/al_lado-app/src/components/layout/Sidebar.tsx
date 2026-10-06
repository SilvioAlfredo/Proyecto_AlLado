'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/utils';

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
                  : 'text-zinc-400 hover:bg-[#26262a] hover:text-white'
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