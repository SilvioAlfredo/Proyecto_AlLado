'use client';

export function useTooltip() {
  return { show: () => {}, hide: () => {} };
}

export function Tooltip({ children }: { children: React.ReactNode }) {
  return <>{children}</>;
}
