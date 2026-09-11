import { useState } from "react";
import "./App.css";
import { FeedPage } from "./pages/FeedPage";
import { CrearOfertaPage } from "./pages/CrearOfertaPage";
import { CrearPostulacionPage } from "./pages/CrearPostulacionPage";

export default function App() {
  const [screen, setScreen] = useState<"inicio" | "nueva" | "postulacion">("inicio");
  const [ofertaSeleccionada, setOfertaSeleccionada] = useState<any>(null);

  const handleIrAPostular = (oferta: any) => {
    setOfertaSeleccionada(oferta);
    setScreen("postulacion");
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
    </main>
  );
}
