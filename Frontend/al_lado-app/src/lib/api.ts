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
            // 401 Unauthorized: token expirado, inválido o no provisto -> Cerrar sesión
            if (status === 401) {
                const isLoginRequest = error.config?.url?.includes('/auth/login');
                const isLoginPage = ['/login', '/acceso-admin'].includes(window.location.pathname);

                if (!isLoginRequest && !isLoginPage) {
                    localStorage.removeItem('auth_token');
                    localStorage.removeItem('user_email');
                    localStorage.removeItem('user_roles');
                    window.location.href = window.location.pathname.startsWith('/admin') ? '/acceso-admin' : '/login';
                }
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

        return Promise.reject(error);
    }
);

export default api;