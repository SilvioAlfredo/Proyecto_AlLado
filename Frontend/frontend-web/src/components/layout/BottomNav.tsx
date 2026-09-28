import React from 'react';
import { Home, BarChart2, Briefcase, User } from 'lucide-react';

interface BottomNavProps {
    vistaActual: 'inicio' | 'metricas' | 'trabajos' | 'perfil';
    alCambiarVista: (vista: 'inicio' | 'metricas' | 'trabajos' | 'perfil') => void;
}

export const BottomNav: React.FC<BottomNavProps> = ({ vistaActual, alCambiarVista }) => {
    return (
        <nav className="fixed bottom-0 left-0 right-0 bg-[#0b1320]/95 backdrop-blur-md border-t border-slate-800/70 py-2.5 px-6 z-50 flex justify-around items-center max-w-md md:max-w-2xl mx-auto">
            <button
                type="button"
                onClick={() => alCambiarVista('inicio')}
                className={`flex flex-col items-center gap-1 text-[10px] ${
                    vistaActual === 'inicio' ? 'text-amber-400 font-semibold' : 'text-slate-400'
                }`}
            >
                <Home size={18} />
                <span>Inicio</span>
            </button>

            <button
                type="button"
                onClick={() => alCambiarVista('metricas')}
                className={`flex flex-col items-center gap-1 text-[10px] ${
                    vistaActual === 'metricas' ? 'text-amber-400 font-semibold' : 'text-slate-400'
                }`}
            >
                <BarChart2 size={18} />
                <span>Métricas</span>
            </button>

            <button
                type="button"
                onClick={() => alCambiarVista('trabajos')}
                className={`flex flex-col items-center gap-1 text-[10px] ${
                    vistaActual === 'trabajos' ? 'text-amber-400 font-semibold' : 'text-slate-400'
                }`}
            >
                <Briefcase size={18} />
                <span>Trabajos</span>
            </button>

            <button
                type="button"
                onClick={() => alCambiarVista('perfil')}
                className={`flex flex-col items-center gap-1 text-[10px] ${
                    vistaActual === 'perfil' ? 'text-amber-400 font-semibold' : 'text-slate-400'
                }`}
            >
                <User size={18} />
                <span>Mi Perfil</span>
            </button>
        </nav>
    );
};