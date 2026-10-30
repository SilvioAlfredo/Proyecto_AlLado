import axios, { AxiosError } from 'axios';

export const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000,
});

// Interceptor de Peticiones (Inyección del JWT)
api.interceptors.request.use(
    (config) => {
        if (typeof window !== 'undefined') {
            const token = localStorage.getItem('auth_token');

            if (token && config.headers) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor de Respuestas (Manejo Global de Errores - AL-FE-007 / MEJ-008)
api.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
        const status = error.response?.status;
        const responseData = error.response?.data;

        // Parseo tolerante: extraer mensaje legible sea objeto, string plano o fallback
        let errorMessage = 'Ha ocurrido un error en la comunicación con el servidor.';
        if (typeof responseData === 'string' && responseData.trim() !== '') {
            errorMessage = responseData;
        } else if (responseData && typeof responseData === 'object') {
            const dataObj = responseData as Record<string, any>;
            errorMessage = dataObj.message || dataObj.error || errorMessage;
        }

        if (typeof window !== 'undefined') {
            const urlPeticion = error.config?.url ?? '';
            // El login/registro con credenciales inválidas no destruye la sesión actual
            const esPeticionDeAuth = urlPeticion.includes('/auth/login') || urlPeticion.includes('/auth/register');
            const enPantallaDeAcceso = ['/login', '/acceso-admin'].includes(window.location.pathname);

            // 401 (token expirado/inválido) y 403 (sin permisos): la sesión ya no
            // sirve, se borra completa (incluido el identificador del usuario,
            // AL-API-005) y se vuelve al login para no mostrar datos de la cuenta
            // anterior (AL-FE-007 / MEJ-008)
            if ((status === 401 || status === 403) && !esPeticionDeAuth && !enPantallaDeAcceso) {
                ['auth_token', 'user_email', 'user_roles', 'user_id', 'user_nombre'].forEach((key) =>
                    localStorage.removeItem(key)
                );
                window.location.href = window.location.pathname.startsWith('/admin') ? '/acceso-admin' : '/login';
            }

            // 403 Forbidden: autenticado pero sin permisos -> NO desloguear, solo registrar / propagar
            if (status === 403) {
                console.warn('Acceso denegado (403 Forbidden): El usuario carece de permisos para este recurso.');
            }
        }

        // Adjuntar mensaje normalizado al error antes de propagarlo
        if (error.response) {
            (error.response as any).friendlyMessage = errorMessage;
        }

        // Adjuntar mensaje normalizado al error antes de propagarlo
        if (error.response) {
            (error.response as any).friendlyMessage = errorMessage;
        }

        return Promise.reject(error);
    }
);

export default api;