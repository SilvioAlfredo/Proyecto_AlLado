'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/utils';
import { SidebarItem } from './Sidebar';

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
                isActive ? 'text-[#ffc107] font-bold' : 'text-zinc-500'
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