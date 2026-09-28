import React, { useState } from 'react';
import { ArrowLeft, Clock, ShieldCheck, Check } from 'lucide-react';

export const PostulacionTrabajador: React.FC = () => {
    const [remuneracion, setRemuneracion] = useState('35.000');
    const [disponibilidadRapida, setDisponibilidadRapida] = useState<'inmediato' | 'manana' | 'finde'>('inmediato');
    const [mensaje, setMensaje] = useState(
        'Cuento con instrumental térmico y repuestos en el vehículo para solucionar en el acto.'
    );

    return (
        <div className="min-h-screen bg-[#070d18] text-white flex justify-center pb-24">
            <div className="w-full max-w-md md:max-w-2xl px-5 pt-8">

                {/* Header Superior */}
                <header className="flex items-center justify-between mb-6">
                    <button type="button" className="p-2.5 bg-[#131f33] rounded-full text-slate-300 hover:text-white">
                        <ArrowLeft size={18} />
                    </button>
                    <h1 className="text-xs tracking-widest font-semibold text-slate-300 uppercase">
                        Postulación
                    </h1>
                    <div className="w-9 h-9 rounded-full bg-[#131f33] flex items-center justify-center text-xs text-slate-300 font-bold">
                        ⓘ
                    </div>
                </header>

                {/* Tarjeta del Pedido del Vecino */}
                <div className="bg-[#111a2c] border border-slate-800/80 rounded-3xl p-4 mb-6">
                    <div className="flex items-start justify-between">
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center text-xs font-bold text-slate-300">
                                MG
                            </div>
                            <div>
                                <h3 className="text-xs font-bold text-slate-100">Mariana Gómez</h3>
                                <p className="text-[10px] text-slate-400">Barrio Las Rosas • Hace 2 horas</p>
                            </div>
                        </div>
                        <span className="text-[9px] font-bold px-2 py-0.5 rounded-full bg-amber-950/70 text-amber-400 border border-amber-500/40 uppercase">
              Urgente
            </span>
                    </div>

                    <div className="mt-3">
                        <h4 className="text-xs font-bold text-amber-400">Cambio térmico y recableado taller</h4>
                        <p className="text-xs text-slate-300 mt-1 italic">
                            "Salta la térmica principal al conectar soldadora. Se requiere electricista con instrumental para revisar."
                        </p>
                    </div>

                    <div className="flex gap-2 mt-3 pt-3 border-t border-slate-800/60 text-[10px] text-slate-400">
                        <span className="bg-[#0d1524] px-2.5 py-1 rounded-lg border border-slate-800">Presupuesto Abierto</span>
                        <span className="bg-[#0d1524] px-2.5 py-1 rounded-lg border border-slate-800">A convenir</span>
                    </div>
                </div>

                {/* Formulario de Propuesta Económica */}
                <div className="space-y-4">
                    <div>
                        <h2 className="text-xs font-bold text-slate-200 uppercase tracking-wide">Tu Propuesta Económica</h2>
                        <p className="text-[10px] text-slate-400 mt-0.5">REMUNERACIÓN PRETENDIDA ($)</p>

                        <div className="mt-2 relative">
                            <div className="w-full bg-[#101929] border border-amber-500/70 rounded-2xl p-3.5 flex items-center justify-between shadow-inner">
                                <div className="flex items-center gap-1.5">
                                    <span className="text-xl font-bold text-amber-400">$</span>
                                    <input
                                        type="text"
                                        value={remuneracion}
                                        onChange={(e) => setRemuneracion(e.target.value)}
                                        className="bg-transparent text-xl font-bold text-white outline-none w-36"
                                    />
                                </div>
                                <span className="text-xs font-semibold bg-[#19263e] px-2.5 py-1 rounded-lg text-slate-300 border border-slate-700/60">
                  ARS ▾
                </span>
                            </div>
                            <p className="text-[10px] text-slate-400 mt-1.5 ml-1">
                                Incluye diagnóstico y mano de obra básica inicial.
                            </p>
                        </div>
                    </div>

                    {/* Disponibilidad Horaria */}
                    <div>
                        <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wide block mb-1.5">
                            Disponibilidad Horaria
                        </label>
                        <div className="bg-[#101929] border border-slate-800 rounded-2xl p-3.5 flex items-center gap-2 text-xs text-slate-200 mb-2">
                            <Clock size={16} className="text-amber-400" />
                            <span>Hoy mismo, a partir de 16:30 hs</span>
                        </div>

                        {/* Opciones rápidas */}
                        <div className="grid grid-cols-3 gap-2">
                            <button
                                type="button"
                                onClick={() => setDisponibilidadRapida('inmediato')}
                                className={`py-2 px-2 rounded-xl text-[11px] font-semibold border flex items-center justify-center gap-1 transition-all ${
                                    disponibilidadRapida === 'inmediato'
                                        ? 'border-amber-500/80 bg-amber-950/30 text-amber-400'
                                        : 'border-slate-800 bg-[#101929] text-slate-400'
                                }`}
                            >
                                {disponibilidadRapida === 'inmediato' && <Check size={12} />} Inmediato
                            </button>
                            <button
                                type="button"
                                onClick={() => setDisponibilidadRapida('manana')}
                                className={`py-2 px-2 rounded-xl text-[11px] font-semibold border transition-all ${
                                    disponibilidadRapida === 'manana'
                                        ? 'border-amber-500/80 bg-amber-950/30 text-amber-400'
                                        : 'border-slate-800 bg-[#101929] text-slate-400'
                                }`}
                            >
                                Mañana Mañana
                            </button>
                            <button
                                type="button"
                                onClick={() => setDisponibilidadRapida('finde')}
                                className={`py-2 px-2 rounded-xl text-[11px] font-semibold border transition-all ${
                                    disponibilidadRapida === 'finde'
                                        ? 'border-amber-500/80 bg-amber-950/30 text-amber-400'
                                        : 'border-slate-800 bg-[#101929] text-slate-400'
                                }`}
                            >
                                Fin de semana
                            </button>
                        </div>
                    </div>

                    {/* Mensaje opcional */}
                    <div>
                        <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wide block mb-1.5">
                            Mensaje al Vecino (Opcional)
                        </label>
                        <textarea
                            rows={3}
                            value={mensaje}
                            onChange={(e) => setMensaje(e.target.value)}
                            className="w-full bg-[#101929] border border-slate-800 rounded-2xl p-3 text-xs text-slate-200 outline-none focus:border-amber-500/60 resize-none"
                        />
                    </div>

                    {/* Botón Enviar */}
                    <button
                        type="button"
                        className="w-full mt-3 py-3.5 rounded-xl font-bold text-sm bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-400 hover:to-amber-500 text-slate-950 shadow-lg shadow-amber-500/20 transition-all uppercase tracking-wider"
                    >
                        Enviar Postulación
                    </button>

                    <p className="text-center text-[10px] text-slate-400 flex items-center justify-center gap-1.5 mt-2">
                        <ShieldCheck size={13} className="text-amber-400" />
                        Contacto protegido por Sello Vecinal AlLado
                    </p>
                </div>

            </div>
        </div>
    );
};