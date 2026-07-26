import * as React from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { toast } from "sonner";
import { ArrowLeft, Bell, Trash2 } from "lucide-react";
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
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { ErrorState } from "@/components/ui/error-state";
import { EmptyState } from "@/components/ui/empty-state";
import { StatusChip } from "@/components/ui/status-chip";
import { ThresholdMeter } from "@/components/ui/threshold-meter";
import { Panel, PanelHeader, PanelTitle } from "@/components/ui/panel";
import { ApplianceRow } from "@/features/homes/components/ApplianceRow";
import { AddApplianceForm } from "@/features/homes/components/AddApplianceForm";
import { DailyChart } from "@/features/homes/components/DailyChart";
import { EditBudgetDialog } from "@/features/homes/components/EditBudgetDialog";
import { EditTariffDialog } from "@/features/homes/components/EditTariffDialog";
import { useHomeInfo, useHomeStatus, useDeleteHome } from "@/features/homes/hooks";
import { useDeleteAppliance } from "@/features/appliances/hooks";
import { useNotifications } from "@/features/notifications/hooks";
import { NotificationRow } from "@/features/notifications/components/NotificationRow";
import { statusOf } from "@/types/dto";
import { STATUS_META } from "@/lib/status";
import { clamp, formatCurrency, formatKwh } from "@/lib/utils";
import { ApiError } from "@/lib/apiClient";

