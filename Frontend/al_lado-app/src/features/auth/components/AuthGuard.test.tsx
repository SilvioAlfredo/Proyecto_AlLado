import { beforeEach, describe, expect, it, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { AuthGuard } from './AuthGuard';
import { useAuth } from '../hooks/useAuth';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

/**
 * HU-04 y HU-18/HU-19: Autenticación y sesiones.
 *
 * Pruebas del componente AuthGuard: protege rutas, redirige a login si no hay
 * sesión y restringe por rol cuando se indica.
 */
vi.mock('../hooks/useAuth', () => ({
  useAuth: vi.fn(),
}));

// El router se define con vi.hoisted porque vi.mock se eleva al inicio del
// archivo: la fábrica necesita tener mockRouter disponible al ejecutarse.
const { mockRouter } = vi.hoisted(() => ({
  mockRouter: { replace: vi.fn() },
}));

vi.mock('next/navigation', () => ({
  useRouter: () => mockRouter,
}));

const mockUseAuth = useAuth as unknown as ReturnType<typeof vi.fn>;

function renderGuard(props: { allowedRoles?: string[]; fallbackUrl?: string }) {
  const { allowedRoles, fallbackUrl } = props;
  return render(
    <QueryClientProvider client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}>
      <AuthGuard allowedRoles={allowedRoles} fallbackUrl={fallbackUrl}>
        <div data-testid="contenido">Contenido protegido</div>
      </AuthGuard>
    </QueryClientProvider>
  );
}

describe('AuthGuard', () => {
  beforeEach(() => {
    mockUseAuth.mockReset();
    mockRouter.replace.mockClear();
  });

  it('muestra un spinner mientras carga la sesión', () => {
    mockUseAuth.mockReturnValue({ user: null, loading: true });

    renderGuard({});

    expect(screen.getByRole('presentation')).toBeInTheDocument();
    expect(screen.queryByTestId('contenido')).not.toBeInTheDocument();
  });

  it('no muestra contenido ni redirige si no hay sesión y no está cargando', () => {
    mockUseAuth.mockReturnValue({ user: null, loading: false });

    renderGuard({});

    expect(screen.queryByTestId('contenido')).not.toBeInTheDocument();
  });

  it('muestra el contenido si hay sesión válida', () => {
    mockUseAuth.mockReturnValue({
      user: { email: 'ana@mail.com', roles: ['ROLE_TRABAJADOR'] },
      loading: false,
    });

    renderGuard({});

    expect(screen.getByTestId('contenido')).toBeInTheDocument();
  });

  it('redirige al fallbackUrl si no hay sesión', () => {
    mockUseAuth.mockReturnValue({ user: null, loading: false });

    renderGuard({ fallbackUrl: '/acceso-admin' });

    expect(mockRouter.replace).toHaveBeenCalledWith('/acceso-admin');
  });

  it('redirige a / si el usuario no tiene el rol requerido', () => {
    mockUseAuth.mockReturnValue({
      user: { email: 'ana@mail.com', roles: ['ROLE_TRABAJADOR'] },
      loading: false,
    });

    renderGuard({ allowedRoles: ['ROLE_ADMIN'] });

    expect(mockRouter.replace).toHaveBeenCalledWith('/');
  });

  it('no redirige si el usuario tiene el rol requerido', () => {
    mockUseAuth.mockReturnValue({
      user: { email: 'ana@mail.com', roles: ['ROLE_ADMIN'] },
      loading: false,
    });

    renderGuard({ allowedRoles: ['ROLE_ADMIN'] });

    expect(mockRouter.replace).not.toHaveBeenCalled();
    expect(screen.getByTestId('contenido')).toBeInTheDocument();
  });
});
