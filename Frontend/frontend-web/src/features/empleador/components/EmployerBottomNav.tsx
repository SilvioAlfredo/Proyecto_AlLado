import type { EmployerScreen } from "../types/employer.types";

interface Props { screen: EmployerScreen; onNavigate: (screen: EmployerScreen) => void; }

export function EmployerBottomNav({ screen, onNavigate }: Props) {
  return <nav className="absolute bottom-0 left-0 right-0 z-20 flex h-20 items-end justify-around border-t border-[#2E2E2E] bg-[#111111] px-4 pb-3">
    <button type="button" onClick={() => onNavigate("dashboard")}
      className={`flex flex-col items-center gap-1 text-xs ${screen === "dashboard" ? "text-[#FFC107]" : "text-[#8A8A8A]"}`}>
      <span className="text-2xl">⌂</span>Inicio
    </button>
    <button type="button" onClick={() => onNavigate("new-request")}
      className="flex flex-col items-center gap-1 text-xs text-[#F5F5F5]">
      <span className="mt-[-28px] flex h-14 w-14 items-center justify-center rounded-full bg-[#FFC107] text-3xl text-[#0F0F0F]">+</span>
      Publicar
    </button>
    <button type="button" onClick={() => onNavigate("applicants")}
      className={`flex flex-col items-center gap-1 text-xs ${screen === "applicants" ? "text-[#FFC107]" : "text-[#8A8A8A]"}`}>
      <span className="text-2xl">▤</span>Contratos
    </button>
  </nav>;
}
