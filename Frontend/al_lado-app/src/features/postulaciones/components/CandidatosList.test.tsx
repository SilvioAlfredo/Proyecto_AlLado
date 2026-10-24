import { render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { CandidatosList } from './CandidatosList';
import type { PostulacionDTO } from '@/types/trabajo';

/**
 * El empleador tiene que poder contactar a quien se postuló.
 *
 * AL-SEC-006 quitó los datos de contacto de las lecturas públicas del directorio,
 * pero la lista de postulantes de su propia oferta sí los trae: son parte de la
 * relación laboral. Este test fija ese contrato.
 */

vi.mock('next/link', () => ({
  default: ({ children, href }: { children: React.ReactNode; href: string }) => (
    <a href={href}>{children}</a>
  ),
}));

function postulacion(autor: Partial<PostulacionDTO['autor']>): PostulacionDTO {
  return {
    id: 1,
    titulo: 'Propuesta',
    contenido: 'Puedo hacerlo',
    estado: 'PENDIENTE',
    remuneracionPretendida: 1000,
    autor: { id: 3, nombre: 'Demo Trabajador', ...autor } as PostulacionDTO['autor'],
    oportunidadLaboral: {
      id: 7,
      titulo: 'Oferta',
      contenido: '',
      ubicacion: '',
      modalidad: '',
      rangoSalarial: '',
      activa: true,
      fechaCreacion: '2026-10-22T00:00:00',
    },
  };
}

describe('CandidatosList', () => {
  it('muestra el email del postulante para poder contactarlo', () => {
    render(
      <CandidatosList
        postulaciones={[postulacion({ email: 'trabajador@mail.com' })]}
        onSeleccionar={vi.fn()}
        onRechazar={vi.fn()}
      />,
    );

    expect(screen.getByText('trabajador@mail.com')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'trabajador@mail.com' })).toHaveAttribute(
      'href',
      'mailto:trabajador@mail.com',
    );
  });

  it('no muestra email si el postulante no lo tiene', () => {
    render(
      <CandidatosList
        postulaciones={[postulacion({})]}
        onSeleccionar={vi.fn()}
        onRechazar={vi.fn()}
      />,
    );

    expect(screen.getByText('Demo Trabajador')).toBeInTheDocument();
    expect(screen.queryByText(/@mail\.com/)).not.toBeInTheDocument();
  });

  it('avisa cuando no hay postulantes', () => {
    render(<CandidatosList postulaciones={[]} onSeleccionar={vi.fn()} onRechazar={vi.fn()} />);

    expect(screen.getByText(/No hay postulantes/i)).toBeInTheDocument();
  });
});
