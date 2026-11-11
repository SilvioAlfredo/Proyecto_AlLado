import { expect, request, type APIRequestContext } from '@playwright/test';
import { env } from './env';

export type Role = 'TRABAJADOR' | 'EMPLEADOR';

export interface SessionUser {
  id: number;
  email: string;
  nombre: string;
  password: string;
  token: string;
}

interface AuthResponse {
  token: string;
  id: number;
  email: string;
  nombre: string;
  roles: string[];
}

interface Etiqueta {
  id?: number;
  nombre: string;
}

interface Oportunidad {
  id: number;
  titulo: string;
}

interface Postulacion {
  id: number;
  estado: string;
}

/**
 * Cliente HTTP mínimo para preparar las precondiciones de los escenarios E2E.
 *
 * La suite crea sus propias cuentas y publicaciones para no depender de datos
 * acumulados de corridas anteriores; el flujo visible se sigue validando por UI.
 */
export class ApiClient {
  private constructor(
    private readonly ctx: APIRequestContext,
    private readonly apiUrl: string,
  ) {}

  static async create(apiUrl: string = env.apiUrl): Promise<ApiClient> {
    const ctx = await request.newContext({
      baseURL: apiUrl,
      extraHTTPHeaders: { 'Content-Type': 'application/json' },
    });
    return new ApiClient(ctx, apiUrl);
  }

  async dispose(): Promise<void> {
    await this.ctx.dispose();
  }

  /** Registra una cuenta nueva y devuelve la sesión autenticada. */
  async register(role: Role, password: string = env.defaultPassword): Promise<SessionUser> {
    const suffix = `${Date.now()}${Math.floor(Math.random() * 1000)}`;
    const nombre = `E2E ${role === 'EMPLEADOR' ? 'Empleador' : 'Trabajador'} ${suffix}`;
    const email = `e2e.${role.toLowerCase()}.${suffix}@allado.test`;

    const response = await this.ctx.post('/auth/register', {
      data: {
        nombre,
        email,
        password,
        telefono: '+5491100000000',
        rol: role,
      },
    });
    expect(response.status(), `registro de ${role}: ${await response.text()}`).toBe(200);

    const body = (await response.json()) as AuthResponse;
    return {
      id: body.id,
      email: body.email,
      nombre: body.nombre,
      password,
      token: body.token,
    };
  }

  /** Inicia sesión por API (se usa en escenarios que no prueban el formulario). */
  async login(email: string, password: string): Promise<SessionUser> {
    const response = await this.ctx.post('/auth/login', { data: { email, password } });
    expect(response.status(), `login de ${email}: ${await response.text()}`).toBe(200);
    const body = (await response.json()) as AuthResponse;
    return { id: body.id, email: body.email, nombre: body.nombre, password, token: body.token };
  }

  private headers(user: SessionUser) {
    return { Authorization: `Bearer ${user.token}` };
  }

  /** Devuelve una especialidad existente para poder publicar ofertas. */
  async firstEtiqueta(user: SessionUser): Promise<Etiqueta> {
    const response = await this.ctx.get('/etiquetas', { headers: this.headers(user) });
    expect(response.status(), `etiquetas: ${await response.text()}`).toBe(200);
    const etiquetas = (await response.json()) as Etiqueta[];
    expect(etiquetas.length, 'la base de QA debe tener especialidades sembradas').toBeGreaterThan(0);
    return etiquetas[0];
  }

  async createOportunidad(
    user: SessionUser,
    data: { titulo: string; contenido: string; ubicacion?: string },
  ): Promise<Oportunidad> {
    const etiqueta = await this.firstEtiqueta(user);
    const response = await this.ctx.post('/oportunidades', {
      headers: this.headers(user),
      data: {
        titulo: data.titulo,
        contenido: data.contenido,
        ubicacion: data.ubicacion ?? 'Centro',
        modalidad: 'PRESENCIAL',
        rangoSalarial: '100000-150000',
        activa: true,
        autor: { id: user.id, nombre: user.nombre, email: user.email },
        etiquetas: [etiqueta],
      },
    });
    expect(response.status(), `crear oportunidad: ${await response.text()}`).toBe(201);
    return (await response.json()) as Oportunidad;
  }

  async createPostulacion(
    worker: SessionUser,
    oportunidad: Oportunidad,
    remuneracion = 110000,
  ): Promise<Postulacion> {
    const response = await this.ctx.post('/postulaciones', {
      headers: this.headers(worker),
      data: {
        titulo: `Propuesta para ${oportunidad.titulo}`.slice(0, 150),
        contenido: 'Propuesta generada como precondición del escenario E2E.',
        autor: { id: worker.id, nombre: worker.nombre, email: worker.email },
        remuneracionPretendida: remuneracion,
        oportunidadLaboral: { id: oportunidad.id, titulo: oportunidad.titulo },
      },
    });
    expect(response.status(), `crear postulación: ${await response.text()}`).toBe(201);
    return (await response.json()) as Postulacion;
  }

  async seleccionar(employer: SessionUser, postulacionId: number): Promise<Postulacion> {
    return this.patchEstado(employer, postulacionId, 'seleccionar');
  }

  async completar(employer: SessionUser, postulacionId: number): Promise<Postulacion> {
    return this.patchEstado(employer, postulacionId, 'completar');
  }

  private async patchEstado(
    employer: SessionUser,
    postulacionId: number,
    accion: 'seleccionar' | 'completar',
  ): Promise<Postulacion> {
    const response = await this.ctx.patch(`/postulaciones/${postulacionId}/${accion}`, {
      headers: this.headers(employer),
    });
    expect(response.status(), `${accion} postulación: ${await response.text()}`).toBe(200);
    return (await response.json()) as Postulacion;
  }
}
