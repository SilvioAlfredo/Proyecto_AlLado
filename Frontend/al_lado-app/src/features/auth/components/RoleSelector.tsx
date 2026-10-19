'use client';

import { BriefcaseBusiness, Check, Wrench } from 'lucide-react';
import { RolUsuario } from '@/types/usuario';

interface RoleSelectorProps {
  selectedRole: RolUsuario;
  onSelectRole: (role: RolUsuario) => void;
}

export function RoleSelector({ selectedRole, onSelectRole }: RoleSelectorProps) {
  return (
    <div className="grid grid-cols-1 gap-3 min-[400px]:grid-cols-2">
      {[
        {
          role: 'TRABAJADOR' as const,
          title: 'Quiero trabajar',
          description: 'Ofrecé tus servicios y encontrá oportunidades.',
          icon: Wrench,
        },
        {
          role: 'EMPLEADOR' as const,
          title: 'Quiero contratar',
          description: 'Publicá trabajos y encontrá profesionales.',
          icon: BriefcaseBusiness,
        },
      ].map((item) => (
        <button
          key={item.role}
          type="button"
          onClick={() => onSelectRole(item.role)}
          aria-pressed={selectedRole === item.role}
          className={`relative rounded-2xl border p-4 text-left transition focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-allado-yellow disabled:opacity-50 ${selectedRole === item.role ? 'border-allado-yellow bg-allado-yellow/5' : 'border-allado-border bg-allado-bg/50 hover:border-zinc-500'}`}
        >
          <div className="mb-3 flex items-center justify-between">
            <item.icon
              size={21}
              className={
                selectedRole === item.role ? 'text-allado-yellow' : 'text-zinc-400'
              }
            />
            <span
              className={`flex h-4 w-4 items-center justify-center rounded-full border ${selectedRole === item.role ? 'border-allado-yellow bg-allado-yellow text-black' : 'border-zinc-600'}`}
            >
              {selectedRole === item.role && <Check size={11} strokeWidth={3} />}
            </span>
          </div>
          <span className="block text-sm font-bold text-white">{item.title}</span>
          <span className="mt-1.5 block text-xs leading-relaxed text-zinc-400">
            {item.description}
          </span>
        </button>
      ))}
    </div>
  );
}
