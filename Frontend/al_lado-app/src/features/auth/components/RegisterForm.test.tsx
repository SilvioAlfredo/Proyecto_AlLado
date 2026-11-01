import { describe, expect, it, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { RegisterForm } from './RegisterForm';
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

const mockRegister = vi.mocked(authService.register);

function renderRegister() {
  return render(<RegisterForm onRegisterSuccess={vi.fn()} />);
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
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'ana@mail.com' } });
    fireEvent.change(screen.getByLabelText(/teléfono/i), { target: { value: '+5491100000000' } });
    fireEvent.change(screen.getByLabelText(/contraseña/i), { target: { value: 'Clave123!' } });
    fireEvent.click(screen.getByRole('button', { name: /registrarme/i }));

    await waitFor(() => expect(mockRegister).toHaveBeenCalledWith({
      nombre: 'Ana Gomez',
      email: 'ana@mail.com',
      contrasena: 'Clave123!',
      telefono: '+5491100000000',
      rol: 'TRABAJADOR',
    }));
  });

  it('rechaza contraseña menor a 8 caracteres', async () => {
    renderRegister();

    fireEvent.change(screen.getByLabelText(/contraseña/i), { target: { value: 'corta' } });
    fireEvent.click(screen.getByRole('button', { name: /registrarme/i }));

    await waitFor(() =>
      expect(screen.getByText(/mínimo 8 caracteres/i)).toBeInTheDocument(),
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
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'e@mail.com' } });
    fireEvent.change(screen.getByLabelText(/teléfono/i), { target: { value: '+5491100000000' } });
    fireEvent.change(screen.getByLabelText(/contraseña/i), { target: { value: 'Clave123!' } });
    fireEvent.click(screen.getByLabelText(/empleador/i));
    fireEvent.click(screen.getByRole('button', { name: /registrarme/i }));

    await waitFor(() => expect(mockRegister).toHaveBeenCalledWith(
      expect.objectContaining({ rol: 'EMPLEADOR' }),
    ));
  });
});
