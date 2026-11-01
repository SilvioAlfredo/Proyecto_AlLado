import { describe, expect, it, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { LoginForm } from './LoginForm';
import { authService } from '../services/authService';

/**
 * HU-04: Trabajador se registra y valida correo electrónico.
 *
 * Pruebas del formulario de inicio de sesión: envío de credenciales,
 * manejo de errores y redirección tras login exitoso.
 */
vi.mock('../services/authService', () => ({
  authService: { login: vi.fn() },
}));

const mockLogin = vi.mocked(authService.login);

function renderLogin() {
  return render(<LoginForm onLoginSuccess={vi.fn()} />);
}

describe('LoginForm', () => {
  beforeEach(() => {
    mockLogin.mockReset();
  });

  it('envía email y contraseña al hacer login', async () => {
    mockLogin.mockResolvedValue({
      token: 'jwt',
      id: 7,
      email: 'ana@mail.com',
      roles: ['ROLE_TRABAJADOR'],
    });

    renderLogin();

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'ana@mail.com' } });
    fireEvent.change(screen.getByLabelText(/contraseña/i), { target: { value: 'Clave123!' } });
    fireEvent.click(screen.getByRole('button', { name: /entrar/i }));

    await waitFor(() => expect(mockLogin).toHaveBeenCalledWith({
      email: 'ana@mail.com',
      contrasena: 'Clave123!',
    }));
  });

  it('muestra error cuando el login falla', async () => {
    mockLogin.mockRejectedValue(new Error('Credenciales inválidas'));

    renderLogin();

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'ana@mail.com' } });
    fireEvent.change(screen.getByLabelText(/contraseña/i), { target: { value: 'clave' } });
    fireEvent.click(screen.getByRole('button', { name: /entrar/i }));

    await waitFor(() =>
      expect(screen.getByText(/credenciales inválidas/i)).toBeInTheDocument(),
    );
  });

  it('no llama al login si los campos están vacíos', async () => {
    renderLogin();

    fireEvent.click(screen.getByRole('button', { name: /entrar/i }));

    await waitFor(() => expect(mockLogin).not.toHaveBeenCalled());
  });
});
