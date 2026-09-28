import React from 'react';
import { ArrowLeft, TrendingUp, Users, Calendar } from 'lucide-react';
import { MOCK_ESTADISTICAS } from '../mocks/trabajadorMock';

export const EstadisticasTrabajador: React.FC = () => {
    return (
        <div className="min-h-screen bg-[#070d18] text-white flex justify-center pb-24">
            <div className="w-full max-w-md md:max-w-2xl px-5 pt-8">

                {/* Header Superior */}
                <header className="flex items-center justify-between mb-6">
                    <button type="button" className="p-2.5 bg-[#131f33] rounded-full text-slate-300 hover:text-white">
                        <ArrowLeft size={18} />
                    </button>
                    <h1 className="text-xs tracking-widest font-semibold text-slate-300 uppercase">
                        Mis Estadísticas
                    </h1>
                    <div className="w-9 h-9 rounded-full bg-[#131f33] flex items-center justify-center text-xs text-slate-300 font-bold">
                        ⓘ
                    </div>
                </header>

                {/* Tarjeta de Ingresos Estimados */}
                <div className="bg-gradient-to-b from-[#16233b] to-[#101b2d] border border-slate-700/60 rounded-3xl p-5 mb-5 shadow-lg">
                    <div className="flex items-center justify-between">
            <span className="text-[11px] font-bold text-slate-400 tracking-wider">
              INGRESOS ESTIMADOS
            </span>
                        <span className="flex items-center gap-1 text-[11px] font-bold text-emerald-400 bg-emerald-950/60 border border-emerald-500/40 px-2 py-0.5 rounded-full">
              ▲ {MOCK_ESTADISTICAS.porcentajeCrecimiento}
            </span>
                    </div>

                    <div className="mt-3">
            <span className="text-3xl font-extrabold tracking-tight text-white">
              {MOCK_ESTADISTICAS.ingresos}
            </span>
                        <p className="text-xs text-amber-400/90 font-medium mt-1">
                            {MOCK_ESTADISTICAS.periodo}
                        </p>
                    </div>

                    <div className="flex items-center justify-between mt-4 pt-3 border-t border-slate-700/50 text-[11px]">
            <span className="text-slate-400">
              Promedio por trabajo: <strong className="text-slate-200">{MOCK_ESTADISTICAS.promedioPorTrabajo}</strong>
            </span>
                        <span className="text-cyan-400 font-medium cursor-pointer hover:underline">
              Cobro al día ⓘ
            </span>
                    </div>
                </div>

                {/* Gráfico de Trabajos por Semana */}
                <div className="bg-[#111a2c] border border-slate-800/80 rounded-3xl p-5 mb-5">
                    <div className="flex items-center justify-between mb-4">
                        <div>
                            <h2 className="text-sm font-bold text-slate-100">Trabajos por Semana</h2>
                            <p className="text-[10px] text-slate-400 mt-0.5">Distribución del mes de Abril</p>
                        </div>
                        <span className="text-[10px] bg-[#1a2840] text-slate-300 px-2.5 py-1 rounded-full border border-slate-700/60 font-semibold">
              Total: 22
            </span>
                    </div>

                    {/* Barras verticales */}
                    <div className="flex items-end justify-between h-40 pt-6 px-4 border-b border-slate-800">
                        {MOCK_ESTADISTICAS.distribucionSemanas.map((item) => (
                            <div key={item.semana} className="flex flex-col items-center gap-2 h-full justify-end">
                <span className={`text-[11px] font-bold ${item.activo ? 'text-amber-400' : 'text-slate-400'}`}>
                  {item.cantidad}
                </span>
                                <div
                                    style={{ height: `${(item.cantidad / 8) * 100}%` }}
                                    className={`w-11 rounded-t-lg transition-all ${
                                        item.activo
                                            ? 'bg-gradient-to-t from-amber-600 to-amber-400 shadow-md shadow-amber-500/30'
                                            : 'bg-[#21304d]'
                                    }`}
                                />
                                <span className={`text-[11px] font-semibold mt-1 ${item.activo ? 'text-amber-400' : 'text-slate-400'}`}>
                  {item.semana}
                </span>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Tendencias en el Barrio */}
                <div className="bg-[#111a2c] border border-slate-800/80 rounded-3xl p-5 mb-5">
                    <h2 className="text-sm font-bold text-slate-100">Tendencias en el Barrio</h2>
                    <p className="text-[10px] text-slate-400 mb-4">Demandas más solicitadas en tu zona</p>

                    <div className="space-y-4">
                        {MOCK_ESTADISTICAS.tendencias.map((tendencia) => (
                            <div key={tendencia.rubro}>
                                <div className="flex justify-between text-xs mb-1.5">
                                    <span className="font-semibold text-slate-200">{tendencia.rubro}</span>
                                    <span className="font-bold text-amber-400">{tendencia.porcentaje}% {tendencia.nivel}</span>
                                </div>
                                <div className="w-full bg-[#1a2840] h-2.5 rounded-full overflow-hidden">
                                    <div
                                        className="bg-gradient-to-r from-amber-500 to-amber-400 h-full rounded-full"
                                        style={{ width: `${tendencia.porcentaje}%` }}
                                    />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Impacto en la Comunidad */}
                <div className="bg-[#131d2e] border border-amber-500/40 rounded-3xl p-4 flex items-center gap-4">
                    <div className="w-12 h-12 rounded-2xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-center text-amber-400 shrink-0">
                        <Users size={22} />
                    </div>
                    <div>
                        <h3 className="text-xs font-bold text-slate-100">Impacto en la Comunidad</h3>
                        <p className="text-xs text-slate-300 mt-0.5">
                            Has ayudado a <strong className="text-amber-400">{MOCK_ESTADISTICAS.impacto.vecinosAyudados} vecinos</strong> este mes en un radio de {MOCK_ESTADISTICAS.impacto.radioKm} km.
                        </p>
                    </div>
                </div>

            </div>
        </div>
    );
};