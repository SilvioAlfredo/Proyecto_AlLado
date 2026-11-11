import api from '@/lib/api';
import { EmpleadorDTO } from '@/types/usuario';

async function resolverIdPorEmail(email: string): Promise<number | null> {
  try {
    const { data } = await api.get<EmpleadorDTO[]>('/empleadores');
    const match = data.find((e) => e.email?.toLowerCase() === email.toLowerCase());
    return match?.id ?? null;
  } catch {
    return null;
  }
}

export const empleadorService = {
  // Backend no tiene /empleadores/perfil: se resuelve id por email y usa /empleadores/{id}
  async obtenerPerfil(email?: string): Promise<EmpleadorDTO> {
    if (!email) throw new Error('Sin email de sesión');
    const id = await resolverIdPorEmail(email);
    if (!id) throw new Error('Empleador no encontrado para ' + email);
    return this.obtenerPorId(id);
  },

  async actualizarPerfil(email: string, dto: Partial<EmpleadorDTO>): Promise<EmpleadorDTO> {
    // PUT exige el DTO completo (nombre/email/telefono @NotBlank): se fusiona con el actual
    const actual = await this.obtenerPerfil(email);
    const { data } = await api.put<EmpleadorDTO>(`/empleadores/${actual.id}`, { ...actual, ...dto });
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
