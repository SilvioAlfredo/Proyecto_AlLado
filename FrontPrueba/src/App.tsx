import { useState } from "react";
import "./App.css";
import { FeedPage } from "./pages/FeedPage";
import { CrearOfertaPage } from "./pages/CrearOfertaPage";
import { CrearPostulacionPage } from "./pages/CrearPostulacionPage";
import { PerfilTrabajadorPage } from "./pages/PerfilTrabajadorPage";

export default function App() {
  const [screen, setScreen] = useState<"inicio" | "nueva" | "postulacion" | "perfil-trabajador">("inicio");
  const [ofertaSeleccionada, setOfertaSeleccionada] = useState<any>(null);
  const [trabajadorId, setTrabajadorId] = useState<number | null>(null);

  const handleIrAPostular = (oferta: any) => {
    setOfertaSeleccionada(oferta);
    setScreen("postulacion");
  };

  const handleIrAPerfil = (id: number) => {
    setTrabajadorId(id);
    setScreen("perfil-trabajador");
  };

  const handleVolver = () => {
    setScreen("inicio");
  };

  return (
    <main className="app">
      {screen === "inicio" && (
        <FeedPage
          onSeleccionarOferta={handleIrAPostular}
          onIrACrear={() => setScreen("nueva")}
        />
      )}

      {screen === "nueva" && (
        <CrearOfertaPage
          onVolver={() => setScreen("inicio")}
          onExito={() => setScreen("inicio")}
        />
      )}

      {screen === "postulacion" && (
        <CrearPostulacionPage
          oportunidad={ofertaSeleccionada}
          onVolver={() => setScreen("inicio")}
          onExito={() => setScreen("inicio")}
        />
      )}

      {screen === "perfil-trabajador" && trabajadorId && (
        <PerfilTrabajadorPage trabajadorId={trabajadorId} onVolver={handleVolver} />
      )}
    </main>
  );
}
