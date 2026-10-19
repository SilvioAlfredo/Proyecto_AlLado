'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import { empleadorService } from '@/features/usuario/services/empleadorService';
import { usuariosService } from '@/features/usuario/services/usuariosService';
import { TrabajadorDTO, EmpleadorDTO } from '@/types/usuario';

export default function AdminUsuariosPage() {
  const [trabajadores, setTrabajadores] = useState<TrabajadorDTO[]>([]);
  const [empleadores, setEmpleadores] = useState<EmpleadorDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filtro, setFiltro] = useState('');

  const recargar = async () => {
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

  useEffect(() => {
    recargar();
  }, []);

  const alternarBloqueo = async (
    id: number | undefined,
    nombre: string,
    habilitado?: boolean,
  ) => {
    if (id == null) {
      return;
    }
    try {
      if (habilitado === false) {
        await usuariosService.habilitar(id);
      } else {
        if (!confirm(`Bloquear a ${nombre}? No podrá ingresar.`)) {
          return;
        }
        await usuariosService.deshabilitar(id);
      }
      await recargar();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'No se pudo cambiar el estado.');
    }
  };

  const f = filtro.toLowerCase();
  const tFil = trabajadores.filter(
    (t) => !f || t.nombre.toLowerCase().includes(f) || t.email.toLowerCase().includes(f),
  );
  const eFil = empleadores.filter(
    (e) => !f || e.nombre.toLowerCase().includes(f) || e.email.toLowerCase().includes(f),
  );

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-extrabold text-white">Gestión de Usuarios</h1>

      {error && (
        <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-red-400">
          <p className="font-medium">Error: {error}</p>
        </div>
      )}

      <div className="max-w-md">
        <Input
          placeholder="Buscar por nombre o email..."
          value={filtro}
          onChange={(e) => setFiltro(e.target.value)}
        />
      </div>

      <div className="bg-[#1a1a1c] rounded-2xl border border-[#2e2e33] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-[#2e2e33]">
                <th className="text-left p-4 text-sm font-medium text-zinc-500">ID</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">
                  Nombre
                </th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">Email</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">Rol</th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">
                  Teléfono
                </th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">
                  Estado
                </th>
                <th className="text-left p-4 text-sm font-medium text-zinc-500">
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody>
              {tFil.map((t) => (
                <tr key={t.id} className="border-b border-[#2e2e33] last:border-0">
                  <td className="p-4 text-zinc-400">{t.id}</td>
                  <td className="p-4 text-white font-medium">{t.nombre}</td>
                  <td className="p-4 text-zinc-300">{t.email}</td>
                  <td className="p-4">
                    <Badge variant="default">TRABAJADOR</Badge>
                  </td>
                  <td className="p-4 text-zinc-300">{t.telefono || '—'}</td>
                  <td className="p-4">
                    <Badge variant={t.habilitado === false ? 'destructive' : 'default'}>
                      {t.habilitado === false ? 'BLOQUEADO' : 'ACTIVO'}
                    </Badge>
                  </td>
                  <td className="p-4">
                    <div className="flex gap-2">
                      <Link href={`/admin/usuarios/${t.id}`}>
                        <Button
                          size="sm"
                          variant="outline"
                          className={
                            'border-[#2e2e33] text-zinc-300 hover:border-[#ffc107] ' +
                            'hover:text-[#ffc107]'
                          }
                        >
                          Ver
                        </Button>
                      </Link>
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => alternarBloqueo(t.id, t.nombre, t.habilitado)}
                        className={
                          'border-[#2e2e33] text-zinc-300 hover:border-[#ffc107] ' +
                          'hover:text-[#ffc107]'
                        }
                      >
                        {t.habilitado === false ? 'Desbloquear' : 'Bloquear'}
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
              {eFil.map((e) => (
                <tr key={e.id} className="border-b border-[#2e2e33] last:border-0">
                  <td className="p-4 text-zinc-400">{e.id}</td>
                  <td className="p-4 text-white font-medium">{e.nombre}</td>
                  <td className="p-4 text-zinc-300">{e.email}</td>
                  <td className="p-4">
                    <Badge variant="default">EMPLEADOR</Badge>
                  </td>
                  <td className="p-4 text-zinc-300">{e.telefono || '—'}</td>
                  <td className="p-4">
                    <Badge variant={e.habilitado === false ? 'destructive' : 'default'}>
                      {e.habilitado === false ? 'BLOQUEADO' : 'ACTIVO'}
                    </Badge>
                  </td>
                  <td className="p-4">
                    <div className="flex gap-2">
                      <Link href={`/admin/usuarios/${e.id}`}>
                        <Button
                          size="sm"
                          variant="outline"
                          className={
                            'border-[#2e2e33] text-zinc-300 hover:border-[#ffc107] ' +
                            'hover:text-[#ffc107]'
                          }
                        >
                          Ver
                        </Button>
                      </Link>
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => alternarBloqueo(e.id, e.nombre, e.habilitado)}
                        className={
                          'border-[#2e2e33] text-zinc-300 hover:border-[#ffc107] ' +
                          'hover:text-[#ffc107]'
                        }
                      >
                        {e.habilitado === false ? 'Desbloquear' : 'Bloquear'}
                      </Button>
                    </div>
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
