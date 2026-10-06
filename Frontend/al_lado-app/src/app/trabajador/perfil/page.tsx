'use client';

import { useState, useEffect } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { TrabajadorDTO } from '@/types/usuario';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';

export default function TrabajadorPerfilPage() {
  const { user } = useAuth();
  const [perfil, setPerfil] = useState<TrabajadorDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [biografia, setBiografia] = useState('');
  const [certificados, setCertificados] = useState('');
  const [sector, setSector] = useState('');

  useEffect(() => {
    cargarPerfil();
  }, []);

  const cargarPerfil = async () => {
    try {
      const data = await trabajadorService.obtenerPerfil();
      setPerfil(data);
      setBiografia(data.biografia || '');
      setCertificados(data.certificados || '');
      setSector(data.sector || '');
    } catch (error) {
      console.error('Error al cargar perfil:', error);
    } finally {
      setLoading(false);
    }
  };

  const guardarPerfil = async () => {
    setGuardando(true);
    try {
      const actualizado = await trabajadorService.actualizarPerfil({
        biografia,
        certificados,
        sector,
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
      <h1 className="text-2xl font-extrabold text-white">Mi Perfil Profesional</h1>
      
      <div className="bg-[#1a1a1c] p-6 rounded-2xl border border-[#2e2e33] space-y-4">
        <div>
          <label className="text-xs text-zinc-500 block uppercase font-bold mb-1">Correo Electrónico</label>
          <p className="text-white font-medium">{user?.email || 'Cargando...'}</p>
        </div>
        <div>
          <label className="text-xs text-zinc-500 block uppercase font-bold mb-1">Roles de Cuenta</label>
          <p className="text-white font-medium">{user?.roles?.join(', ')}</p>
        </div>
        <div>
          <label className="text-xs text-zinc-500 block uppercase font-bold mb-1">Especialidades</label>
          <div className="flex flex-wrap gap-2 mt-2">
            {perfil?.especialidades?.map((esp) => (
              <Badge key={esp.id} variant="default">{esp.nombre}</Badge>
            )) || <span className="text-zinc-500">Sin especialidades</span>}
          </div>
        </div>
      </div>

      <div className="bg-[#1a1a1c] p-6 rounded-2xl border border-[#2e2e33] space-y-4">
        <h2 className="text-lg font-bold text-white">Información Profesional</h2>
        
        <div>
          <label className="block text-sm font-medium text-zinc-300 mb-2">Biografía</label>
          <textarea
            rows={4}
            value={biografia}
            onChange={(e) => setBiografia(e.target.value)}
            className="w-full rounded-xl bg-[#0b0b0c] border border-[#2e2e33] p-3 text-sm text-white placeholder-zinc-500 focus:outline-none focus:border-[#ffc107] focus:ring-1 focus:ring-[#ffc107] transition"
            placeholder="Describe tu experiencia y habilidades..."
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-zinc-300 mb-2">Certificados</label>
          <textarea
            rows={3}
            value={certificados}
            onChange={(e) => setCertificados(e.target.value)}
            className="w-full rounded-xl bg-[#0b0b0c] border border-[#2e2e33] p-3 text-sm text-white placeholder-zinc-500 focus:outline-none focus:border-[#ffc107] focus:ring-1 focus:ring-[#ffc107] transition"
            placeholder="Lista tus certificados y títulos..."
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-zinc-300 mb-2">Sector / Zona</label>
          <Input
            value={sector}
            onChange={(e) => setSector(e.target.value)}
            placeholder="Ej: Barrio Centro, Zona Norte"
          />
        </div>

        <Button onClick={guardarPerfil} disabled={guardando} className="bg-[#ffc107] text-black hover:bg-[#eab308]">
          {guardando ? 'Guardando...' : 'Guardar Cambios'}
        </Button>
      </div>

      {perfil?.portafolioItems && perfil.portafolioItems.length > 0 && (
        <div className="bg-[#1a1a1c] p-6 rounded-2xl border border-[#2e2e33] space-y-4">
          <h2 className="text-lg font-bold text-white">Portafolio</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {perfil.portafolioItems.map((item) => (
              <div key={item.id} className="border border-[#2e2e33] rounded-xl p-4 bg-[#0b0b0c]">
                <h3 className="font-medium text-white">{item.titulo}</h3>
                <p className="text-sm text-zinc-400">{item.descripcion}</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
