import { useEffect, useState } from "react";
import { applicants as mockApplicants } from "../data/employer.mock";
import { postulacionService, type PostulacionDTO } from "../services/postulacionService";
import { StarRating } from "../components/StarRating";
import type { EmployerScreen } from "../types/employer.types";

interface Props { onNavigate: (screen: EmployerScreen) => void; }

export function ApplicantsPage({ onNavigate }: Props) {
  const [backendApplicants, setBackendApplicants] = useState<PostulacionDTO[]>([]);
  const [accepted, setAccepted] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let mounted = true;
    const load = async () => {
      setLoading(true);
      try { const data = await postulacionService.obtenerTodas(); if (mounted) setBackendApplicants(data); }
      catch { /* mock fallback */ }
      finally { if (mounted) setLoading(false); }
    };
    void load();
    return () => { mounted = false; };
  }, []);

  const displayed = backendApplicants.length ? backendApplicants.map(x => ({
    id: x.id ?? 0, initials: (x.autor.nombre ?? x.autor.email ?? "TR").split(/\s+/).map(p => p[0]).join("").slice(0,2).toUpperCase(),
    name: x.autor.nombre ?? x.autor.email ?? "Trabajador", verified: false, pro: false, rating: 0, reviews: 0,
    proposal: x.contenido, budget: x.remuneracionPretendida ? `$${x.remuneracionPretendida.toLocaleString("es-AR")}` : "A convenir",
    availability: "Consultar disponibilidad"
  })) : mockApplicants;

  const accept = async (id: number) => {
    try { await postulacionService.seleccionar(id); } catch { /* visual fallback */ }
    setAccepted(id);
  };

  return <div className="h-full overflow-y-auto px-5 pb-24 pt-10">
    <button type="button" onClick={() => onNavigate("dashboard")} className="mb-4 text-2xl text-[#F5F5F5]" aria-label="Volver">←</button>
    <h1 className="text-[22px] font-bold text-[#F5F5F5]">Postulantes recibidos</h1>
    <p className="mt-1 text-sm text-[#8A8A8A]">Para tu solicitud de:</p>
    <h2 className="text-base font-semibold text-[#F5F5F5]">Reparación de panel LED</h2>
    <div className="mt-4 flex gap-2"><span className="rounded-lg bg-[#FFC107]/10 px-3 py-2 text-xs font-semibold text-[#FFC107]">{displayed.length} postulantes</span>{loading && <span className="py-2 text-xs text-[#8A8A8A]">Cargando...</span>}</div>
    <div className="mt-5 flex flex-col gap-4">
      {displayed.map(a => <article key={a.id} className={`rounded-[20px] border bg-[#1E1E1E] p-5 ${accepted === a.id ? "border-[#FFC107]" : "border-[#2E2E2E]"}`}>
        <div className="flex items-start justify-between gap-3"><div className="flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-full bg-[#272727] text-sm font-bold text-[#F5F5F5]">{a.initials}</div>
          <div><h3 className="font-bold text-[#F5F5F5]">{a.name}</h3><div className="mt-1 flex flex-wrap gap-1">
            {a.verified && <span className="rounded-full bg-[#1B5E20] px-2 py-1 text-[10px] font-semibold text-white">✓ Vecino Verificado</span>}
            {a.pro && <span className="rounded-full bg-[#FFC107]/20 px-2 py-1 text-[10px] font-semibold text-[#FFC107]">★ Pro</span>}
          </div></div>
        </div>{a.rating > 0 && <StarRating value={a.rating} count={a.reviews}/>}</div>
        <p className="mt-4 text-sm leading-5 text-[#BDBDBD]">“{a.proposal}”</p>
        <div className="mt-4 grid grid-cols-2 gap-2"><div className="rounded-xl bg-[#272727] p-3"><p className="text-[10px] uppercase text-[#8A8A8A]">Presupuesto</p><p className="mt-1 font-bold text-[#F5F5F5]">{a.budget}</p></div>
          <div className="rounded-xl bg-[#272727] p-3"><p className="text-[10px] uppercase text-[#8A8A8A]">Disponibilidad</p><p className="mt-1 text-xs font-semibold text-[#F5F5F5]">{a.availability}</p></div></div>
        <div className="mt-4 grid grid-cols-2 gap-2"><button type="button" className="rounded-xl border border-[#FFC107] py-3 font-semibold text-[#F5F5F5]">Chat</button>
          <button type="button" onClick={() => void accept(a.id)} className="rounded-xl bg-[#FFC107] py-3 font-bold text-[#0F0F0F]">{accepted === a.id ? "Propuesta aceptada" : "Aceptar propuesta"}</button></div>
      </article>)}
    </div>
  </div>;
}
