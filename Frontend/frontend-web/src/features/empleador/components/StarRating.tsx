interface Props { value: number; count: number; }
export function StarRating({ value, count }: Props) {
  return <div className="flex items-center gap-1">
    <span className="text-[#FFC107]">★</span>
    <span className="text-sm font-semibold text-[#F5F5F5]">{value.toFixed(1)}</span>
    <span className="text-xs text-[#8A8A8A]">({count})</span>
  </div>;
}
