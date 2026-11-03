import { beforeEach, describe, expect, it, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { OportunidadFormModal } from './OportunidadFormModal';
import { oportunidadesService } from '../services/oportunidadService';
import type { OportunidadLaboralDTO } from '@/types/trabajo';

/**
 * HU-01: Empleador publica ofertas de trabajo.
 *
 * Pruebas del formulario de publicación de ofertas: envío de datos,
 * validación de campos requeridos y manejo de errores.
 */
vi.mock('../services/oportunidadService', () => ({
  oportunidadesService: { crear: vi.fn() },
}));

// El autor de la oferta sale de la sesión guardada en el navegador.
vi.mock('@/lib/usuarioRef', () => ({
  obtenerRefSesion: () => ({ id: 7, nombre: 'Ana', email: 'ana@mail.com' }),
}));

const mockCrear = vi.mocked(oportunidadesService.crear);

const ofertaCreada: OportunidadLaboralDTO = {
  id: 7,
  titulo: 'Oferta Test',
  contenido: 'Descripción test',
  ubicacion: 'Barrio',
  modalidad: 'PRESENCIAL',
  rangoSalarial: '$10000',
  activa: true,
};

function renderForm() {
  return render(<OportunidadFormModal isOpen onClose={vi.fn()} onSuccess={vi.fn()} />);
}

describe('OportunidadFormModal', () => {
  beforeEach(() => {
    mockCrear.mockReset();
  });

  it('envía los datos de la oferta al crear', async () => {
    mockCrear.mockResolvedValue(ofertaCreada);

    renderForm();

    fireEvent.change(screen.getByLabelText(/título/i), { target: { value: 'Albañil urgente' } });
    fireEvent.change(screen.getByLabelText(/descripción/i), { target: { value: 'Reparar pared del baño' } });
    fireEvent.change(screen.getByLabelText(/ubicación/i), { target: { value: 'Barrio Centro' } });
    fireEvent.change(screen.getByLabelText(/modalidad/i), { target: { value: 'PRESENCIAL' } });
    fireEvent.change(screen.getByLabelText(/presupuesto/i), { target: { value: '$15000' } });
    fireEvent.click(screen.getByRole('button', { name: /publicar/i }));

    await waitFor(() => expect(mockCrear).toHaveBeenCalledWith(
      expect.objectContaining({ titulo: 'Albañil urgente' }),
    ));
  });

  it('muestra error si falta el título', async () => {
    renderForm();

    fireEvent.click(screen.getByRole('button', { name: /publicar/i }));

    await waitFor(() =>
      expect(screen.getByText(/el título es obligatorio/i)).toBeInTheDocument(),
    );
    expect(mockCrear).not.toHaveBeenCalled();
  });

  it('muestra mensaje de éxito tras publicar', async () => {
    mockCrear.mockResolvedValue(ofertaCreada);
    const onOfertaCreada = vi.fn();

    render(<OportunidadFormModal isOpen onClose={vi.fn()} onSuccess={onOfertaCreada} />);

    fireEvent.change(screen.getByLabelText(/título/i), { target: { value: 'Oferta Test' } });
    fireEvent.change(screen.getByLabelText(/descripción/i), { target: { value: 'Descripción test' } });
    fireEvent.change(screen.getByLabelText(/ubicación/i), { target: { value: 'Barrio' } });
    fireEvent.change(screen.getByLabelText(/modalidad/i), { target: { value: 'PRESENCIAL' } });
    fireEvent.change(screen.getByLabelText(/presupuesto/i), { target: { value: '$10000' } });
    fireEvent.click(screen.getByRole('button', { name: /publicar/i }));

    await waitFor(() => expect(onOfertaCreada).toHaveBeenCalled());
  });
});
