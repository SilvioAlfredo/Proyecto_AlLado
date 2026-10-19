import { cn } from '@/lib/utils';

export function PageHeader({
  eyebrow,
  title,
  subtitle,
  action,
}: {
  eyebrow?: string;
  title: string;
  subtitle?: string;
  action?: React.ReactNode;
}) {
  return (
    <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
      <div>
        {eyebrow && (
          <p className="text-[11px] font-bold uppercase tracking-[0.2em] text-[#ffc107]">{eyebrow}</p>
        )}
        <h1 className="text-2xl font-extrabold text-white md:text-3xl">{title}</h1>
        {subtitle && <p className="mt-1 text-sm text-zinc-400">{subtitle}</p>}
      </div>
      {action && <div className="shrink-0">{action}</div>}
    </div>
  );
}

export function StatCard({
  label,
  value,
  hint,
  highlight,
}: {
  label: string;
  value: string;
  hint?: string;
  highlight?: boolean;
}) {
  return (
    <div
      className={cn(
        'rounded-2xl border p-4',
        highlight ? 'border-[#ffc107] bg-[#fff3c4] text-black' : 'border-[#2e2e33] bg-[#1a1a1c]'
      )}
    >
      <p className={cn('text-[11px] font-bold uppercase tracking-wide', highlight ? 'text-black/60' : 'text-zinc-500')}>
        {label}
      </p>
      <p className={cn('mt-1 text-2xl font-extrabold', highlight ? 'text-black' : 'text-white')}>{value}</p>
      {hint && <p className={cn('mt-1 text-xs', highlight ? 'text-black/60' : 'text-zinc-500')}>{hint}</p>}
    </div>
  );
}

export function Tabs<T extends string>({
  options,
  value,
  onChange,
}: {
  options: { value: T; label: string }[];
  value: T;
  onChange: (v: T) => void;
}) {
  return (
    <div className="flex flex-wrap gap-2">
      {options.map((o) => (
        <button
          key={o.value}
          onClick={() => onChange(o.value)}
          className={cn(
            'rounded-full border px-4 py-1.5 text-sm font-semibold transition',
            value === o.value
              ? 'border-[#ffc107] bg-[#ffc107] text-black'
              : 'border-[#3a3a3f] text-zinc-300 hover:border-zinc-500'
          )}
        >
          {o.label}
        </button>
      ))}
    </div>
  );
}

export function Avatar({ nombre, size = 'md' }: { nombre: string; size?: 'sm' | 'md' | 'lg' }) {
  const initials = nombre
    .split(' ')
    .map((p) => p[0])
    .slice(0, 2)
    .join('')
    .toUpperCase();
  const cls = size === 'lg' ? 'h-16 w-16 text-xl' : size === 'sm' ? 'h-9 w-9 text-sm' : 'h-12 w-12 text-base';
  return (
    <span className={cn('flex shrink-0 items-center justify-center rounded-full bg-[#ff8c1a] font-extrabold text-black', cls)}>
      {initials || '?'}
    </span>
  );
}

export function Note({ children, tone = 'info' }: { children: React.ReactNode; tone?: 'info' | 'danger' }) {
  return (
    <div
      className={cn(
        'rounded-2xl border p-3 text-xs leading-relaxed',
        tone === 'danger'
          ? 'border-red-500/30 bg-red-500/10 text-red-300'
          : 'border-[#ffc107]/20 bg-[#3a2e12]/40 text-zinc-300'
      )}
    >
      ⓘ {children}
    </div>
  );
}

export function Empty({ mensaje }: { mensaje: string }) {
  return (
    <div className="rounded-2xl border border-dashed border-[#3a3a3f] p-10 text-center text-sm text-zinc-500">
      {mensaje}
    </div>
  );
}
