'use client';

import * as React from 'react';
import { cn } from '@/lib/utils';

export interface FieldProps extends React.HTMLAttributes<HTMLDivElement> {
  label?: React.ReactNode;
  error?: string | null;
  hint?: React.ReactNode;
  children: React.ReactElement;
  className?: string;
}

/**
 * Campo de formulario con etiqueta, control, mensaje de error y texto de ayuda.
 *
 * El error se renderiza con `role="alert"` y el control lo referencia con
 * `aria-describedby` para accesibilidad (WCAG 1.3.1, 3.3.2).
 */
export function Field({
  label,
  error,
  hint,
  children,
  className,
  ...rest
}: FieldProps) {
  // useId genera un id estable entre renders (Math.random en render es impuro
  // y cambia en cada uno, rompiendo la asociación label/control).
  const idGenerado = React.useId();
  const id = (children.props as { id?: string }).id || idGenerado;

  return (
    <div className={cn('space-y-2', className)} {...rest}>
      {label && (
        <label className="text-xs font-medium text-zinc-300" htmlFor={id}>
          {label}
        </label>
      )}
      {React.cloneElement(
        children as React.ReactElement<{ id?: string } & React.HTMLAttributes<HTMLElement>>,
        {
          id,
          'aria-describedby': cn(
            error ? `${id}-error` : undefined,
            hint ? `${id}-hint` : undefined,
          ),
          ...(error ? { 'aria-invalid': true } : {}),
        },
      )}
      {error && (
        <p id={`${id}-error`} role="alert" className="text-xs text-red-400">
          {error}
        </p>
      )}
      {hint && <p id={`${id}-hint`} className="text-xs text-zinc-400">{hint}</p>}
    </div>
  );
}

Field.displayName = 'Field';
