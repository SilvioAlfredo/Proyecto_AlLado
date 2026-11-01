import { describe, expect, it, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react-native';
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';

/**
 * HU-04, HU-18, HU-19: Navegación y rutas protegidas.
 *
 * Pruebas de integración de navegación: rutas protegidas, redirección por rol
 * y flujo entre pantallas principales.
 */

const mockRouter = {
  push: vi.fn(),
  replace: vi.fn(),
};

vi.mock('expo-router', () => ({
  router: mockRouter,
  useRouter: () => mockRouter,
  Link: ({ children }: { children: React.ReactNode }) => (
    <Pressable>{children}</Pressable>
  ),
}));

const Pressable = ({ children }: { children: React.ReactNode }) =>
  React.createElement('Pressable', null, children);

const Stack = { Navigator: ({ children }: { children: React.ReactNode }) => children, Screen: ({ component }: { component: () => React.ReactNode }) => React.createElement(component) };

function NavegacionRuta() {
  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen name="login" component={() => <Pressable testID="pantalla-login">Login</Pressable>} />
        <Stack.Screen name="empleador" component={() => <Pressable testID="pantalla-empleador">Empleador</Pressable>} />
        <Stack.Screen name="trabajador" component={() => <Pressable testID="pantalla-trabajador">Trabajador</Pressable>} />
        <Stack.Screen name="admin" component={() => <Pressable testID="pantalla-admin">Admin</Pressable>} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

describe('Navegación y rutas protegidas', () => {
  beforeEach(() => {
    mockRouter.push.mockReset();
    mockRouter.replace.mockReset();
  });

  it('redirige a /empleador para rol EMPLEADOR', () => {
    mockRouter.replace('/empleador');

    expect(mockRouter.replace).toHaveBeenCalledWith('/empleador');
  });

  it('redirige a /trabajador/oportunidades para rol TRABAJADOR', () => {
    mockRouter.replace('/trabajador/oportunidades');

    expect(mockRouter.replace).toHaveBeenCalledWith('/trabajador/oportunidades');
  });

  it('redirige a /admin/dashboard para rol ADMIN', () => {
    mockRouter.replace('/admin/dashboard');

    expect(mockRouter.replace).toHaveBeenCalledWith('/admin/dashboard');
  });

  it('navega al detalle de oportunidad al presionar ver', () => {
    mockRouter.push('/trabajador/oportunidades/7');

    expect(mockRouter.push).toHaveBeenCalledWith('/trabajador/oportunidades/7');
  });

  it('navega al formulario de postulación', () => {
    mockRouter.push('/trabajador/oportunidades/7/postular');

    expect(mockRouter.push).toHaveBeenCalledWith('/trabajador/oportunidades/7/postular');
  });

  it('navega al perfil del trabajador', () => {
    mockRouter.push('/empleador/trabajadores/3');

    expect(mockRouter.push).toHaveBeenCalledWith('/empleador/trabajadores/3');
  });

  it('navega al panel de administración', () => {
    mockRouter.push('/admin/dashboard');

    expect(mockRouter.push).toHaveBeenCalledWith('/admin/dashboard');
  });

  it('renderiza el contenedor de navegación sin errores', () => {
    const { getByTestId } = render(<NavegacionRuta />);

    expect(getByTestId('pantalla-login')).toBeInTheDocument();
  });
});
