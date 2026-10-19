'use client';
import { useTrabajadorQuery } from './useTrabajadorQuery';
import { trabajadorService } from '../services/trabajadorService';

export const useEstadisticasTrabajador = () =>
  useTrabajadorQuery('estadisticas', (id) => trabajadorService.obtenerEstadisticas(id));
