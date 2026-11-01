'use client';

import * as React from 'react';
import { cn } from '@/lib/utils';

export interface TextareaProps
  extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  error?: boolean;
}

/**
 * Área de texto con soporte de estado de error (estilo consistente con Input).
 */
export const Textarea = React.forwardRef<HTMLTextAreaElement, TextareaProps>(
  ({ className, error, ...props }, ref) => {
    return (
      <textarea
        ref={ref}
        className={cn(
          'flex h-20 w-full rounded-xl border border-[#3a3a3f] bg-[#26262a] px-4 py-3 text-sm text-white placeholder:text-zinc-400 focus:outline-none focus:ring-2 focus:ring-[#ffc107] focus:border-transparent disabled:cursor-not-allowed disabled:opacity-50',
          error && 'border-red-500 focus:ring-red-500',
          className
        )}
        {...props}
      />
    );
  }
);
Textarea.displayName = 'Textarea';
