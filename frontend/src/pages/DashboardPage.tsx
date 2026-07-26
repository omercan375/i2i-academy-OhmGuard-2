import { Plus, Zap } from "lucide-react";
import { useDashboard } from "@/features/homes/hooks";
import { HomeRow } from "@/features/homes/components/HomeRow";
import { RegisterHomeDialog } from "@/features/homes/components/RegisterHomeDialog";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { ErrorState } from "@/components/ui/error-state";
import { EmptyState } from "@/components/ui/empty-state";
import { Panel } from "@/components/ui/panel";
import { ApiError } from "@/lib/apiClient";
import { cn } from "@/lib/utils";

export default function DashboardPage() {
  const { data, isLoading, isError, error, refetch, isFetching } = useDashboard();

  return (
    <div>
      <div className="mb-5 flex flex-wrap items-center justify-between gap-3 border-b border-border pb-4">
        <div>
          <h1 className="text-2xl font-bold text-ink-900">Evleriniz</h1>
          <p className="mt-1 flex items-center gap-1.5 text-xs text-ink-400">
            <span className={cn("size-1.5 rounded-full", isFetching ? "bg-ok-500" : "bg-ink-300")} aria-hidden="true" />
            {isFetching ? "Canlı bağlantı" : "Beklemede"} · her 2 saniyede güncellenir
          </p>
        </div>
        <RegisterHomeDialog
          trigger={
            <Button size="sm">
              <Plus className="size-4" aria-hidden="true" />
              Ev ekle
            </Button>
          }
        />
      </div>

      {isLoading && (
        <div className="space-y-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-[72px] w-full" />
          ))}
        </div>
      )}

      {isError && (
        <ErrorState
          message={error instanceof ApiError ? error.message : "Evler yüklenemedi."}
          onRetry={() => refetch()}
        />
      )}

      {data && data.length === 0 && (
        <EmptyState
          icon={<Zap className="size-5" aria-hidden="true" />}
          title="Henüz bir eviniz yok"
          description="İlk evinizi ekleyerek anlık tüketim izlemeye başlayın."
          action={
            <RegisterHomeDialog
              trigger={
                <Button size="sm">
                  <Plus className="size-4" aria-hidden="true" />
                  Ev ekle
                </Button>
              }
            />
          }
        />
      )}

      {data && data.length > 0 && (
        <Panel className="overflow-hidden">
          <div className="hidden border-b border-border bg-surface-sunken px-4 py-2 text-xs font-medium text-ink-400 sm:grid sm:grid-cols-[1.5fr_1.1fr_1.3fr_1fr_0.8fr] sm:gap-4">
            <span>Ev</span>
            <span>Durum</span>
            <span>Kullanım</span>
            <span>Maliyet</span>
            <span className="text-right">Cihaz</span>
          </div>
          <div className="divide-y divide-border">
            {data.map((home) => (
              <HomeRow key={home.homeId} home={home} />
            ))}
          </div>
        </Panel>
      )}
    </div>
  );
}
