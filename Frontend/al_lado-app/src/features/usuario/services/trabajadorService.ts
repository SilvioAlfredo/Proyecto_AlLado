import api from '@/lib/api';
import { TrabajadorDTO } from '@/types/usuario';
import { EstadisticasTrabajadorDTO } from '@/types/usuario';

async function resolverIdPorEmail(email: string): Promise<number | null> {
  try {
    const { data } = await api.get<TrabajadorDTO[]>('/trabajadores');
    const match = data.find((t) => t.email?.toLowerCase() === email.toLowerCase());
    return match?.id ?? null;
  } catch {
    return null;
  }
}

export const trabajadorService = {
  // Backend no tiene /trabajadores/perfil: se resuelve id por email y usa /trabajadores/{id}
  async obtenerPerfil(email?: string): Promise<TrabajadorDTO> {
    if (!email) throw new Error('Sin email de sesión');
    const id = await resolverIdPorEmail(email);
    if (!id) throw new Error('Trabajador no encontrado para ' + email);
    return this.obtenerPorId(id);
  },

  async actualizarPerfil(email: string, dto: Partial<TrabajadorDTO>): Promise<TrabajadorDTO> {
    // PUT exige el DTO completo (nombre/email/telefono @NotBlank): se fusiona con el actual
    const actual = await this.obtenerPerfil(email);
    const { data } = await api.put<TrabajadorDTO>(`/trabajadores/${actual.id}`, { ...actual, ...dto });
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
