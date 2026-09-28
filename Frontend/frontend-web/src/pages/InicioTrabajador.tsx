import React from 'react';
import { Bell, MapPin, Clock, Search, AlertCircle, Wrench } from 'lucide-react';

interface PedidoVecino {
    id: number;
    nombre: string;
    iniciales: string;
    barrio: string;
    haceCuanto: string;
    urgente: boolean;
    titulo: string;
    descripcion: string;
    presupuesto: string;
}

const PEDIDOS_DISPONIBLES: PedidoVecino[] = [
    {
        id: 1,
        nombre: 'Mariana Gómez',
        iniciales: 'MG',
        barrio: 'Barrio Las Rosas',
        haceCuanto: 'Hace 2 horas',
        urgente: true,
        titulo: 'Cambio térmico y recableado taller',
        descripcion: 'Salta la térmica principal al conectar soldadora. Se requiere electricista con instrumental para revisar.',
        presupuesto: 'A convenir',
    },
    {
        id: 2,
        nombre: 'Lucas Benítez',
        iniciales: 'LB',
        barrio: 'Centro',
        haceCuanto: 'Hace 4 horas',
        urgente: false,
        titulo: 'Instalación de apliques LED en pasillo',
        descripcion: 'Colocación de 4 artefactos embutidos y llave conmutadora simple.',
        presupuesto: '$28.000',
    },
    {
        id: 3,
        nombre: 'Valeria Rossi',
        iniciales: 'VR',
        barrio: 'Alberdi',
        haceCuanto: 'Hace 5 horas',
        urgente: false,
        titulo: 'Revisión toma de fuerza lavarropas',
        descripcion: 'Enchufe recalentado, hay olor a plástico al centrifugar.',
        presupuesto: '$22.000',
    },
];

interface InicioTrabajadorProps {
    alIrAPostular: () => void;
}

export const InicioTrabajador: React.FC<InicioTrabajadorProps> = ({ alIrAPostular }) => {
    return (
        <div className="min-h-screen bg-[#070d18] text-white flex justify-center pb-24">
            <div className="w-full max-w-md md:max-w-2xl px-5 pt-8">
                {/* Header superior */}
                <header className="flex items-center justify-between mb-5">
                    <div className="flex items-center gap-2">
                        <div className="w-8 h-8 rounded-full bg-amber-500/20 border border-amber-500/40 flex items-center justify-center text-amber-400">
                            <Wrench size={16} />
                        </div>
                        <div>
                            <h1 className="text-sm font-bold text-white leading-tight">AlLado Servicios</h1>
                            <p className="text-[10px] text-slate-400 flex items-center gap-1">
                                <MapPin size={11} className="text-amber-400" /> Radio activo: 3 km
                            </p>
                        </div>
                    </div>
                    <button type="button" className="p-2.5 bg-[#131f33] rounded-full text-slate-300 relative">
                        <Bell size={18} />
                        <span className="w-2 h-2 rounded-full bg-amber-400 absolute top-2 right-2" />
                    </button>
                </header>

                {/* Barra de búsqueda */}
                <div className="relative mb-5">
                    <Search size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Buscar por zona, rubro o necesidad..."
                        className="w-full bg-[#111a2c] border border-slate-800 rounded-xl py-2.5 pl-10 pr-4 text-xs text-slate-200 placeholder-slate-500 outline-none focus:border-amber-500/60"
                    />
                </div>

                {/* Título de sección */}
                <div className="flex items-center justify-between mb-3">
                    <h2 className="text-xs font-bold text-slate-200 uppercase tracking-wide">
                        Solicitudes en tu zona ({PEDIDOS_DISPONIBLES.length})
                    </h2>
                    <span className="text-[10px] text-amber-400 font-medium">Actualizado recién</span>
                </div>

                {/* Lista de solicitudes */}
                <div className="space-y-3">
                    {PEDIDOS_DISPONIBLES.map((pedido) => (
                        <article
                            key={pedido.id}
                            className="bg-[#111a2c] border border-slate-800/80 rounded-2xl p-4 transition-colors hover:border-slate-700"
                        >
                            <div className="flex items-start justify-between">
                                <div className="flex items-center gap-2.5">
                                    <div className="w-9 h-9 rounded-full bg-slate-800 flex items-center justify-center text-xs font-bold text-slate-300">
                                        {pedido.iniciales}
                                    </div>
                                    <div>
                                        <h3 className="text-xs font-bold text-slate-100">{pedido.nombre}</h3>
                                        <p className="text-[10px] text-slate-400">{pedido.barrio} • {pedido.haceCuanto}</p>
                                    </div>
                                </div>

                                {pedido.urgente && (
                                    <span className="flex items-center gap-1 text-[9px] font-bold px-2 py-0.5 rounded-full bg-amber-950/70 text-amber-400 border border-amber-500/40 uppercase">
                    <AlertCircle size={10} /> Urgente
                  </span>
                                )}
                            </div>

                            <div className="mt-2.5">
                                <h4 className="text-xs font-bold text-amber-400">{pedido.titulo}</h4>
                                <p className="text-xs text-slate-300 mt-1 leading-relaxed">
                                    "{pedido.descripcion}"
                                </p>
                            </div>

                            <div className="flex items-center justify-between mt-3 pt-3 border-t border-slate-800/60 text-xs">
                <span className="text-slate-400 text-[11px]">
                  Presupuesto: <strong className="text-slate-200">{pedido.presupuesto}</strong>
                </span>
                                <button
                                    type="button"
                                    onClick={alIrAPostular}
                                    className="px-3 py-1.5 rounded-lg text-xs font-semibold bg-amber-500/20 text-amber-300 border border-amber-500/40 hover:bg-amber-500 hover:text-slate-950 transition-all"
                                >
                                    Postularse →
                                </button>
                            </div>
                        </article>
                    ))}
                </div>
            </div>
        </div>
    );
};