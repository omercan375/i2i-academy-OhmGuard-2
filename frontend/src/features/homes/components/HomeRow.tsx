import { Link } from "react-router-dom";
import { Cpu } from "lucide-react";
import type { DashboardHomeDto } from "@/types/dto";
import { statusOf } from "@/types/dto";
import { STATUS_META } from "@/lib/status";
import { StatusChip } from "@/components/ui/status-chip";
import { ThresholdMeter } from "@/components/ui/threshold-meter";
import { formatCurrency, formatKwh } from "@/lib/utils";

export function HomeRow({ home }: { home: DashboardHomeDto }) {
  const status = statusOf(home);
  const meta = STATUS_META[status];

  return (
    <Link
      to={`/panel/evler/${home.homeId}`}
      className="grid grid-cols-1 gap-3 px-4 py-4 transition-colors hover:bg-surface-sunken focus-visible:outline-2 focus-visible:-outline-offset-2 focus-visible:outline-brand-500 sm:grid-cols-[1.5fr_1.1fr_1.3fr_1fr_0.8fr] sm:items-center sm:gap-4"
    >
      <div className="min-w-0">
        <p className="truncate text-base font-bold text-ink-900">{home.name}</p>
        <p className="truncate text-xs text-ink-400">{home.contactEmail}</p>
      </div>

      <div className="flex items-center gap-2 sm:justify-self-start">
        <StatusChip status={status} />
      </div>

      <div>
        <ThresholdMeter percent={home.usagePercent} status={status} size="sm" />
      </div>

      <div className="tabular-nums">
        <p className="text-lg font-bold text-ink-900">{formatCurrency(home.accumulatedCost)}</p>
        <p className="text-xs text-ink-400">/ {formatCurrency(home.budgetLimit)}</p>
      </div>

      <div className="flex items-center justify-between text-xs text-ink-400 sm:flex-col sm:items-end sm:justify-center sm:gap-0.5">
        <span className="flex items-center gap-1.5">
          <Cpu className="size-3.5" aria-hidden="true" />
          {home.applianceCount} cihaz
        </span>
        <span className="tabular-nums">{formatKwh(home.accumulatedKwh)}</span>
      </div>

      {!home.live && (
        <p className="text-[11px] font-medium text-ink-400 sm:col-span-5">Henüz canlı veri alınmadı.</p>
      )}
      {home.live && status !== "ok" && (
        <p className={`text-[11px] font-medium sm:col-span-5 ${meta.textClass}`}>{meta.description}</p>
      )}
    </Link>
  );
}
