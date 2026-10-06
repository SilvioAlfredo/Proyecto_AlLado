import { useState } from 'react';
import { api } from '../api/client';

interface FavoriteButtonProps {
  trabajadorId: number;
  ofertaId?: number;
  isFavorite?: boolean;
  onToggle: (isFavorite: boolean) => void;
}

export function FavoriteButton({ trabajadorId, ofertaId, isFavorite = false, onToggle }: FavoriteButtonProps) {
  const [isToggled, setIsToggled] = useState(isFavorite);

  const handleClick = async () => {
    try {
      if (isToggled) {
        // Remove from favorites
        await api.delete(`/favoritos/empleador/${trabajadorId}/trabajador/${trabajadorId}`);
        setIsToggled(false);
        onToggle(false);
      } else {
        // Add to favorites
        await api.post(`/favoritos/empleador/${trabajadorId}/trabajador/${trabajadorId}`, {});
        setIsToggled(true);
        onToggle(true);
      }
    } catch (err) {
      console.error('Error al actualizar favoritos:', err);
      alert('Error al actualizar favoritos');
    }
  };

  return (
    <button
      className="favorite-btn"
      type="button"
      aria-label={isToggled ? "Quitar de favoritos" : "Añadir a favoritos"}
      onClick={handleClick}
    >
      {isToggled ? ⭐ : ♡}
    </button>
  );
}