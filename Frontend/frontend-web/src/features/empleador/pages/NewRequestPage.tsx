import { useState } from "react";
import { oportunidadService } from "../services/oportunidadService";
import { specialties } from "../data/employer.mock";
import type { EmployerScreen, NewRequestForm } from "../types/employer.types";

interface Props { onNavigate: (screen: EmployerScreen) => void; }

export function NewRequestPage({ onNavigate }: Props) {
  const [form, setForm] = useState<NewRequestForm>({ description: "", specialty: "Electricidad", location: "Villa Urquiza, CABA" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const update = (field: keyof NewRequestForm, value: string) => setForm(current => ({ ...current, [field]: value }));
  const canPublish = form.description.trim().length >= 10;

  const publish = async () => {
    if (!canPublish) return;
    setError(""); setLoading(true);
    try {
      const stored = localStorage.getItem("alLado_user");
      const user = stored ? JSON.parse(stored) : null;
      if (!user?.usuarioId) throw new Error("No se encontró el usuario autenticado. Iniciá sesión nuevamente.");
      await oportunidadService.crear({
        titulo: `${form.specialty}: nueva solicitud`, contenido: form.description, ubicacion: form.location,
        modalidad: "PRESENCIAL", rangoSalarial: "", activa: true, autor: { id: Number(user.usuarioId) }
      });
      onNavigate("dashboard");
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo publicar la solicitud.");
    } finally { setLoading(false); }
  };

  return <div className="h-full overflow-y-auto px-5 pb-28 pt-10">
    <button type="button" onClick={() => onNavigate("dashboard")} className="mb-3 text-2xl text-[#F5F5F5]" aria-label="Volver">←</button>
    <h1 className="text-[26px] font-bold text-[#F5F5F5]">Nueva solicitud</h1>
    <p className="mt-1 text-sm leading-5 text-[#8A8A8A]">Contanos qué necesitás y recibí propuestas de profesionales cerca tuyo.</p>
    <div className="mt-6 rounded-2xl border border-[#2E2E2E] bg-[#1E1E1E] p-4">
      <label htmlFor="description" className="text-base font-semibold text-[#F5F5F5]">¿Qué necesitás?</label>
      <textarea id="description" value={form.description} onChange={e => update("description", e.target.value)} maxLength={500}
        placeholder="Describí el problema o trabajo que necesitás..."
        className="mt-3 h-36 w-full resize-none rounded-xl border border-[#3A3A3A] bg-[#272727] p-4 text-sm text-[#F5F5F5] outline-none placeholder:text-[#777] focus:border-[#FFC107]" />
      <div className="mt-1 text-right text-xs text-[#8A8A8A]">{form.description.length}/500</div>
      <label htmlFor="specialty" className="mt-5 block text-base font-semibold text-[#F5F5F5]">Especialidad</label>
      <select id="specialty" value={form.specialty} onChange={e => update("specialty", e.target.value)}
        className="mt-3 w-full rounded-xl border border-[#3A3A3A] bg-[#272727] p-4 text-sm text-[#F5F5F5] outline-none focus:border-[#FFC107]">
        {specialties.map(s => <option key={s} value={s}>{s}</option>)}
      </select>
      <div className="mt-3 flex flex-wrap gap-2">{specialties.slice(0,8).map(s => <button key={s} type="button" onClick={() => update("specialty", s)}
        className={`rounded-full border px-3 py-2 text-xs ${form.specialty === s ? "border-[#FFC107] bg-[#FFC107]/10 text-[#FFC107]" : "border-[#3A3A3A] bg-[#272727] text-[#BDBDBD]"}`}>{s}</button>)}</div>
      <label htmlFor="location" className="mt-5 block text-base font-semibold text-[#F5F5F5]">Ubicación</label>
      <input id="location" value={form.location} onChange={e => update("location", e.target.value)}
        className="mt-3 w-full rounded-xl border border-[#3A3A3A] bg-[#272727] p-4 text-sm text-[#F5F5F5] outline-none focus:border-[#FFC107]" />
      {error && <div className="mt-4 rounded-xl border border-red-900 bg-red-950/40 p-3 text-sm text-red-300">{error}</div>}
      <button type="button" disabled={!canPublish || loading} onClick={() => void publish()}
        className="mt-6 w-full rounded-xl bg-[#FFC107] py-4 font-bold text-[#0F0F0F] disabled:cursor-not-allowed disabled:opacity-40">
        {loading ? "Publicando..." : "Publicar solicitud"}
      </button>
    </div>
  </div>;
}
