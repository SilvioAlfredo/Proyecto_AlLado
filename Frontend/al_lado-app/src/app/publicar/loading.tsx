'use client';

export default function PublicarLoading() {
  return (
    <div className="max-w-2xl mx-auto py-12 px-4">
      <h1 className="text-2xl font-extrabold text-white mb-6">Publicar oportunidad</h1>
      <div className="rounded-xl border border-[#2e2e33] bg-[#1a1a1c] p-6 space-y-4 animate-pulse">
        <div className="h-10 bg-[#2a2a2e] rounded-full w-full"></div>
        <div className="h-40 bg-[#2a2a2e] rounded-xl w-full"></div>
        <div className="h-10 bg-[#2a2a2e] rounded-full w-3/4"></div>
        <button className="h-11 w-full rounded-full bg-allado-yellow text-black font-bold"></button>
      </div>
    </div>
  );
}
