import { useEffect, useState } from "react";
import { categories, requests as mockRequests } from "../data/employer.mock";
import { oportunidadService, type OportunidadLaboralDTO } from "../services/oportunidadService";
import type { EmployerScreen } from "../types/employer.types";

interface Props { onNavigate: (screen: EmployerScreen) => void; }

export function EmployerDashboardPage({ onNavigate }: Props) {
  const [backendRequests, setBackendRequests] = useState<OportunidadLaboralDTO[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let mounted = true;
    const load = async () => {
      setLoading(true);
      try { const data = await oportunidadService.obtenerTodas(); if (mounted) setBackendRequests(data); }
      catch { /* mock fallback */ }
      finally { if (mounted) setLoading(false); }
    };
    void load();
    return () => { mounted = false; };
  }, []);

  const visible = backendRequests.length ? backendRequests.map(x => ({
    id: x.id ?? 0, title: x.titulo, category: x.etiquetas?.length ? "Especialidad" : "Servicio",
    description: x.contenido, distance: x.ubicacion ?? "Ubicación registrada",
    candidates: Array.isArray(x.postulaciones) ? x.postulaciones.length : 0,
    status: x.activa ? "active" as const : "closed" as const
  })) : mockRequests;

  return <div className="h-full overflow-y-auto pb-24">
    <header className="bg-[#0F0F0F] px-5 pb-5 pt-10">
      <div className="flex items-center justify-between">
        <div><p className="text-xs text-[#8A8A8A]">AlLado Servicios Locales</p>
          <h1 className="mt-1 text-[26px] font-bold text-[#F5F5F5]">Hola, Usuario</h1>
          <p className="mt-1 text-sm font-medium text-[#FFC107]">⌖ Villa Urquiza</p></div>
        <div className="flex h-11 w-11 items-center justify-center rounded-full border-2 border-[#FFC107] bg-[#272727] font-bold text-[#FFC107]">U</div>
      </div>
      <div className="mt-4 flex items-center gap-3 rounded-2xl border border-[#2E2E2E] bg-[#1E1E1E] px-4 py-3">
        <span className="text-lg text-[#8A8A8A]">⌕</span><span className="text-sm text-[#8A8A8A]">Buscar electricista, plomero, hogar...</span>
      </div>
    </header>
    <section className="pt-2">
      <div className="mb-3 flex items-center justify-between px-5"><h2 className="text-base font-semibold text-[#F5F5F5]">Categorías populares</h2><span className="text-sm text-[#FFC107]">Ver todas →</span></div>
      <div className="flex gap-3 overflow-x-auto px-5 pb-2">
        {categories.map(c => <button key={c.name} type="button" className="min-w-[120px] rounded-2xl border border-[#2E2E2E] bg-[#1E1E1E] p-4 text-left">
          <div className="mb-2 text-2xl">{c.icon}</div><div className="text-sm font-semibold text-[#F5F5F5]">{c.name}</div><div className="mt-1 text-xs font-medium text-[#FFC107]">{c.active} activos</div>
        </button>)}
      </div>
    </section>
    <section className="mt-6 px-5">
      <div className="mb-3 flex items-center justify-between"><h2 className="text-base font-semibold text-[#F5F5F5]">Mis solicitudes activas</h2>{loading && <span className="text-xs text-[#8A8A8A]">Cargando...</span>}</div>
      <div className="flex flex-col gap-3">
        {visible.map(r => <button key={r.id} type="button" onClick={() => onNavigate("applicants")} className="w-full rounded-[18px] border border-[#2E2E2E] bg-[#1E1E1E] p-[18px] text-left">
          <div className="flex items-start justify-between gap-3"><div className="min-w-0 flex-1">
            <span className="rounded-md bg-[#FFC107]/10 px-2 py-1 text-[11px] font-semibold text-[#FFC107]">{r.category}</span>
            <h3 className="mt-2 text-[15px] font-bold text-[#F5F5F5]">{r.title}</h3><p className="mt-1 text-xs leading-5 text-[#8A8A8A]">{r.description}</p>
          </div><span className="shrink-0 rounded-xl bg-[#FFC107] px-3 py-2 text-xs font-extrabold text-[#0F0F0F]">{r.candidates} postulados</span></div>
          <div className="mt-3 flex items-center justify-between border-t border-[#2E2E2E] pt-3 text-xs"><span className="text-[#4CAF50]">● {r.status === "active" ? "Activa" : "Cerrada"} · {r.distance}</span><span className="font-semibold text-[#FFC107]">Ver postulantes →</span></div>
        </button>)}
      </div>
    </section>
  </div>;
}
