import api from '@/lib/api';
import { userIdFromToken } from '@/lib/jwt';
import { obtenerRefSesion } from '@/lib/usuarioRef';
import { EmpleadorDTO } from '@/types/usuario';

// AL-API-005: el id de la sesión sale del claim uid del token (o de lo guardado
// en el navegador); nunca se lista el directorio de empleadores.
function idSesion(): number | null {
  const token = typeof window !== 'undefined' ? localStorage.getItem('auth_token') : null;
  return userIdFromToken(token) ?? (obtenerRefSesion()?.id || null);
}

export const empleadorService = {
  // Backend no tiene /empleadores/perfil: se usa el id de la sesión con /empleadores/{id}
  async obtenerPerfil(): Promise<EmpleadorDTO> {
    const id = idSesion();
    if (!id) throw new Error('No se pudo identificar al empleador: la sesión no tiene token válido.');
    return this.obtenerPorId(id);
  },

  // PUT exige el DTO completo (nombre/email/telefono @NotBlank): se fusiona con el actual
  async actualizarPerfil(id: number, dto: Partial<EmpleadorDTO>): Promise<EmpleadorDTO> {
    const actual = await this.obtenerPorId(id);
    const { data } = await api.put<EmpleadorDTO>(`/empleadores/${id}`, { ...actual, ...dto });
    return data;
  },

  async obtenerTodos(): Promise<EmpleadorDTO[]> {
    const { data } = await api.get<EmpleadorDTO[]>('/empleadores');
    return data;
  },

  async obtenerPorId(id: number): Promise<EmpleadorDTO> {
    const { data } = await api.get<EmpleadorDTO>(`/empleadores/${id}`);
    return data;
  },
};
