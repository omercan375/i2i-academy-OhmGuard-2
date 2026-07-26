import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { useDailyHistory } from "@/features/homes/hooks";
import { Skeleton } from "@/components/ui/skeleton";
import { ErrorState } from "@/components/ui/error-state";
import { EmptyState } from "@/components/ui/empty-state";
import { formatCurrency, formatDate, formatKwh } from "@/lib/utils";
import { LineChart as ChartIcon } from "lucide-react";

interface ChartTooltipProps {
  active?: boolean;
  payload?: Array<{ payload: { recordedAt: string; energyKwh: number; totalCost: number } }>;
}

function ChartTooltip({ active, payload }: ChartTooltipProps) {
  if (!active || !payload?.length) return null;
  const point = payload[0].payload;
  return (
    <div className="rounded-md border border-border bg-surface px-3 py-2 text-xs shadow-[var(--shadow-panel)]">
      <p className="font-medium text-ink-900">{formatDate(point.recordedAt)}</p>
      <p className="mt-0.5 tabular-nums text-data-600">{formatKwh(point.energyKwh)}</p>
      <p className="tabular-nums text-ink-400">{formatCurrency(point.totalCost)}</p>
    </div>
  );
}

export function DailyChart({ homeId }: { homeId: string }) {
  const { data, isLoading, isError, refetch } = useDailyHistory(homeId);

  if (isLoading) {
    return <Skeleton className="h-52 w-full" />;
  }

  if (isError) {
    return (
      <ErrorState
        compact
        message="Günlük tüketim grafiği yüklenemedi."
        onRetry={() => refetch()}
      />
    );
  }

  if (!data || data.length === 0) {
    return (
      <EmptyState
        icon={<ChartIcon className="size-5" aria-hidden="true" />}
        title="Henüz günlük özet oluşmadı"
        description="Sistem her gece yarısı bir önceki günün özetini kaydeder. Bu ev bugün eklendiyse ilk nokta yarın burada görünür."
        className="py-10"
      />
    );
  }

  return (
    <div className="h-52 w-full rounded-md border border-border p-2">
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={data} margin={{ top: 8, right: 8, left: -16, bottom: 0 }}>
          <CartesianGrid vertical={false} stroke="var(--color-border)" />
          <XAxis
            dataKey="recordedAt"
            tickFormatter={(value) => formatDate(value)}
            tick={{ fontSize: 11, fill: "var(--color-ink-400)" }}
            axisLine={{ stroke: "var(--color-border)" }}
            tickLine={false}
          />
          <YAxis
            tick={{ fontSize: 11, fill: "var(--color-ink-400)" }}
            axisLine={false}
            tickLine={false}
            width={44}
          />
          <Tooltip content={<ChartTooltip />} />
          <Area
            type="monotone"
            dataKey="energyKwh"
            stroke="var(--color-data-500)"
            strokeWidth={1.75}
            fill="var(--color-data-400)"
            fillOpacity={0.14}
            isAnimationActive={false}
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}
