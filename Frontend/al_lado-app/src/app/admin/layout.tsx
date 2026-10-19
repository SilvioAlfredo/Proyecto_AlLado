import { AdminAccess } from '@/features/admin/components/AdminAccess';
import { AdminNavigation } from '@/features/admin/components/AdminNavigation';

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  return (
    <AdminAccess>
      <div className="flex min-h-[calc(100vh-5rem)] flex-col md:flex-row">
        <AdminNavigation />
        <div className="min-w-0 flex-1 p-4 md:p-6">{children}</div>
      </div>
    </AdminAccess>
  );
}
