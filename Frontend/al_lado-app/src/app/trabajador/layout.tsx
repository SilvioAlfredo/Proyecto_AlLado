import { Sidebar } from '@/components/layout/Sidebar';
import { BottomNav } from '@/components/layout/BottomNav';

const trabajadorNav = [
  { label: 'Mi Panel', href: '/trabajador/dashboard' },
  { label: 'Oportunidades', href: '/trabajador/oportunidades' },
  { label: 'Mis Postulaciones', href: '/trabajador/postulaciones' },
  { label: 'Mi Perfil', href: '/trabajador/perfil' },
];

export default function TrabajadorLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-[calc(100vh-5rem)]">
      <Sidebar items={trabajadorNav} />
      <div className="flex-1 p-4 md:p-6 pb-16 md:pb-6">{children}</div>
      <BottomNav items={trabajadorNav} />
    </div>
  );
}