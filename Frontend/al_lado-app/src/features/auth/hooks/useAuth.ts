'use client';

import { useState, useEffect } from 'react';
import { AuthRequestDTO, RegisterCredentials } from '@/types/auth';
import { authService } from '../services/authService';

export interface UserSession {
  email: string;
  roles: string[];
}

export function useAuth() {
  const [user, setUser] = useState<UserSession | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const token = localStorage.getItem('auth_token');
    const storedEmail = localStorage.getItem('user_email');
    const storedRoles = localStorage.getItem('user_roles');

    if (token && storedEmail && storedRoles) {
      setUser({
        email: storedEmail,
        roles: JSON.parse(storedRoles),
      });
    }
    setLoading(false);
  }, []);

  const login = async (credentials: AuthRequestDTO) => {
    const data = await authService.login(credentials);
    localStorage.setItem('auth_token', data.token);
    localStorage.setItem('user_email', data.email);
    localStorage.setItem('user_roles', JSON.stringify(data.roles));
    setUser({ email: data.email, roles: data.roles });
    return data;
  };

  const register = async (credentials: RegisterCredentials) => {
    const data = await authService.register(credentials);
    if (data.token) {
      localStorage.setItem('auth_token', data.token);
      localStorage.setItem('user_email', data.email);
      localStorage.setItem('user_roles', JSON.stringify(data.roles));
      setUser({ email: data.email, roles: data.roles });
    }
    return data;
  };

  const logout = () => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_email');
    localStorage.removeItem('user_roles');
    setUser(null);
    window.location.href = '/login';
  };

  return { user, loading, login, register, logout };
}