import { Sidebar } from '@/components/layout/Sidebar';

const adminNav = [
  { label: 'Métricas', href: '/admin/dashboard' },
  { label: 'Usuarios', href: '/admin/usuarios' },
  { label: 'Publicaciones', href: '/admin/publicaciones' },
];

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-[calc(100vh-5rem)]">
      <Sidebar items={adminNav} />
      <div className="flex-1 p-6">{children}</div>
    </div>
  );
}