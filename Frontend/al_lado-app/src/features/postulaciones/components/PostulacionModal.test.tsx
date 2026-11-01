import { describe, expect, it, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { PostulacionModal } from './PostulacionModal';
import { postulacionesService } from '../services/postulacionesService';

/**
 * HU-02: Trabajador se postula a tareas.
 *
 * Pruebas del modal de postulación: envío de mensaje y remuneración,
 * validación de campos y manejo de respuesta del servidor.
 */
vi.mock('../services/postulacionesService', () => ({
  postulacionesService: { crear: vi.fn() },
}));

const mockCrear = vi.mocked(postulacionesService.crear);

function renderModal() {
  return render(<PostulacionModal oportunidadId={7} onClose={vi.fn()} onPostulacionCreada={vi.fn()} />);
}

describe('PostulacionModal', () => {
  beforeEach(() => {
    mockCrear.mockReset();
  });

  it('envía mensaje y remuneración al postularse', async () => {
    mockCrear.mockResolvedValue({ id: 1, estado: 'PENDIENTE' });

    renderModal();

    fireEvent.change(screen.getByLabelText(/mensaje/i), { target: { value: 'Tengo experiencia en albañilería' } });
    fireEvent.change(screen.getByLabelText(/remuneración/i), { target: { value: '12000' } });
    fireEvent.click(screen.getByRole('button', { name: /enviar postulación/i }));

    await waitFor(() => expect(mockCrear).toHaveBeenCalledWith(
      expect.objectContaining({
        contenido: 'Tengo experiencia en albañilería',
        remuneracionPretendida: 12000,
        oportunidadLaboral: { id: 7 },
      }),
    ));
  });

  it('valida que la remuneración sea mayor a cero', async () => {
    renderModal();

    fireEvent.change(screen.getByLabelText(/mensaje/i), { target: { value: 'Mensaje' } });
    fireEvent.change(screen.getByLabelText(/remuneración/i), { target: { value: '0' } });
    fireEvent.click(screen.getByRole('button', { name: /enviar postulación/i }));

    await waitFor(() =>
      expect(screen.getByText(/debe ser mayor a cero/i)).toBeInTheDocument(),
    );
    expect(mockCrear).not.toHaveBeenCalled();
  });

  it('muestra error cuando el servidor rechaza la postulación', async () => {
    mockCrear.mockRejectedValue(new Error('Ya te encuentras postulado a esta oportunidad'));

    renderModal();

    fireEvent.change(screen.getByLabelText(/mensaje/i), { target: { value: 'Mensaje' } });
    fireEvent.change(screen.getByLabelText(/remuneración/i), { target: { value: '10000' } });
    fireEvent.click(screen.getByRole('button', { name: /enviar postulación/i }));

    await waitFor(() =>
      expect(screen.getByText(/ya te encuentras postulado/i)).toBeInTheDocument(),
    );
  });
});
