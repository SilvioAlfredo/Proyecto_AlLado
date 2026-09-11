import { useState } from "react";
import "./App.css";

function App() {
  const [screen, setScreen] = useState<"inicio" | "nueva" | "postulacion">("inicio");
  const [titulo, setTitulo] = useState("");
  const [descripcion, setDescripcion] = useState("");
  const [especialidad, setEspecialidad] = useState("Electricidad");
  const [presupuesto, setPresupuesto] = useState("");
  const [disponibilidad, setDisponibilidad] = useState("");
  const [mensaje, setMensaje] = useState("");

  const handlePublicar = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setScreen("postulacion");
  };

  const handleVolver = () => {
    setScreen("inicio");
  };

  if (screen === "inicio") {
    return (
      <main className="app">
        <section className="inicio-screen">
          <header className="inicio-header">
            <span>AlLado</span>
            <span className="perfil" aria-label="Perfil de usuario" />
          </header>

          <select className="buscador" defaultValue="">
            <option value="">Buscar por Especialidad</option>
            <option>Electricidad</option>
            <option>Plomería</option>
            <option>Gas</option>
            <option>Albañilería</option>
            <option>Pintura</option>
          </select>

          <h1 className="publicaciones-title">Publicaciones</h1>

          <div className="publicaciones-list">
            <button
              className="publicacion-card publicacion-card-destacada"
              type="button"
              onClick={() => setScreen("postulacion")}
            >
              <span className="publicacion-avatar" />
              <span className="publicacion-info">
                <strong>Marta S.</strong>
                <small>Necesito un Plomero</small>
              </span>
            </button>
            <div className="publicacion-card" aria-hidden="true" />
            <div className="publicacion-card" aria-hidden="true" />
          </div>

          <button
            className="boton-agregar"
            type="button"
            aria-label="Crear solicitud"
            onClick={() => setScreen("nueva")}
          >
            +
          </button>

          <footer className="inicio-footer">Inicio</footer>
        </section>
      </main>
    );
  }

  if (screen === "postulacion") {
    return (
      <main className="app">
        <section className="postulacion-screen">
          <button className="volver" type="button" onClick={handleVolver}>
            <span className="volver-arrow">‹</span> Volver
          </button>

          <h1 className="screen-title">Postularse a la Solicitud</h1>

          <div className="detalle-box">
            <div className="detalle-content">
              {descripcion || "Necesidad: Reparación Panel LED\nPublicado por: Juan P. (Zona Norte)\nSe necesita cambio de térmicas..."}
            </div>
          </div>

          <label className="field-label">Presupuesto Estimado</label>
          <input
            className="text-input"
            type="text"
            value={presupuesto}
            onChange={(e) => setPresupuesto(e.target.value)}
            placeholder="$15.000"
          />

          <label className="field-label">Disponibilidad Estimada</label>
          <select
            className="select-input"
            value={disponibilidad}
            onChange={(e) => setDisponibilidad(e.target.value)}
          >
            <option value="">Seleccionar</option>
            <option value="8:00-9:00">8am - 9am</option>
            <option value="9:00-10:00">9am - 10am</option>
            <option value="10:00-11:00">10am - 11am</option>
            <option value="11:00-12:00">11am - 12pm</option>
            <option value="16:00-17:00">16hs - 17hs</option>
            <option value="17:00-18:00">17hs - 18hs</option>
            <option value="18:00-19:00">18hs - 19hs</option>
            <option value="19:00-20:00">19hs - 20hs</option>
          </select>

          <label className="field-label">Mensaje (Opcional)</label>
          <div className="mensaje-box">
            <textarea
              value={mensaje}
              onChange={(e) => setMensaje(e.target.value)}
              maxLength={500}
              placeholder="Escribí un mensaje para el cliente..."
            />
            <div className="contador">{mensaje.length}/500</div>
          </div>

          <button className="boton-enviar" type="button">
            Enviar Postulación
          </button>
        </section>
      </main>
    );
  }

  return (
    <main className="app">
      <section className="solicitud">
        <button className="volver" type="button" onClick={handleVolver}>
          <span className="volver-arrow">←</span> Volver
        </button>

        <form onSubmit={handlePublicar}>
          <input
            className="titulo"
            type="text"
            placeholder="Nueva Solicitud"
            value={titulo}
            onChange={(e) => setTitulo(e.target.value)}
          />

          <label htmlFor="descripcion">¿Qué necesitás?</label>

          <textarea
            id="descripcion"
            value={descripcion}
            onChange={(e) => setDescripcion(e.target.value)}
          />

          <label htmlFor="especialidad">Especialidad (Seleccionar)</label>

          <select
            id="especialidad"
            value={especialidad}
            onChange={(e) => setEspecialidad(e.target.value)}
          >
            <option>Electricidad</option>
            <option>Plomería</option>
            <option>Gas</option>
            <option>Albañilería</option>
            <option>Pintura</option>
          </select>

          <button className="publicar" type="submit">
            Publicar
          </button>
        </form>
      </section>
    </main>
  );
}

export default App;