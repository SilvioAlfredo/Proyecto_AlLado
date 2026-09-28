import React, { useState } from 'react';
import { InicioTrabajador } from './pages/InicioTrabajador';
import { PerfilTrabajador } from './pages/PerfilTrabajador';
import { EstadisticasTrabajador } from './pages/EstadisticasTrabajador';
import { PostulacionTrabajador } from './pages/PostulacionTrabajador';
import { BottomNav } from './components/layout/BottomNav';

export const App: React.FC = () => {
  const [vistaActual, setVistaActual] = useState<'inicio' | 'metricas' | 'trabajos' | 'perfil'>('inicio');

  return (
      <div className="min-h-screen bg-[#070d18] text-white">
        <main>
          {vistaActual === 'inicio' && (
              <InicioTrabajador alIrAPostular={() => setVistaActual('trabajos')} />
          )}
          {vistaActual === 'metricas' && <EstadisticasTrabajador />}
          {vistaActual === 'trabajos' && <PostulacionTrabajador />}
          {vistaActual === 'perfil' && <PerfilTrabajador />}
        </main>

        {/* Barra de navegación inferior */}
        <BottomNav vistaActual={vistaActual} alCambiarVista={setVistaActual} />
      </div>
  );
};

export default App;