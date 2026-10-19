import axios from 'axios';

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

// Interceptor de Respuestas (Manejo Global de Errores)
api.interceptors.response.use(
    (response) => response,
    (error) => {
        const status = error.response?.status;

        // Maneja tanto token no autorizado (401) como acceso denegado / token vencido o inválido (403)
        if (typeof window !== 'undefined' && (status === 401 || status === 403)) {
            const isLoginRequest = error.config?.url?.includes('/auth/login');
            const isLoginPage = ['/login', '/acceso-admin'].includes(window.location.pathname);

            // Si no viene del formulario de login ni el usuario está en la vista de login
            if (!isLoginRequest && !isLoginPage) {
                localStorage.removeItem('auth_token');
                localStorage.removeItem('user_email');
                localStorage.removeItem('user_roles');
                window.location.href = window.location.pathname.startsWith('/admin') ? '/acceso-admin' : '/login';
            }
        }

        return Promise.reject(error);
    }
);

export default api;