export default function HomeDetailPage() {
  const { homeId } = useParams<{ homeId: string }>();
  const navigate = useNavigate();

  const infoQuery = useHomeInfo(homeId);
  const statusQuery = useHomeStatus(homeId);
  const notificationsQuery = useNotifications();
  const deleteHome = useDeleteHome();
  const deleteAppliance = useDeleteAppliance(homeId ?? "");
  const [deletingApplianceId, setDeletingApplianceId] = React.useState<string | null>(null);

  if (!homeId) return null;

  const handleDeleteAppliance = async (applianceId: string, name: string) => {
    setDeletingApplianceId(applianceId);
    try {
      await deleteAppliance.mutateAsync(applianceId);
      toast.success(`${name} silindi`);
    } catch (err) {
      toast.error(err instanceof ApiError ? err.message : "Cihaz silinemedi.");
    } finally {
      setDeletingApplianceId(null);
    }
  };

  const handleDeleteHome = async () => {
    try {
      await deleteHome.mutateAsync(homeId);
      toast.success(`${infoQuery.data?.name ?? "Ev"} silindi`);
      navigate("/panel");
    } catch (err) {
      toast.error(err instanceof ApiError ? err.message : "Ev silinemedi.");
    }
  };

  const data = statusQuery.data;
  const usagePercent = data && data.budgetLimit > 0 ? (data.accumulatedCost / data.budgetLimit) * 100 : 0;
  const anomalyCount = data?.appliances.filter((a) => a.anomalous).length ?? 0;
  const status = data
    ? statusOf({ activeTariff: data.activeTariff, anomalyCount, alert80Sent: data.alert80Sent, usagePercent })
    : "ok";
  const meta = STATUS_META[status];

  const homeNotifications = infoQuery.data
    ? (notificationsQuery.data ?? []).filter((n) => n.homeName === infoQuery.data!.name)
    : [];

  const isLoading = infoQuery.isLoading || statusQuery.isLoading;
  const isError = infoQuery.isError || statusQuery.isError;

  return (
    <div>
      <Link to="/panel" className="mb-4 inline-flex items-center gap-1.5 text-sm font-medium text-ink-600 hover:text-ink-900">
        <ArrowLeft className="size-4" aria-hidden="true" />
        Evlerinize dönün
      </Link>

      {isLoading && (
        <div className="space-y-4">
          <Skeleton className="h-8 w-64" />
          <Skeleton className="h-40 w-full" />
          <Skeleton className="h-40 w-full" />
        </div>
      )}

      {isError && (
        <ErrorState
          message={
            infoQuery.error instanceof ApiError
              ? infoQuery.error.message
              : statusQuery.error instanceof ApiError
                ? statusQuery.error.message
                : "Ev bilgileri yüklenemedi."
          }
          onRetry={() => {
            infoQuery.refetch();
            statusQuery.refetch();
          }}
        />
      )}

      {infoQuery.data && data && (
        <div className="space-y-5">
          <div className="flex flex-wrap items-start justify-between gap-3 border-b border-border pb-4">
            <div>
              <h1 className="text-2xl font-bold text-ink-900">{infoQuery.data.name}</h1>
              <p className="mt-0.5 text-sm text-ink-600">{infoQuery.data.contactEmail}</p>
            </div>
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button variant="ghost" size="sm">
                  <Trash2 className="size-3.5" aria-hidden="true" />
                  Evi sil
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogTitle>Evi sil</AlertDialogTitle>
                <AlertDialogDescription>
                  "{infoQuery.data.name}" ve bağlı tüm cihazlar/kayıtlar kalıcı olarak silinecek. Bu işlem geri alınamaz.
                </AlertDialogDescription>
                <AlertDialogFooter>
                  <AlertDialogCancel>Vazgeç</AlertDialogCancel>
                  <AlertDialogAction onClick={handleDeleteHome}>Evi sil</AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </div>

          <Panel>
            <div className="p-4">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <StatusChip status={status} />
                {status !== "ok" && <p className={`text-xs font-medium ${meta.textClass}`}>{meta.description}</p>}
              </div>

              <div className="mt-5 grid grid-cols-2 gap-x-4 gap-y-4 sm:grid-cols-4">
                <div>
                  <p className="eyebrow">Maliyet</p>
                  <p className="mt-1 text-2xl font-bold tabular-nums text-ink-900">{formatCurrency(data.accumulatedCost)}</p>
                </div>
                <div>
                  <p className="eyebrow">Bütçe</p>
                  <p className="mt-1 text-2xl font-bold tabular-nums text-ink-900">{formatCurrency(data.budgetLimit)}</p>
                </div>
                <div>
                  <p className="eyebrow">Tarife</p>
                  <p className="mt-1 text-2xl font-bold text-ink-900">
                    {data.activeTariff === "PENALTY" ? "Ceza" : "Normal"}
                  </p>
                </div>
                <div>
                  <p className="eyebrow">Toplam</p>
                  <p className="mt-1 text-2xl font-bold tabular-nums text-ink-900">{formatKwh(data.accumulatedKwh)}</p>
                </div>
              </div>

              <div className="mt-4">
                <ThresholdMeter percent={clamp(usagePercent, 0, 999)} status={status} />
              </div>

              <div className="mt-4 flex flex-wrap gap-2 border-t border-border pt-4">
                <EditBudgetDialog homeId={homeId} currentBudget={infoQuery.data.budgetLimit} />
                <EditTariffDialog
                  homeId={homeId}
                  currentNormalRate={infoQuery.data.normalTariffRate}
                  currentPenaltyRate={infoQuery.data.penaltyTariffRate}
                />
              </div>
            </div>
          </Panel>

          <Panel>
            <PanelHeader>
              <PanelTitle>
                Cihazlar {anomalyCount > 0 && <span className="font-normal text-anomaly-600">· {anomalyCount} anomali</span>}
              </PanelTitle>
              <AddApplianceForm homeId={homeId} />
            </PanelHeader>
            <div className="p-4">
              {data.appliances.length === 0 ? (
                <p className="rounded-md border border-dashed border-border-strong px-4 py-6 text-center text-sm text-ink-400">
                  Henüz cihaz eklenmedi.
                </p>
              ) : (
                <div className="divide-y divide-border rounded-md border border-border">
                  {data.appliances.map((appliance) => (
                    <ApplianceRow
                      key={appliance.applianceId}
                      homeId={homeId}
                      appliance={appliance}
                      deleting={deletingApplianceId === appliance.applianceId}
                      onDelete={() => handleDeleteAppliance(appliance.applianceId, appliance.name)}
                    />
                  ))}
                </div>
              )}
            </div>
          </Panel>

          <Panel>
            <PanelHeader>
              <PanelTitle>Günlük tüketim</PanelTitle>
            </PanelHeader>
            <div className="p-4">
              <DailyChart homeId={homeId} />
            </div>
          </Panel>

          <Panel>
            <PanelHeader>
              <PanelTitle>Bu eve ait bildirimler</PanelTitle>
            </PanelHeader>
            {notificationsQuery.isLoading && (
              <div className="space-y-2 p-4">
                <Skeleton className="h-20 w-full" />
              </div>
            )}
            {homeNotifications.length === 0 && !notificationsQuery.isLoading ? (
              <div className="p-4">
                <EmptyState
                  icon={<Bell className="size-5" aria-hidden="true" />}
                  title="Bu ev için henüz bildirim yok"
                  description="Bütçe eşiğine yaklaşıldığında veya bir cihaz anormal davrandığında burada görünecek."
                />
              </div>
            ) : (
              <div className="divide-y divide-border">
                {homeNotifications.map((notification, index) => (
                  <NotificationRow key={index} notification={notification} showHomeName={false} />
                ))}
              </div>
            )}
          </Panel>
        </div>
      )}
    </div>
  );
}
