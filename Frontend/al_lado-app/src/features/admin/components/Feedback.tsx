import { Button } from '@/components/ui/button';

export function Feedback({
  message,
  success = false,
}: {
  message: string;
  success?: boolean;
}) {
  const statusClasses = success
    ? 'border-emerald-500/30 bg-emerald-500/10 text-emerald-300'
    : 'border-red-500/30 bg-red-500/10 text-red-300';

  return (
    <p
      role={success ? 'status' : 'alert'}
      className={`rounded-xl border p-3 text-sm ${statusClasses}`}
    >
      {message}
    </p>
  );
}

export function LoadError({ message, retry }: { message: string; retry: () => void }) {
  return (
    <div className="space-y-3">
      <Feedback message={message} />
      <Button variant="outline" onClick={retry}>
        Volver a intentar
      </Button>
    </div>
  );
}
