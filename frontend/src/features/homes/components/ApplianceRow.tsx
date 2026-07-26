import { Radar, Trash2 } from "lucide-react";
import type { ApplianceStatusDto } from "@/types/dto";
import { Button } from "@/components/ui/button";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { EditApplianceLimitDialog } from "@/features/appliances/components/EditApplianceLimitDialog";
import { cn, clamp, formatWatt } from "@/lib/utils";

export function ApplianceRow({
  homeId,
  appliance,
  onDelete,
  deleting,
}: {
  homeId: string;
  appliance: ApplianceStatusDto;
  onDelete: () => void;
  deleting: boolean;
}) {
  const ratio = appliance.safeWattLimit > 0 ? clamp((appliance.lastMeasuredWatt / appliance.safeWattLimit) * 100, 0, 100) : 0;

  return (
    <div
      className={cn(
        "flex items-center gap-3 border-l-2 bg-surface px-3 py-2.5",
        appliance.anomalous ? "border-l-anomaly-500" : "border-l-transparent",
      )}
    >
      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-2">
          <p className="truncate text-base font-bold text-ink-900">{appliance.name}</p>
          {appliance.anomalous && (
            <span className="flex items-center gap-1 rounded-sm bg-anomaly-50 px-1.5 py-0.5 text-[10px] font-semibold text-anomaly-600">
              <Radar className="size-3" aria-hidden="true" />
              ANOMALİ
            </span>
          )}
        </div>
        <div className="mt-1.5 h-1 w-full overflow-hidden rounded-sm bg-surface-sunken">
          <div
            className={cn("h-full rounded-sm", appliance.anomalous ? "bg-anomaly-500" : "bg-data-500")}
            style={{ width: `${ratio}%` }}
          />
        </div>
      </div>

      <div className="shrink-0 text-right tabular-nums">
        <p className="text-lg font-bold text-ink-900">{formatWatt(appliance.lastMeasuredWatt)}</p>
        <p className="text-[11px] text-ink-400">limit {formatWatt(appliance.safeWattLimit)}</p>
      </div>

      <div className="flex shrink-0 items-center">
        <EditApplianceLimitDialog
          homeId={homeId}
          applianceId={appliance.applianceId}
          applianceName={appliance.name}
          currentLimit={appliance.safeWattLimit}
        />

        <AlertDialog>
          <AlertDialogTrigger asChild>
            <Button variant="ghost" size="icon" aria-label={`${appliance.name} cihazını sil`} disabled={deleting}>
              <Trash2 className="size-4 text-ink-400" aria-hidden="true" />
            </Button>
          </AlertDialogTrigger>
          <AlertDialogContent>
            <AlertDialogTitle>Cihazı sil</AlertDialogTitle>
            <AlertDialogDescription>
              "{appliance.name}" cihazını silmek istediğinize emin misiniz? Bu işlem geri alınamaz.
            </AlertDialogDescription>
            <AlertDialogFooter>
              <AlertDialogCancel>Vazgeç</AlertDialogCancel>
              <AlertDialogAction onClick={onDelete}>Sil</AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      </div>
    </div>
  );
}
