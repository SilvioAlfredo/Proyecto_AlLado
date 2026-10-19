'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import {
  Search,
  SlidersHorizontal,
  MapPin,
  DollarSign,
  Clock,
  Briefcase,
  X,
  ChevronRight,
} from 'lucide-react';
import { useOportunidades } from '@/features/oportunidades/hooks/useOportunidades';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { formatDate } from '@/lib/utils';

export default function OportunidadesPage() {
  const { oportunidades, loading, error } = useOportunidades();
  const [busqueda, setBusqueda] = useState('');
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState('Todas');
  const [mostrarFiltros, setMostrarFiltros] = useState(false);

  const [modalidad, setModalidad] = useState('');
  const [ubicacion, setUbicacion] = useState('');
  const [soloDestacadas, setSoloDestacadas] = useState(false);
  const categorias = [
    'Todas',
    ...new Set(oportunidades.flatMap((op) => op.etiquetas?.map((e) => e.nombre) ?? [])),
  ];

  const oportunidadesFiltradas = (oportunidades || []).filter((op) => {
    const coincideTexto =
      op.titulo.toLowerCase().includes(busqueda.toLowerCase()) ||
      op.contenido.toLowerCase().includes(busqueda.toLowerCase());
    const coincideCategoria =
      categoriaSeleccionada === 'Todas' ||
      op.etiquetas?.some(
        (e) => e.nombre.toLowerCase() === categoriaSeleccionada.toLowerCase(),
      );
    return (
      op.activa &&
      coincideTexto &&
      coincideCategoria &&
      (!modalidad || op.modalidad === modalidad) &&
      op.ubicacion.toLowerCase().includes(ubicacion.toLowerCase()) &&
      (!soloDestacadas || op.destacada)
    );
  });

  return (
    <div className="w-full">
      <div className="max-w-6xl mx-auto space-y-6">
        {/* Cabecera y Buscador */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl md:text-3xl font-bold text-white">
              Oportunidades Laborales
            </h1>
            <p className="text-sm text-zinc-400">
              Encuentra trabajos cercanos a tu zona y según tus oficios
            </p>
          </div>

          <div className="flex items-center gap-2">
            <div className="relative flex-1 md:w-80">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-400" />
              <Input
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
                placeholder="Buscar por oficio o tarea..."
                className="pl-9 bg-allado-card2 text-white"
              />
            </div>
            <Button
              variant="outline"
              className="flex items-center gap-2 bg-allado-card"
              onClick={() => setMostrarFiltros(true)}
            >
              <SlidersHorizontal className="w-4 h-4 text-zinc-300" />
              <span>Filtros</span>
            </Button>
          </div>
        </div>

        {/* Chips de Categorías Rápidas */}
        <div className="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-none">
          {categorias.map((cat) => (
            <button
              key={cat}
              onClick={() => setCategoriaSeleccionada(cat)}
              className={`px-4 py-1.5 rounded-full text-xs md:text-sm font-medium transition-all whitespace-nowrap ${
                categoriaSeleccionada === cat
                  ? 'bg-allado-yellow text-black shadow-sm'
                  : 'bg-allado-card text-zinc-300 border border-allado-border hover:bg-allado-card2'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        {error && (
          <p role="alert" className="text-red-300">
            {error}
          </p>
        )}
        {/* Listado de Oportunidades */}
        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {[1, 2, 3, 4, 5, 6].map((i) => (
              <div key={i} className="h-48 rounded-xl bg-allado-card2 animate-pulse" />
            ))}
          </div>
        ) : oportunidadesFiltradas.length === 0 ? (
          <div className="text-center py-12 bg-allado-card rounded-2xl border border-allado-border p-8">
            <Briefcase className="w-12 h-12 text-zinc-300 mx-auto mb-3" />
            <h3 className="text-lg font-semibold text-zinc-200">
              No se encontraron trabajos
            </h3>
            <p className="text-sm text-zinc-400">
              Prueba ajustando tus términos de búsqueda o filtros.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
            {oportunidadesFiltradas.map((op) => (
              <div
                key={op.id}
                className="bg-allado-card rounded-xl border border-allado-border hover:border-allado-yellow p-5 shadow-sm hover:shadow-md transition-all flex flex-col justify-between"
              >
                <div className="space-y-3">
                  <div className="flex items-start justify-between gap-2">
                    <span className="text-xs font-semibold text-allado-yellow bg-allado-yellow/10 px-2.5 py-1 rounded-md">
                      {op.etiquetas?.[0]?.nombre || 'Servicio'}
                    </span>
                    <span className="text-xs text-zinc-400 flex items-center gap-1">
                      <Clock className="w-3.5 h-3.5" />
                      {formatDate(op.fechaCreacion)}
                    </span>
                  </div>

                  <h2 className="font-semibold text-white line-clamp-1 text-base">
                    {op.titulo}
                  </h2>
                  <p className="text-xs md:text-sm text-zinc-300 line-clamp-2">
                    {op.contenido}
                  </p>
                </div>

                <div className="pt-4 mt-4 border-t border-allado-border space-y-3">
                  <div className="flex items-center justify-between text-xs text-zinc-400">
                    <div className="flex items-center gap-1">
                      <MapPin className="w-3.5 h-3.5 text-zinc-400" />
                      <span>{op.ubicacion || 'A convenir'}</span>
                    </div>
                    <div className="flex items-center gap-1 font-semibold text-white">
                      <DollarSign className="w-3.5 h-3.5 text-emerald-400" />
                      <span>{op.rangoSalarial || 'A convenir'}</span>
                    </div>
                  </div>

                  <Link href={`/trabajador/oportunidades/${op.id}`}>
                    <Button
                      variant="outline"
                      className="w-full text-allado-yellow hover:text-allado-yellow hover:bg-allado-yellow/10 border-allado-yellow/30 text-xs"
                    >
                      Ver detalle y postularme
                      <ChevronRight className="w-3.5 h-3.5 ml-1" />
                    </Button>
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Modal / Drawer de Filtros Avanzados */}
        {mostrarFiltros && (
          <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex justify-end">
            <div className="w-full max-w-md bg-allado-card h-full p-6 flex flex-col justify-between shadow-2xl animate-in slide-in-from-right">
              <div className="space-y-6">
                <div className="flex items-center justify-between pb-3 border-b border-allado-border">
                  <h3 className="font-bold text-lg text-white">
                    Filtros de Oportunidades
                  </h3>
                  <button
                    onClick={() => setMostrarFiltros(false)}
                    className="text-zinc-400 hover:text-white"
                  >
                    <X className="w-5 h-5" />
                  </button>
                </div>

                <div className="space-y-4 text-white">
                  <label className="block">
                    Modalidad
                    <select
                      value={modalidad}
                      onChange={(e) => setModalidad(e.target.value)}
                      className="w-full rounded-xl border border-allado-border bg-allado-card2 p-3 text-white focus:outline-none focus:ring-2 focus:ring-allado-yellow"
                    >
                      <option value="">Todas</option>
                      {[...new Set(oportunidades.map((op) => op.modalidad))].map(
                        (valor) => (
                          <option key={valor}>{valor}</option>
                        ),
                      )}
                    </select>
                  </label>
                  <label className="block">
                    Ubicación
                    <Input
                      value={ubicacion}
                      onChange={(e) => setUbicacion(e.target.value)}
                      className="bg-allado-card2 text-white"
                    />
                  </label>
                  <label className="flex gap-2">
                    <input
                      type="checkbox"
                      checked={soloDestacadas}
                      onChange={(e) => setSoloDestacadas(e.target.checked)}
                    />
                    Solo destacadas
                  </label>
                </div>
              </div>

              <div className="flex gap-2 pt-4 border-t border-allado-border">
                <Button
                  variant="ghost"
                  className="flex-1"
                  onClick={() => {
                    setModalidad('');
                    setUbicacion('');
                    setSoloDestacadas(false);
                    setCategoriaSeleccionada('Todas');
                    setBusqueda('');
                  }}
                >
                  Limpiar
                </Button>
                <Button
                  className="flex-1 bg-allado-yellow hover:bg-allado-yellow-hover text-black"
                  onClick={() => setMostrarFiltros(false)}
                >
                  Aplicar filtros
                </Button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
