'use client';

export default function ForoLoading() {
  return (
    <div className="space-y-6 max-w-4xl mx-auto py-12">
      <h1 className="text-2xl font-extrabold text-white">Foro Comunitario</h1>
      <div className="space-y-4">
        {[1, 2, 3].map((i) => (
          <div key={i} className="border border-[#2e2e33] rounded-2xl p-5 bg-[#1a1a1c] space-y-3 animate-pulse">
            <div className="h-6 bg-[#2a2a2e] rounded w-3/4"></div>
            <div className="h-4 bg-[#2a2a2e] rounded w-1/2"></div>
            <div className="h-4 bg-[#2a2a2e] rounded w-1/3"></div>
          </div>
        ))}
      </div>
    </div>
  );
}
