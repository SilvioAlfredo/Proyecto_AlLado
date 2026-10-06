'use client';

import { RolUsuario } from '@/types/usuario';

interface RoleSelectorProps {
  selectedRole: RolUsuario;
  onSelectRole: (role: RolUsuario) => void;
}

export function RoleSelector({ selectedRole, onSelectRole }: RoleSelectorProps) {
  return (
    <div className="grid grid-cols-2 gap-3">
      <button
        type="button"
        onClick={() => onSelectRole('TRABAJADOR')}
        className={`p-4 rounded-xl border text-center font-medium transition ${
          selectedRole === 'TRABAJADOR'
            ? 'border-[#ffc107] bg-[#ffc107]/10 text-[#ffc107]'
            : 'border-[#2e2e33] bg-[#1a1a1c] text-zinc-400 hover:border-zinc-600'
        }`}
      >
        <span className="block text-2xl mb-1">🔧</span>
        Ofrezco Servicios
      </button>
      <button
        type="button"
        onClick={() => onSelectRole('EMPLEADOR')}
        className={`p-4 rounded-xl border text-center font-medium transition ${
          selectedRole === 'EMPLEADOR'
            ? 'border-[#ffc107] bg-[#ffc107]/10 text-[#ffc107]'
            : 'border-[#2e2e33] bg-[#1a1a1c] text-zinc-400 hover:border-zinc-600'
        }`}
      >
        <span className="block text-2xl mb-1">💼</span>
        Busco Personal
      </button>
    </div>
  );
}