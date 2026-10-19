'use client';

import { CambiarPasswordForm } from '@/features/auth/components/CambiarPasswordForm';
import { PageHeader } from '@/components/ui/shared';

export default function SeguridadEmpleadorPage() {
  return (
    <div className="space-y-6">
      <PageHeader eyebrow="Mi cuenta" title="Seguridad" subtitle="POST /auth/password · POST /auth/logout" />
      <CambiarPasswordForm />
    </div>
  );
}
