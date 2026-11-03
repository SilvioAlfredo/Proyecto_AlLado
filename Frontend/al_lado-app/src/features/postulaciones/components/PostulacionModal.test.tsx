import { beforeEach, describe, expect, it, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { PostulacionModal } from './PostulacionModal';
import { postulacionesService } from '../services/postulacionesService';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { AuthContext, type AuthContextValue } from '@/features/auth/hooks/useAuth';
import type { PostulacionDTO } from '@/types/trabajo';

/**
 * HU-02: Trabajador se postula a tareas.
 *
 * Pruebas del modal de postulación: envío de mensaje y remuneración,
 * validación de campos y manejo de respuesta del servidor.
 */
vi.mock('../services/postulacionesService', () => ({
  postulacionesService: { crear: vi.fn() },
}));

// Sin la prop `oportunidad`, el modal consulta la oferta antes de postularse.
vi.mock('@/features/oportunidades/services/oportunidadService', () => ({
  oportunidadesService: { obtenerPorId: vi.fn() },
}));

// El autor de la postulación sale de la sesión guardada en el navegador.
vi.mock('@/lib/usuarioRef', () => ({
  obtenerRefSesion: () => ({ id: 7, nombre: 'Ana', email: 'ana@mail.com' }),
}));

const mockCrear = vi.mocked(postulacionesService.crear);

const postulacionCreada: PostulacionDTO = {
  id: 1,
  titulo: 'Postulación',
  contenido: 'Tengo experiencia en albañilería',
  estado: 'PENDIENTE',
  remuneracionPretendida: 12000,
  oportunidadLaboral: {
    id: 7,
    titulo: 'Albañil urgente',
    contenido: 'Reparar pared del baño',
    ubicacion: 'Barrio Centro',
    modalidad: 'PRESENCIAL',
    rangoSalarial: '$12000',
    activa: true,
    fechaCreacion: '2026-01-01',
  },
};

function renderModal() {
  // El modal necesita QueryClient (invalidateQueries) y un usuario de sesión
  // con rol TRABAJADOR para habilitar el envío.
  const valorAuth: AuthContextValue = {
    user: { email: 'ana@mail.com', roles: ['ROLE_TRABAJADOR'] },
    userId: 7,
    nombre: 'Ana',
    loading: false,
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
  };
  return render(
    <QueryClientProvider
      client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}
    >
      <AuthContext.Provider value={valorAuth}>
        <PostulacionModal oportunidadId={7} isOpen onClose={vi.fn()} onSuccess={vi.fn()} />
      </AuthContext.Provider>
    </QueryClientProvider>,
  );
}

describe('PostulacionModal', () => {
  beforeEach(() => {
    mockCrear.mockReset();
    vi.mocked(oportunidadesService.obtenerPorId).mockResolvedValue({
      id: 7,
      titulo: 'Albañil urgente',
      contenido: 'Reparar pared del baño',
      ubicacion: 'Barrio Centro',
      modalidad: 'PRESENCIAL',
      rangoSalarial: '$12000',
      activa: true,
      fechaCreacion: '2026-01-01',
    });
  });

  it('envía mensaje y remuneración al postularse', async () => {
    mockCrear.mockResolvedValue(postulacionCreada);

    renderModal();

    fireEvent.change(screen.getByLabelText(/mensaje/i), { target: { value: 'Tengo experiencia en albañilería' } });
    fireEvent.change(screen.getByLabelText(/cotización/i), { target: { value: '12000' } });
    fireEvent.click(screen.getByRole('button', { name: /confirmar postulación/i }));

    await waitFor(() => expect(mockCrear).toHaveBeenCalledWith(
      expect.objectContaining({
        contenido: expect.stringContaining('Tengo experiencia en albañilería'),
        remuneracionPretendida: 12000,
        oportunidadLaboral: expect.objectContaining({ id: 7 }),
      }),
    ));
  });

  it('valida que la remuneración sea mayor a cero', async () => {
    renderModal();

    fireEvent.change(screen.getByLabelText(/mensaje/i), { target: { value: 'Mensaje' } });
    fireEvent.change(screen.getByLabelText(/cotización/i), { target: { value: '0' } });
    fireEvent.click(screen.getByRole('button', { name: /confirmar postulación/i }));

    await waitFor(() =>
      expect(screen.getByText(/debe ser mayor a cero/i)).toBeInTheDocument(),
    );
    expect(mockCrear).not.toHaveBeenCalled();
  });

  it('muestra error cuando el servidor rechaza la postulación', async () => {
    mockCrear.mockRejectedValue(new Error('Ya te encuentras postulado a esta oportunidad'));

    renderModal();

    fireEvent.change(screen.getByLabelText(/mensaje/i), { target: { value: 'Mensaje' } });
    fireEvent.change(screen.getByLabelText(/cotización/i), { target: { value: '10000' } });
    fireEvent.click(screen.getByRole('button', { name: /confirmar postulación/i }));

    await waitFor(() =>
      expect(screen.getByText(/ya te encuentras postulado/i)).toBeInTheDocument(),
    );
  });
});
