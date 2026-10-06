'use client';

import { useComunidad } from '@/features/comunidad/hooks/useComunidad';

export default function ForoPage() {
  const { publicaciones, loading } = useComunidad();

  if (loading) return <p>Cargando publicaciones del foro...</p>;

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-extrabold text-white">Foro Comunitario</h1>
      <div className="space-y-4">
        {publicaciones.map((post) => (
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
