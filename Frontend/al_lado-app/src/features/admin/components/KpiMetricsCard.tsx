interface KpiMetricsCardProps {
  titulo: string;
  valor: number | string;
  icono?: string;
}

export function KpiMetricsCard({ titulo, valor, icono }: KpiMetricsCardProps) {
  return (
    <div className="bg-[#1a1a1c] border border-[#2e2e33] rounded-2xl p-5">
      <div className="flex items-center justify-between">
        <span className="text-sm font-medium text-zinc-500">{titulo}</span>
        {icono && <span className="text-xl">{icono}</span>}
      </div>
      <div className="mt-2 text-3xl font-extrabold text-white">{valor}</div>
    </div>
  );
}
