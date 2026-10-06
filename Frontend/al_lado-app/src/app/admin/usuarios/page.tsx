'use client';

import { useState, useEffect } from 'react';
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import { empleadorService } from '@/features/usuario/services/empleadorService';
import { TrabajadorDTO, EmpleadorDTO } from '@/types/usuario';

export default function AdminUsuariosPage() {
  const [trabajadores, setTrabajadores] = useState<TrabajadorDTO[]>([]);
  const [empleadores, setEmpleadores] = useState<EmpleadorDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filtro, setFiltro] = useState('');

  useEffect(() => {
    const cargar = async () => {
      setLoading(true);
      try {
        const [t, e] = await Promise.all([
          trabajadorService.obtenerTodos(),
          empleadorService.obtenerTodos(),
        ]);
        setTrabajadores(t);
        setEmpleadores(e);
      } catch (err: any) {
        setError(err?.response?.data?.message || 'Error al cargar usuarios');
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, []);

  const f = filtro.toLowerCase();
  const tFil = trabajadores.filter((t) => !f || t.nombre.toLowerCase().includes(f) || t.email.toLowerCase().includes(f));
  const eFil = empleadores.filter((e) => !f || e.nombre.toLowerCase().includes(f) || e.email.toLowerCase().includes(f));

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-extrabold text-white">Gestión de Usuarios</h1>

      {error && (
        <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-red-400">
          <p className="font-medium">Error: {error}</p>
        </div>
      )}

      <div className="max-w-md">
        <Input placeholder="Buscar por nombre o email..." value={filtro} onChange={(e) => setFiltro(e.target.value)} />
      </div>

      <div className="bg-[#1a1a1c] rounded-2xl border border-[#2e2e33] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-[#2e2e33]">
                <th className="text-left p-4 text-sm font-medium text-zinc-500">ID</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">Nombre</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">Email</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">Rol</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">Estado</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {tFil.map((t) => (
                <tr key={t.id} className="border-b border-[#2e2e33] last:border-0">
                  <td className="p-4 text-zinc-400">{t.id}</td>
                  <td className="p-4 text-white font-medium">{t.nombre}</td>
                  <td className="p-4 text-zinc-300">{t.email}</td>
                  <td className="p-4"><Badge variant="default">TRABAJADOR</Badge></td>
                  <td className="p-4 text-zinc-300">{t.suscripcionActiva ? 'Activo' : 'Inactivo'}</td>
                  <td className="p-4">
                    <Button size="sm" variant="outline" className="border-[#2e2e33] text-zinc-300 hover:border-[#ffc107] hover:text-[#ffc107]">Bloquear</Button>
                  </td>
                </tr>
              ))}
              {eFil.map((e) => (
                <tr key={e.id} className="border-b border-[#2e2e33] last:border-0">
                  <td className="p-4 text-zinc-400">{e.id}</td>
                  <td className="p-4 text-white font-medium">{e.nombre}</td>
                  <td className="p-4 text-zinc-300">{e.email}</td>
                  <td className="p-4"><Badge variant="default">EMPLEADOR</Badge></td>
                  <td className="p-4 text-zinc-300">{e.perfilCompleto ? 'Completo' : 'Incompleto'}</td>
                  <td className="p-4">
                    <Button size="sm" variant="outline" className="border-[#2e2e33] text-zinc-300 hover:border-[#ffc107] hover:text-[#ffc107]">Bloquear</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {loading && <p className="text-zinc-500">Cargando usuarios...</p>}
    </div>
  );
}
