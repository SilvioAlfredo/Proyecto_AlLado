import { Sidebar } from '@/components/layout/Sidebar';
import { BottomNav } from '@/components/layout/BottomNav';

const empleadorNav = [
  { label: 'Dashboard', href: '/empleador/dashboard' },
  { label: 'Mis Ofertas', href: '/empleador/oportunidades' },
  { label: 'Candidatos', href: '/empleador/candidatos' },
  { label: 'Perfil Empresarial', href: '/empleador/perfil' },
];

export default function EmpleadorLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-[calc(100vh-5rem)]">
      <Sidebar items={empleadorNav} />
      <div className="flex-1 p-4 md:p-6 pb-16 md:pb-6">{children}</div>
      <BottomNav items={empleadorNav} />
    </div>
  );
}