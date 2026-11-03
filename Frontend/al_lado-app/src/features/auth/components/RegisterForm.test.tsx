import { beforeEach, describe, expect, it, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { RegisterForm } from './RegisterForm';
import { AuthProvider } from './AuthProvider';
import { authService } from '../services/authService';

/**
 * HU-04: Trabajador se registra y valida correo electrónico.
 *
 * Pruebas del formulario de registro: envío de datos, validación de
 * contraseña mínima y selección de rol.
 */
vi.mock('../services/authService', () => ({
  authService: { register: vi.fn() },
}));

// AuthProvider usa el router de Next; en jsdom no hay app router.
vi.mock('next/navigation', () => ({
  useRouter: () => ({ replace: vi.fn() }),
}));

const mockRegister = vi.mocked(authService.register);

function renderRegister() {
  return render(
    <QueryClientProvider
      client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}
    >
      {/* El registro real llega a authService a través del AuthProvider. */}
      <AuthProvider>
        <RegisterForm />
      </AuthProvider>
    </QueryClientProvider>,
  );
}

describe('RegisterForm', () => {
  beforeEach(() => {
    mockRegister.mockReset();
  });

  it('envía datos completos al registrarse como trabajador', async () => {
    mockRegister.mockResolvedValue({
      token: 'jwt',
      id: 7,
      email: 'ana@mail.com',
      roles: ['ROLE_TRABAJADOR'],
    });

    renderRegister();

    fireEvent.change(screen.getByLabelText(/nombre/i), { target: { value: 'Ana' } });
    fireEvent.change(screen.getByLabelText(/apellido/i), { target: { value: 'Gomez' } });
    fireEvent.change(screen.getByLabelText(/correo/i), { target: { value: 'ana@mail.com' } });
    fireEvent.change(screen.getByLabelText(/teléfono/i), { target: { value: '+5491100000000' } });
    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'Clave123!' } });
    fireEvent.click(screen.getByRole('button', { name: /registrarme/i }));

    await waitFor(() => expect(mockRegister).toHaveBeenCalledWith({
      // El mock reemplaza a authService.register, que es quien combina
      // nombre + apellido antes de enviarlo al backend.
      nombre: 'Ana',
      apellido: 'Gomez',
      email: 'ana@mail.com',
      contrasena: 'Clave123!',
      telefono: '+5491100000000',
      rol: 'TRABAJADOR',
    }));
  });

  it('rechaza contraseña menor a 8 caracteres', async () => {
    renderRegister();

    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'corta' } });
    fireEvent.click(screen.getByRole('button', { name: /registrarme/i }));

    await waitFor(() =>
      expect(screen.getByText(/al menos 8 caracteres/i)).toBeInTheDocument(),
    );
    expect(mockRegister).not.toHaveBeenCalled();
  });

  it('permite seleccionar rol de empleador', async () => {
    mockRegister.mockResolvedValue({
      token: 'jwt',
      id: 7,
      email: 'empleador@mail.com',
      roles: ['ROLE_EMPLEADOR'],
    });

    renderRegister();

    fireEvent.change(screen.getByLabelText(/nombre/i), { target: { value: 'Empresa' } });
    fireEvent.change(screen.getByLabelText(/apellido/i), { target: { value: 'ABC' } });
    fireEvent.change(screen.getByLabelText(/correo/i), { target: { value: 'e@mail.com' } });
    fireEvent.change(screen.getByLabelText(/teléfono/i), { target: { value: '+5491100000000' } });
    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'Clave123!' } });
    fireEvent.click(screen.getByRole('button', { name: /quiero contratar/i }));
    fireEvent.click(screen.getByRole('button', { name: /registrarme/i }));

    await waitFor(() => expect(mockRegister).toHaveBeenCalledWith(
      expect.objectContaining({ rol: 'EMPLEADOR' }),
    ));
  });
});
