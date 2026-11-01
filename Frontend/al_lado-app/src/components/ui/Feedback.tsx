'use client';

import * as React from 'react';
import { cn } from '@/lib/utils';

interface FeedbackProps {
  children: React.ReactNode;
  variant?: 'success' | 'error' | 'info';
  className?: string;
}

/**
 * Mensaje de feedback único para formularios y pantallas.
 *
 * - success: verde (operación exitosa)
 * - error: rojo (operación fallida)
 *   info: azul (información neutra)
 */
export function Feedback({
  children,
  variant = 'info',
  className,
}: FeedbackProps) {
  const base =
    'rounded-xl border p-4 text-sm flex items-start gap-3';
  const variants: Record<string, string> = {
    success:
      'border-emerald-400/20 bg-emerald-400/10 text-emerald-300',
    error: 'border-red-400/20 bg-red-400/10 text-red-300',
    info: 'border-zinc-400/20 bg-zinc-400/10 text-zinc-300',
  };

  return (
    <div className={cn(base, variants[variant], className)}>{children}</div>
  );
}

Feedback.displayName = 'Feedback';
