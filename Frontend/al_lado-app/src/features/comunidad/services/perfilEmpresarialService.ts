import api from '@/lib/api';
import { PerfilEmpresarialDTO } from '@/types/comunidad';

export const perfilEmpresarialService = {
  async obtener(empleadorId: number): Promise<PerfilEmpresarialDTO> {
    const { data } = await api.get<PerfilEmpresarialDTO>(
      `/perfiles-empresariales/empleador/${empleadorId}`,
    );
    return data;
  },
  async guardar(
    empleadorId: number,
    dto: Partial<PerfilEmpresarialDTO>,
  ): Promise<PerfilEmpresarialDTO> {
    const { data } = await api.put<PerfilEmpresarialDTO>(
      `/perfiles-empresariales/empleador/${empleadorId}`,
      dto,
    );
    return data;
  },
  /**
   * Marca un perfil empresarial como verificado y registra su supervisor.
   *
   * @param id - Identificador del perfil empresarial, no del empleador.
   * @param supervisorId - Parámetro compatible con clientes anteriores. Si se omite,
   * el backend usa al administrador autenticado; si se envía, debe coincidir con él.
   * @returns Perfil verificado. Repetir la operación conserva al supervisor original.
   */
  async verificar(id: number, supervisorId?: number): Promise<PerfilEmpresarialDTO> {
    const { data } = await api.put<PerfilEmpresarialDTO>(
      `/perfiles-empresariales/${id}/verificar`,
      { params: supervisorId == null ? {} : { supervisorId } },
    );
    return data;
  },
};
