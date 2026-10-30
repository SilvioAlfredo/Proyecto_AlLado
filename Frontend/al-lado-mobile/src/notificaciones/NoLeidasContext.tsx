import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import { notificacionService } from '../api/services';
import { useAuth } from '../auth/AuthContext';

interface NoLeidasContextValue {
  noLeidas: number;
  refrescar: () => Promise<void>;
}

/** Exportado para que las pruebas inyecten un contador sin montar la sesión. */
export const NoLeidasContext = createContext<NoLeidasContextValue | null>(null);

/**
 * Cuenta los avisos sin leer. Devuelve `null` si la consulta falla, para que el
 * contador conserve el último valor conocido en vez de resetearse a cero.
 */
async function contarNoLeidas(usuarioId: number): Promise<number | null> {
  try {
    return await notificacionService.noLeidas(usuarioId);
  } catch {
    return null;
  }
}

/**
 * Contador de avisos sin leer de la sesión.
 *
 * Vive en un contexto y no en cada pantalla porque la tab bar de trabajador y
 * empleador lo necesita montada siempre, mientras la lista de notificaciones se
 * desmonta al cambiar de tab. Al marcar leídas, la lista llama a `refrescar()`
 * y el badge se actualiza: sin eso quedaba con el valor de la primera carga.
 */
export function NoLeidasProvider({ children }: { children: ReactNode }): React.JSX.Element {
  const { userId } = useAuth();
  const [noLeidas, setNoLeidas] = useState(0);

  const refrescar = useCallback(async () => {
    if (userId == null) return;
    const total = await contarNoLeidas(userId);
    if (total != null) setNoLeidas(total);
  }, [userId]);

  useEffect(() => {
    if (userId == null) return;
    let vigente = true;
    // Se resuelve en un callback de promesa: nunca hay setState sincrónico
    // dentro del efecto, y una sesión cerrada a mitad de vuelo no escribe.
    void contarNoLeidas(userId).then((total) => {
      if (vigente && total != null) setNoLeidas(total);
    });
    return () => {
      vigente = false;
    };
  }, [userId]);

  const value = useMemo<NoLeidasContextValue>(
    () => ({ noLeidas: userId == null ? 0 : noLeidas, refrescar }),
    [userId, noLeidas, refrescar],
  );

  return <NoLeidasContext.Provider value={value}>{children}</NoLeidasContext.Provider>;
}

export function useNoLeidas(): NoLeidasContextValue {
  const value = useContext(NoLeidasContext);
  if (!value) throw new Error('El contador de avisos requiere NoLeidasProvider.');
  return value;
}

/** Etiqueta del badge de la tab: `undefined` cuando no hay avisos sin leer. */
export function etiquetaBadge(noLeidas: number): string | undefined {
  if (noLeidas <= 0) return undefined;
  return noLeidas > 9 ? '9+' : String(noLeidas);
}
