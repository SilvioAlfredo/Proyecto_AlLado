'use client';

import { useState } from 'react';
import { useComunidad } from '@/features/comunidad/hooks/useComunidad';
import { foroService } from '@/features/comunidad/services/moderacionService';
import { PublicacionForoDTO } from '@/types/comunidad';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

export default function ForoPage() {
  const { publicaciones, loading } = useComunidad();
  const [autorId, setAutorId] = useState('');
  const [filtradas, setFiltradas] = useState<PublicacionForoDTO[] | null>(null);

  if (loading) return <p>Cargando publicaciones del foro...</p>;
  const lista = filtradas ?? publicaciones;

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-extrabold text-white">Foro Comunitario</h1>
      <div className="flex gap-2">
        <Input placeholder="Filtrar por ID de autor..." value={autorId} onChange={(e) => setAutorId(e.target.value)} />
        <Button
          variant="outline"
          onClick={async () => {
            if (!autorId) {
              setFiltradas(null);
              return;
            }
            setFiltradas(await foroService.porAutor(Number(autorId)).catch(() => []));
          }}
        >
          Filtrar
        </Button>
      </div>
      <div className="space-y-4">
        {lista.map((post) => (
          <div key={post.id} className="border border-[#2e2e33] rounded-2xl p-5 bg-[#1a1a1c] space-y-3">
            <h3 className="font-bold text-lg text-white">{post.titulo}</h3>
            <p className="text-zinc-400 text-sm">{post.contenido}</p>
            <div className="text-xs text-zinc-500">
              Publicado por: {post.autor?.nombre || 'Anónimo'}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
