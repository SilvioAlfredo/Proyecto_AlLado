import { PublicacionForoDTO } from '@/types/comunidad';
import { formatDate } from '@/lib/utils';

interface PostCardProps {
  post: PublicacionForoDTO;
}

export function PostCard({ post }: PostCardProps) {
  return (
    <div className="border border-[#2e2e33] rounded-2xl p-5 bg-[#1a1a1c] space-y-3">
      <div className="flex justify-between items-start">
        <h3 className="font-bold text-lg text-white">{post.titulo}</h3>
        {post.fechaCreacion && (
          <span className="text-xs text-zinc-500">{formatDate(post.fechaCreacion)}</span>
        )}
      </div>
      <p className="text-zinc-400 text-sm leading-relaxed">{post.contenido}</p>
      <div className="pt-3 text-xs text-zinc-500 border-t border-[#2e2e33] flex justify-between">
        <span>Publicado por: <strong className="text-zinc-300">{post.autor?.nombre || 'Anónimo'}</strong></span>
        {post.categoria && <span className="bg-[#26262a] px-2 py-0.5 rounded text-zinc-300">{post.categoria}</span>}
      </div>
    </div>
  );
}