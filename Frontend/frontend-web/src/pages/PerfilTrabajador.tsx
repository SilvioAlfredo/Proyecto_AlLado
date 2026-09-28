import React, { useState } from 'react';
import { ArrowLeft, Star, ShieldCheck, Award, MessageCircle, ChevronDown, CheckCircle2, User } from 'lucide-react';
import { MOCK_PERFIL } from '../mocks/trabajadorMock';

export const PerfilTrabajador: React.FC = () => {
    const [desplegarCertificados, setDesplegarCertificados] = useState(true);

    return (
        <div className="min-h-screen bg-[#070d18] text-white flex justify-center pb-24">
            <div className="w-full max-w-md md:max-w-2xl px-5 pt-8">

                {/* Header Superior */}
                <header className="flex items-center justify-between mb-6">
                    <button className="p-2.5 bg-[#131f33] rounded-full text-slate-300 hover:text-white">
                        <ArrowLeft size={18} />
                    </button>
                    <h1 className="text-xs tracking-widest font-semibold text-slate-300 uppercase">
                        Perfil Profesional
                    </h1>
                    <div className="w-9 h-9 rounded-full bg-[#131f33] flex items-center justify-center text-xs text-amber-500 font-bold">
                        !
                    </div>
                </header>

                {/* Foto de Perfil & Calificación */}
                <div className="flex flex-col items-center">
                    <div className="relative">
                        <div className="w-24 h-24 rounded-full border-2 border-amber-500 flex items-center justify-center bg-[#131f33]">
                            <User size={48} className="text-slate-400" />
                        </div>
                        <div className="absolute -bottom-2.5 left-1/2 -translate-x-1/2 bg-[#0b1320] border border-amber-500/60 rounded-full px-2.5 py-0.5 flex items-center gap-1">
                            <Star size={12} className="text-amber-400 fill-amber-400" />
                            <span className="text-xs font-bold text-amber-400">{MOCK_PERFIL.calificacion}</span>
                        </div>
                    </div>

                    <h2 className="mt-4 text-xl font-bold">{MOCK_PERFIL.nombre}</h2>
                    <p className="text-xs text-slate-400">{MOCK_PERFIL.oficio}</p>
                </div>

                {/* Badges de Verificación */}
                <div className="flex flex-wrap items-center justify-center gap-2 mt-4">
          <span className="flex items-center gap-1 px-3 py-1 text-[11px] rounded-full bg-emerald-950/70 border border-emerald-500/60 text-emerald-400">
            <ShieldCheck size={13} /> Vecino Verificado
          </span>
                    <span className="flex items-center gap-1 px-3 py-1 text-[11px] rounded-full bg-amber-950/40 border border-amber-500/60 text-amber-400">
            <Award size={13} /> Sello Pro AlLado
          </span>
                </div>

                {/* Info adicional */}
                <div className="flex justify-center gap-2 mt-2">
          <span className="px-3 py-1 text-[10px] rounded-lg bg-[#111a2c] text-slate-400 border border-slate-800">
            {MOCK_PERFIL.matricula}
          </span>
                    <span className="px-3 py-1 text-[10px] rounded-lg bg-[#111a2c] text-slate-400 border border-slate-800">
            {MOCK_PERFIL.zonas}
          </span>
                </div>

                {/* Botón CTA WhatsApp */}
                <a
                    href={MOCK_PERFIL.whatsappUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="mt-6 w-full py-3.5 px-4 rounded-xl font-semibold text-sm bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-400 hover:to-amber-500 text-slate-950 flex items-center justify-center gap-2 shadow-lg shadow-amber-500/20 transition-all"
                >
                    <MessageCircle size={18} />
                    Contactar por WhatsApp
                </a>

                {/* Galería de Trabajos Realizados */}
                <section className="mt-8">
                    <div className="flex items-center justify-between mb-3">
                        <h3 className="text-sm font-semibold text-slate-200">Trabajos Realizados</h3>
                        <button className="text-xs font-semibold text-amber-400 hover:underline">
                            Ver todos (12)
                        </button>
                    </div>

                    <div className="grid grid-cols-2 md:grid-cols-2 gap-3">
                        {MOCK_PERFIL.trabajos.map((trabajo) => (
                            <div key={trabajo.id} className="bg-[#111a2c] border border-slate-800/80 rounded-2xl p-3.5 flex flex-col justify-between">
                                <div className="w-full h-24 rounded-xl bg-gradient-to-tr from-slate-900 to-indigo-950 mb-3 flex items-center justify-center">
                                    <div className="w-4 h-4 rounded-full bg-amber-400/80 mb-6 mr-6" />
                                </div>
                                <div>
                                    <h4 className="text-xs font-bold text-slate-100">{trabajo.titulo}</h4>
                                    <p className="text-[10px] text-slate-400 mt-0.5">{trabajo.subtitulo}</p>
                                </div>
                                <div className="flex items-center justify-between mt-3 pt-2 border-t border-slate-800/60">
                                    <span className="text-[9px] text-slate-500">{trabajo.fecha}</span>
                                    {trabajo.esPro && (
                                        <span className="text-[9px] px-1.5 py-0.5 rounded bg-amber-950/60 text-amber-400 border border-amber-500/40 font-bold">
                      PRO
                    </span>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </section>

                {/* Certificados y Licencias */}
                <section className="mt-6">
                    <button
                        onClick={() => setDesplegarCertificados(!desplegarCertificados)}
                        className="w-full flex items-center justify-between p-3.5 bg-[#111a2c] border border-slate-800/80 rounded-xl text-xs font-medium text-slate-200"
                    >
            <span className="flex items-center gap-2">
              <Award size={15} className="text-amber-400" />
              Ver Certificados y Licencias ({MOCK_PERFIL.certificados.length})
            </span>
                        <ChevronDown size={16} className={`transition-transform ${desplegarCertificados ? 'rotate-180' : ''}`} />
                    </button>

                    {desplegarCertificados && (
                        <div className="mt-2 space-y-2">
                            {MOCK_PERFIL.certificados.map((cert) => (
                                <div key={cert.id} className="p-3 bg-[#0d1524] border border-slate-800/60 rounded-xl flex items-center justify-between">
                                    <div>
                                        <h5 className="text-xs font-bold text-slate-200">{cert.titulo}</h5>
                                        <p className="text-[10px] text-slate-400">{cert.institucion} • {cert.vencimiento}</p>
                                    </div>
                                    <CheckCircle2 size={16} className="text-emerald-400" />
                                </div>
                            ))}
                        </div>
                    )}
                </section>

            </div>
        </div>
    );
};