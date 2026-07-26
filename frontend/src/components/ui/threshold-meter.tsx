import * as ProgressPrimitive from "@radix-ui/react-progress";
import { STATUS_META } from "@/lib/status";
import type { HomeStatus } from "@/types/dto";
import { cn, clamp } from "@/lib/utils";

export function ThresholdMeter({
  percent,
  status,
  size = "md",
  showValue = true,
  className,
}: {
  percent: number;
  status: HomeStatus;
  size?: "sm" | "md";
  showValue?: boolean;
  className?: string;
}) {
  const meta = STATUS_META[status];
  const visualPercent = clamp(percent, 0, 100);
  const height = size === "sm" ? "h-1.5" : "h-2";

  return (
    <div className={cn("flex items-center gap-2.5", className)}>
      <ProgressPrimitive.Root
        value={Math.round(visualPercent)}
        max={100}
        getValueLabel={() => `%${Math.round(percent)}`}
        className={cn("meter-track flex-1", height)}
        aria-label="Bütçe kullanım oranı"
      >
        <ProgressPrimitive.Indicator
          className={cn("meter-fill", meta.fillClass)}
          style={{ width: `${visualPercent}%` }}
        />
        <span className="meter-tick" style={{ left: "80%" }} aria-hidden="true" />
      </ProgressPrimitive.Root>
      {showValue && (
        <span className={cn("w-10 shrink-0 text-right text-xs font-semibold tabular-nums", meta.textClass)}>
          %{Math.round(percent)}
        </span>
      )}
    </div>
  );
}
