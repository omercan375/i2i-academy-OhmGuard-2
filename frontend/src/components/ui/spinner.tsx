import { Loader2 } from "lucide-react";
import { cn } from "@/lib/utils";

export function Spinner({ className, label = "Yükleniyor" }: { className?: string; label?: string }) {
  return (
    <div className="flex items-center justify-center gap-2 text-ink-400" role="status">
      <Loader2 className={cn("size-5 animate-spin", className)} aria-hidden="true" />
      <span className="sr-only">{label}</span>
    </div>
  );
}
