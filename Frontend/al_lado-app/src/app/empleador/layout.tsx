import { EmpleadorShell } from '@/features/empleador/components/EmpleadorShell';
export default function EmpleadorLayout({ children }: { children: React.ReactNode }) {
  return <EmpleadorShell>{children}</EmpleadorShell>;
}
