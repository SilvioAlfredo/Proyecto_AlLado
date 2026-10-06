'use client';

import { useState, useEffect } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { EmpleadorDTO } from '@/types/usuario';
import { empleadorService } from '@/features/usuario/services/empleadorService';

export default function EmpleadorPerfilPage() {
  const { user } = useAuth();
  const [perfil, setPerfil] = useState<EmpleadorDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [nombreEmpresa, setNombreEmpresa] = useState('');
  const [cuit, setCuit] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [sitioWeb, setSitioWeb] = useState('');

  useEffect(() => {
    cargarPerfil();
  }, []);

  const cargarPerfil = async () => {
    try {
      const data = await empleadorService.obtenerPerfil();
      setPerfil(data);
      setNombreEmpresa(data.perfilEmpresarial?.nombreEmpresa || '');
      setCuit(data.perfilEmpresarial?.cuit || '');
      setDescripcion(data.perfilEmpresarial?.descripcion || '');
      setSitioWeb(data.perfilEmpresarial?.sitioWeb || '');
    } catch (error) {
      console.error('Error al cargar perfil:', error);
    } finally {
      setLoading(false);
    }
  };

  const guardarPerfil = async () => {
    setGuardando(true);
    try {
      const actualizado = await empleadorService.actualizarPerfil({
        perfilEmpresarial: {
          nombreEmpresa,
          cuit,
          descripcion,
          sitioWeb,
        } as any,
      });
      setPerfil(actualizado);
    } catch (error) {
      console.error('Error al guardar perfil:', error);
    } finally {
      setGuardando(false);
    }
  };

  if (loading) {
    return <p className="text-zinc-500">Cargando perfil...</p>;
  }

  return (
    <div className="space-y-6 max-w-2xl">
      <h1 className="text-2xl font-extrabold text-white">Perfil Empresarial / Empleador</h1>

      <div className="bg-[#1a1a1c] p-6 rounded-2xl border border-[#2e2e33] space-y-4">
        <div>
          <label className="text-xs text-zinc-500 block uppercase font-bold mb-1">Correo Electrónico</label>
          <p className="text-white font-medium">{user?.email || 'Cargando...'}</p>
        </div>
        <div>
          <label className="text-xs text-zinc-500 block uppercase font-bold mb-1">Rol Asignado</label>
          <p className="text-white font-medium">{user?.roles?.join(', ')}</p>
        </div>
      </div>

      <div className="bg-[#1a1a1c] p-6 rounded-2xl border border-[#2e2e33] space-y-4">
        <h2 className="text-lg font-bold text-white">Información Empresarial</h2>

        <div>
          <label className="block text-sm font-medium text-zinc-300 mb-2">Nombre de la Empresa</label>
          <Input
            value={nombreEmpresa}
            onChange={(e) => setNombreEmpresa(e.target.value)}
            placeholder="Ej: Reparaciones Eléctricas SRL"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-zinc-300 mb-2">CUIT</label>
          <Input
            value={cuit}
            onChange={(e) => setCuit(e.target.value)}
            placeholder="Ej: 30-12345678-9"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-zinc-300 mb-2">Descripción</label>
          <textarea
            rows={4}
            value={descripcion}
            onChange={(e) => setDescripcion(e.target.value)}
            className="w-full rounded-xl bg-[#0b0b0c] border border-[#2e2e33] p-3 text-sm text-white placeholder-zinc-500 focus:outline-none focus:border-[#ffc107] focus:ring-1 focus:ring-[#ffc107] transition"
            placeholder="Describe tu empresa y servicios..."
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-zinc-300 mb-2">Sitio Web</label>
          <Input
            value={sitioWeb}
            onChange={(e) => setSitioWeb(e.target.value)}
            placeholder="https://www.ejemplo.com"
          />
        </div>

        <Button onClick={guardarPerfil} disabled={guardando} className="bg-[#ffc107] text-black hover:bg-[#eab308]">
          {guardando ? 'Guardando...' : 'Guardar Cambios'}
        </Button>
      </div>
    </div>
  );
}
