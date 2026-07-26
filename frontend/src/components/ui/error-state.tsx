import { AlertTriangle, RotateCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export function ErrorState({
  message,
  onRetry,
  className,
  compact = false,
}: {
  message: string;
  onRetry?: () => void;
  className?: string;
  compact?: boolean;
}) {
  return (
    <div
      role="alert"
      className={cn(
        "flex flex-col items-center justify-center gap-3 rounded-md border border-danger-100 bg-danger-50 text-center",
        compact ? "px-4 py-6" : "px-6 py-14",
        className,
      )}
    >
      <AlertTriangle className="size-6 text-danger-500" aria-hidden="true" />
      <p className="max-w-sm text-sm font-medium text-danger-600">{message}</p>
      {onRetry && (
        <Button variant="secondary" size="sm" onClick={onRetry}>
          <RotateCw className="size-3.5" aria-hidden="true" />
          Tekrar dene
        </Button>
      )}
    </div>
  );
}
