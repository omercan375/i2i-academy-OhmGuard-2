import { Mail } from "lucide-react";
import type { NotificationDto } from "@/types/dto";
import { cn, formatDateTime } from "@/lib/utils";

const TRIGGER_LABEL: Record<string, string> = {
  QUOTA_80: "%80 Bütçe Uyarısı",
  QUOTA_100: "Bütçe Aşıldı",
  ANOMALY: "Cihaz Anomalisi",
};

const STATUS_STYLE: Record<string, string> = {
  SENT: "bg-ok-50 text-ok-600",
  PENDING: "bg-warn-50 text-warn-600",
  FAILED: "bg-danger-50 text-danger-600",
};

const STATUS_LABEL: Record<string, string> = {
  SENT: "Gönderildi",
  PENDING: "Beklemede",
  FAILED: "Başarısız",
};

export function NotificationRow({ notification, showHomeName = true }: { notification: NotificationDto; showHomeName?: boolean }) {
  return (
    <article className="px-4 py-4">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div className="flex items-center gap-2.5">
          <span className="flex size-7 items-center justify-center rounded-sm bg-brand-50 text-brand-600">
            <Mail className="size-3.5" aria-hidden="true" />
          </span>
          <div>
            {showHomeName && <p className="text-sm font-semibold text-ink-900">{notification.homeName}</p>}
            <p className="text-[11px] text-ink-400">{formatDateTime(notification.createdAt)}</p>
          </div>
        </div>
        <div className="flex items-center gap-1.5">
          <span className="rounded-sm bg-surface-sunken px-2 py-0.5 text-[11px] font-medium text-ink-600">
            {TRIGGER_LABEL[notification.triggerType] ?? notification.triggerType}
          </span>
          <span
            className={cn(
              "rounded-sm px-2 py-0.5 text-[11px] font-medium",
              STATUS_STYLE[notification.status] ?? "bg-surface-sunken text-ink-600",
            )}
          >
            {STATUS_LABEL[notification.status] ?? notification.status}
          </span>
        </div>
      </div>
      <p className="mt-3 whitespace-pre-line text-sm leading-relaxed text-ink-600">{notification.text}</p>
      <p className="mt-2 text-[11px] text-ink-300">{notification.recipientEmail}</p>
    </article>
  );
}
