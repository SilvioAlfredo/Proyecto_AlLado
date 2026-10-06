import * as React from 'react';
import { cn } from '@/lib/utils';

export function Card({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn(
        'rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] shadow-sm',
        className
      )}
      {...props}
    />
  );
}
