import { describe, expect, it, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react-native';
import React from 'react';
import { Register } from './register';
import { useAuth } from '../../src/auth/AuthContext';
import { errMsg } from '../../src/api/client';

/**
 * HU-04: Trabajador se registra y valida correo electrónico.
 *
 * Pruebas del componente Register: envío de datos, validación de contraseña
 * mínima (8 caracteres), selección de rol y manejo de errores.
 */
vi.mock('../../src/auth/AuthContext', () => ({
  useAuth: vi.fn(),
  roleHome: vi.fn((roles: string[]) => roles.includes('ROLE_ADMIN') ? '/admin/dashboard' : '/trabajador/oportunidades'),
}));

vi.mock('../../src/api/client', () => ({
  errMsg: vi.fn((e: unknown) => String(e)),
}));

vi.mock('expo-router', () => ({
  Link: ({ children, href }: { children: React.ReactNode; href: string }) => (
    <Pressable testID={`link-${href}`}>{children}</Pressable>
  ),
  router: { replace: vi.fn() },
}));

const Pressable = ({ children, testID }: { children: React.ReactNode; testID?: string }) =>
  React.createElement('Pressable', { testID }, children);

const mockUseAuth = useAuth as unknown as ReturnType<typeof vi.fn>;

beforeEach(() => {
  mockUseAuth.mockReset();
  vi.mocked(errMsg).mockReset();
});

function renderRegister() {
  return render(<Register />);
}

describe('Register', () => {
  it('envía datos completos al registrarse como trabajador', async () => {
    const registerMock = vi.fn().mockResolvedValue({
      token: 'jwt',
      id: 7,
      email: 'ana@mail.com',
      roles: ['ROLE_TRABAJADOR'],
    });
    mockUseAuth.mockReturnValue({ register: registerMock });

    renderRegister();

    fireEvent.changeText(screen.getByPlaceholderText('Juan Pérez'), 'Ana');
    fireEvent.changeText(screen.getAllByPlaceholderText('juan@mail.com')[0], 'ana@mail.com');
    fireEvent.changeText(screen.getAllByPlaceholderText('+54 ...')[0], '+5491100000000');
    fireEvent.changeText(screen.getByPlaceholderText('••••••••'), 'Clave123!');
    fireEvent.press(screen.getByText('Crear mi cuenta'));

    await waitFor(() => expect(registerMock).toHaveBeenCalledWith({
      nombre: 'Ana',
      email: 'ana@mail.com',
      password: 'Clave123!',
      telefono: '+5491100000000',
      rol: 'TRABAJADOR',
    }));
  });

  it('rechaza contraseña menor a 8 caracteres', async () => {
    const registerMock = vi.fn().mockResolvedValue({});
    mockUseAuth.mockReturnValue({ register: registerMock });

    renderRegister();

    fireEvent.changeText(screen.getByPlaceholderText('Juan Pérez'), 'Ana');
    fireEvent.changeText(screen.getAllByPlaceholderText('juan@mail.com')[0], 'ana@mail.com');
    fireEvent.changeText(screen.getAllByPlaceholderText('+54 ...')[0], '+5491100000000');
    fireEvent.changeText(screen.getByPlaceholderText('••••••••'), 'corta');
    fireEvent.press(screen.getByText('Crear mi cuenta'));

    await waitFor(() =>
      expect(screen.getByText(/mínimo 8 caracteres/i)).toBeInTheDocument(),
    );
    expect(registerMock).not.toHaveBeenCalled();
  });

  it('permite seleccionar rol de empleador', async () => {
    const registerMock = vi.fn().mockResolvedValue({
      token: 'jwt',
      id: 7,
      email: 'e@mail.com',
      roles: ['ROLE_EMPLEADOR'],
    });
    mockUseAuth.mockReturnValue({ register: registerMock });

    renderRegister();

    fireEvent.changeText(screen.getByPlaceholderText('Juan Pérez'), 'Empresa');
    fireEvent.changeText(screen.getAllByPlaceholderText('juan@mail.com')[0], 'e@mail.com');
    fireEvent.changeText(screen.getAllByPlaceholderText('+54 ...')[0], '+5491100000000');
    fireEvent.changeText(screen.getByPlaceholderText('••••••••'), 'Clave123!');
    // Seleccionar Empleador (segundo TouchableOpacity en rolRow)
    const rolButtons = screen.getAllByText(/Trabajador|Empleador/);
    fireEvent.press(rolButtons[1]);
    fireEvent.press(screen.getByText('Crear mi cuenta'));

    await waitFor(() => expect(registerMock).toHaveBeenCalledWith(
      expect.objectContaining({ rol: 'EMPLEADOR' }),
    ));
  });

  it('muestra enlace para ir al login', () => {
    renderRegister();

    expect(screen.getByTestId('link-/(auth)/login')).toBeInTheDocument();
  });
});
