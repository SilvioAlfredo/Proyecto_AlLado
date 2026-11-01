'use client';

import { useState } from 'react';
import { ArrowRight, Eye, EyeOff, LoaderCircle } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useAuth } from '../hooks/useAuth';
import { RoleSelector } from './RoleSelector';
import { Field } from '@/components/ui/Field';
import { Feedback } from '@/components/ui/Feedback';
import { Input } from '@/components/ui/input';
import { applyServerErrors } from '@/lib/helpers/applyServerErrors';

/**
 * Esquema de validación de registro (refleja las reglas del backend):
 * - nombre ≤ 100, apellido ≤ 100, email ≤ 150, teléfono ≤ 20, contraseña ≥ 8.
 */
const registerSchema = z.object({
  nombre: z.string().min(1, 'El nombre es obligatorio.').max(100),
  apellido: z.string().min(1, 'El apellido es obligatorio.').max(100),
  email: z.string().email('Formato de email inválido.').max(150),
  telefono: z.string().min(1, 'El teléfono es obligatorio.').max(20),
  contrasena: z.string().min(8, 'Usá al menos 8 caracteres.'),
  rol: z.enum(['TRABAJADOR', 'EMPLEADOR']),
});

type RegisterFormValues = z.infer<typeof registerSchema>;

export function RegisterForm({
  initialRole = 'TRABAJADOR',
}: {
  initialRole?: 'TRABAJADOR' | 'EMPLEADOR';
}) {
  const { register } = useAuth();
  const [visible, setVisible] = useState(false);
  const [pending, setPending] = useState(false);
  const [creado, setCreado] = useState(false);

  const form = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      nombre: '',
      apellido: '',
      email: '',
      telefono: '',
      contrasena: '',
      rol: initialRole,
    },
  });

  const { register: registerField, handleSubmit, formState: { errors } } = form;

  const onSubmit = async (values: RegisterFormValues) => {
    setPending(true);
    try {
      await register({
        nombre: values.nombre,
        apellido: values.apellido,
        email: values.email.trim(),
        telefono: values.telefono,
        contrasena: values.contrasena,
        rol: values.rol,
      });
      setCreado(true);
    } catch (err: unknown) {
      // Aplica errores por campo del backend (mapa `errors`) si los hay.
      const msg = applyServerErrors(
        err,
        (campo, mensaje) => form.setError(campo as keyof RegisterFormValues, { type: 'server', message: mensaje }),
        'Error al registrarse'
      );
      if (msg) form.setError('root' as keyof RegisterFormValues, { type: 'server', message: msg });
    } finally {
      setPending(false);
    }
  };

  const fieldClass =
    'w-full rounded-xl border border-allado-border bg-allado-bg/70 px-4 py-3 text-sm text-white placeholder:text-zinc-600 focus:outline-none focus:border-allado-yellow focus:ring-1 focus:ring-allado-yellow disabled:opacity-50';

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="w-full space-y-6">
      {creado && (
        <Feedback variant="success">Cuenta creada correctamente.</Feedback>
      )}
      {errors.root && (
        <Feedback variant="error">{errors.root.message}</Feedback>
      )}
      <fieldset disabled={pending} className="space-y-3">
        <legend className="mb-3 text-sm font-semibold text-zinc-200">
          ¿Cómo querés usar AlLado?
        </legend>
        <RoleSelector
          selectedRole={form.watch('rol')}
          onSelectRole={(rol) => form.setValue('rol', rol as 'TRABAJADOR' | 'EMPLEADOR')}
        />
      </fieldset>
      <div className="border-t border-allado-border pt-6">
        <h2 className="mb-4 text-sm font-semibold text-zinc-200">Tus datos personales</h2>
        <div className="grid gap-4 sm:grid-cols-2">
          <Field
            label="Nombre"
            error={errors.nombre?.message}
            hint="Máximo 100 caracteres"
          >
            <Input
              {...registerField('nombre')}
              autoComplete="given-name"
              required
              disabled={pending}
              className={fieldClass}
              placeholder="Nombre"
            />
          </Field>
          <Field
            label="Apellido"
            error={errors.apellido?.message}
            hint="Máximo 100 caracteres"
          >
            <Input
              {...registerField('apellido')}
              autoComplete="family-name"
              required
              disabled={pending}
              className={fieldClass}
              placeholder="Apellido"
            />
          </Field>
          <Field
            label="Correo electrónico"
            error={errors.email?.message}
            hint="Máximo 150 caracteres"
          >
            <Input
              {...registerField('email')}
              autoComplete="email"
              type="email"
              required
              disabled={pending}
              className={fieldClass}
              placeholder="Correo electrónico"
            />
          </Field>
          <Field
            label="Teléfono"
            error={errors.telefono?.message}
            hint="Máximo 20 caracteres, con código de país"
          >
            <Input
              {...registerField('telefono')}
              autoComplete="tel"
              type="tel"
              required
              maxLength={20}
              disabled={pending}
              className={fieldClass}
              placeholder="Teléfono"
            />
          </Field>
        </div>
      </div>
      <Field
        label="Contraseña"
        error={errors.contrasena?.message}
        hint="Usá al menos 8 caracteres."
      >
        <div className="relative">
          <Input
            {...registerField('contrasena')}
            id="register-password"
            autoComplete="new-password"
            type={visible ? 'text' : 'password'}
            aria-describedby="register-password-help"
            required
            minLength={8}
            disabled={pending}
            className={`${fieldClass} pr-12`}
            placeholder="Contraseña"
          />
          <button
            type="button"
            onClick={() => setVisible(!visible)}
            aria-label={visible ? 'Ocultar contraseña' : 'Mostrar contraseña'}
            aria-pressed={visible}
            className="absolute right-3 top-1/2 -translate-y-1/2 rounded p-1 text-zinc-400 hover:text-white focus-visible:outline-2 focus-visible:outline-allado-yellow"
          >
            {visible ? <EyeOff size={18} /> : <Eye size={18} />}
          </button>
          <span
            id="register-password-help"
            className="block text-xs font-normal text-zinc-500 mt-1"
          >
            Usá al menos 8 caracteres.
          </span>
        </div>
      </Field>
      <button
        type="submit"
        disabled={pending || creado}
        className="flex w-full items-center justify-center gap-2 rounded-xl bg-allado-yellow px-4 py-3.5 text-sm font-bold text-black transition hover:bg-allado-yellow-hover focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-allado-yellow disabled:opacity-50"
      >
        {pending && <LoaderCircle size={17} className="animate-spin" />}
        {pending ? 'Creando cuenta…' : creado ? 'Cuenta creada' : 'Crear mi cuenta'}
        {!pending && !creado && <ArrowRight size={17} />}
      </button>
    </form>
  );
}
