import * as React from 'react';
import { cn } from '@/lib/utils';

export function Select({ className, ...props }: React.SelectHTMLAttributes<HTMLSelectElement>) {
  return (
    <select
      className={cn(
        'flex h-11 w-full rounded-full border border-[#3a3a3f] bg-[#26262a] px-4 text-sm text-white focus:outline-none focus:ring-2 focus:ring-[#ffc107]',
        className
      )}
      {...props}
    />
  );
}
