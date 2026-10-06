'use client';

import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { formatDate } from '@/lib/utils';

export interface ModeracionItem {
  id: string;
  tipo: string;
  contenido: string;
  motivoReporte: string;
  createdAt: string;
}

interface ModeracionTablaProps {
  items: ModeracionItem[];
  onAccion: (id: string, accion: 'APROBAR' | 'ELIMINAR') => void;
}

export function ModeracionTabla({ items, onAccion }: ModeracionTablaProps) {
  if (items.length === 0) {
    return <p className="text-gray-500 text-sm py-4">No hay publicaciones pendientes de moderación.</p>;
  }

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Tipo</TableHead>
          <TableHead>Contenido</TableHead>
          <TableHead>Motivo</TableHead>
          <TableHead>Fecha</TableHead>
          <TableHead className="text-right">Acciones</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {items.map((item) => (
          <TableRow key={item.id}>
            <TableCell className="font-semibold">{item.tipo}</TableCell>
            <TableCell className="max-w-xs truncate">{item.contenido}</TableCell>
            <TableCell className="text-red-600">{item.motivoReporte}</TableCell>
            <TableCell>{formatDate(item.createdAt)}</TableCell>
            <TableCell className="text-right space-x-2">
              <Button size="sm" variant="outline" onClick={() => onAccion(item.id, 'APROBAR')}>
                Ignorar
              </Button>
              <Button size="sm" variant="destructive" onClick={() => onAccion(item.id, 'ELIMINAR')}>
                Eliminar
              </Button>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}