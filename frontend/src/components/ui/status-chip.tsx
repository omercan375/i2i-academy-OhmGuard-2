import type { HomeStatus } from "@/types/dto";
import { STATUS_META } from "@/lib/status";
import { cn } from "@/lib/utils";

export function StatusChip({ status, className }: { status: HomeStatus; className?: string }) {
  const meta = STATUS_META[status];
  const Icon = meta.icon;
  return (
    <span
      className={cn(
        "inline-flex items-center gap-1.5 rounded-sm px-2.5 py-1 text-sm font-semibold",
        meta.chipClass,
        className,
      )}
    >
      <Icon className="size-3.5" aria-hidden="true" />
      {meta.label}
    </span>
  );
}
