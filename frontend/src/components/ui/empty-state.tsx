import type { ReactNode } from "react";
import { cn } from "@/lib/utils";

export function EmptyState({
  icon,
  title,
  description,
  action,
  className,
}: {
  icon?: ReactNode;
  title: string;
  description?: string;
  action?: ReactNode;
  className?: string;
}) {
  return (
    <div
      className={cn(
        "flex flex-col items-center justify-center gap-3 rounded-md border border-dashed border-border-strong bg-surface px-6 py-14 text-center",
        className,
      )}
    >
      {icon && (
        <div className="flex size-11 items-center justify-center rounded-full bg-brand-50 text-brand-600">
          {icon}
        </div>
      )}
      <div className="space-y-1">
        <p className="text-sm font-semibold text-ink-900">{title}</p>
        {description && <p className="max-w-sm text-sm text-ink-400">{description}</p>}
      </div>
      {action}
    </div>
  );
}
