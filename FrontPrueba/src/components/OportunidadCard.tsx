import type { MouseEventHandler } from "react";

type OportunidadCardProps = {
  nombre: string;
  titulo: string;
  onClick: MouseEventHandler<HTMLButtonElement>;
};

export default function OportunidadCard({ nombre, titulo, onClick }: OportunidadCardProps) {
  return (
    <button className="publicacion-card publicacion-card-destacada" type="button" onClick={onClick}>
      <span className="publicacion-avatar" />
      <span className="publicacion-info">
        <strong>{nombre}</strong>
        <small>{titulo}</small>
      </span>
    </button>
  );
}
