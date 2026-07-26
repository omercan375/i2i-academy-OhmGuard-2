import { Bell } from "lucide-react";
import { useNotifications } from "@/features/notifications/hooks";
import { NotificationRow } from "@/features/notifications/components/NotificationRow";
import { Skeleton } from "@/components/ui/skeleton";
import { ErrorState } from "@/components/ui/error-state";
import { EmptyState } from "@/components/ui/empty-state";
import { Panel } from "@/components/ui/panel";
import { ApiError } from "@/lib/apiClient";

export default function NotificationsPage() {
  const { data, isLoading, isError, error, refetch } = useNotifications();

  return (
    <div>
      <div className="mb-5 border-b border-border pb-4">
        <h1 className="text-2xl font-bold text-ink-900">Bildirimler</h1>
        <p className="mt-1 text-sm text-ink-600">Bütçe ve anomali uyarılarınızın gönderim geçmişi.</p>
      </div>

      {isLoading && (
        <div className="space-y-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-28 w-full" />
          ))}
        </div>
      )}

      {isError && (
        <ErrorState
          message={error instanceof ApiError ? error.message : "Bildirimler yüklenemedi."}
          onRetry={() => refetch()}
        />
      )}

      {data && data.length === 0 && (
        <EmptyState
          icon={<Bell className="size-5" aria-hidden="true" />}
          title="Henüz bildirim yok"
          description="Bir ev bütçe eşiğine yaklaştığında veya bir cihaz anormal davrandığında burada görünecek."
        />
      )}

      {data && data.length > 0 && (
        <Panel className="divide-y divide-border overflow-hidden">
          {data.map((notification, index) => (
            <NotificationRow key={index} notification={notification} />
          ))}
        </Panel>
      )}
    </div>
  );
}
