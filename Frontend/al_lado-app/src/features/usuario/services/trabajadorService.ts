import api from '@/lib/api';
import { userIdFromToken } from '@/lib/jwt';
import { obtenerRefSesion } from '@/lib/usuarioRef';
import { TrabajadorDTO } from '@/types/usuario';
import { EstadisticasTrabajadorDTO } from '@/types/usuario';

// AL-API-005: el id de la sesión sale del claim uid del token (o de lo guardado
// en el navegador); nunca se lista el directorio de trabajadores.
function idSesion(): number | null {
  const token = typeof window !== 'undefined' ? localStorage.getItem('auth_token') : null;
  return userIdFromToken(token) ?? (obtenerRefSesion()?.id || null);
}

export const trabajadorService = {
  // Backend no tiene /trabajadores/perfil: se usa el id de la sesión con /trabajadores/{id}
  async obtenerPerfil(): Promise<TrabajadorDTO> {
    const id = idSesion();
    if (!id) throw new Error('No se pudo identificar al trabajador: la sesión no tiene token válido.');
    return this.obtenerPorId(id);
  },

  // PUT exige el DTO completo (nombre/email/telefono @NotBlank): se fusiona con el actual
  async actualizarPerfil(id: number, dto: Partial<TrabajadorDTO>): Promise<TrabajadorDTO> {
    const actual = await this.obtenerPorId(id);
    const { data } = await api.put<TrabajadorDTO>(`/trabajadores/${id}`, { ...actual, ...dto });
    return data;
  },

  async obtenerTodos(): Promise<TrabajadorDTO[]> {
    const { data } = await api.get<TrabajadorDTO[]>('/trabajadores');
    return data;
  },

  async obtenerPorId(id: number): Promise<TrabajadorDTO> {
    const { data } = await api.get<TrabajadorDTO>(`/trabajadores/${id}`);
    return data;
  },

  async obtenerEstadisticasPorEmail(email: string): Promise<EstadisticasTrabajadorDTO> {
    const { data } = await api.get<EstadisticasTrabajadorDTO>(`/trabajadores/por-email/estadisticas?email=${email}`);
    return data;
  },

  async obtenerEstadisticas(id: number): Promise<EstadisticasTrabajadorDTO> {
    const { data } = await api.get<EstadisticasTrabajadorDTO>(`/trabajadores/${id}/estadisticas`);
    return data;
  },

  async filtrar(etiqueta?: string, sector?: string): Promise<TrabajadorDTO[]> {
    const params = new URLSearchParams();
    if (etiqueta) params.set('etiqueta', etiqueta);
    if (sector) params.set('sector', sector);
    const { data } = await api.get<TrabajadorDTO[]>(`/trabajadores/filtrar?${params.toString()}`);
    return data;
  },
};
