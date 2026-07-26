import { CircleCheck, OctagonAlert, Radar, TriangleAlert, type LucideIcon } from "lucide-react";
import type { HomeStatus } from "@/types/dto";

export const STATUS_META: Record<
  HomeStatus,
  {
    label: string;
    description: string;
    icon: LucideIcon;
    chipClass: string;
    fillClass: string;
    textClass: string;
  }
> = {
  ok: {
    label: "Normal",
    description: "Tüketim bütçe sınırları içinde.",
    icon: CircleCheck,
    chipClass: "bg-ok-50 text-ok-600",
    fillClass: "bg-ok-500",
    textClass: "text-ok-600",
  },
  warn: {
    label: "%80 Uyarısı",
    description: "Bütçenin %80'ine yaklaşıldı.",
    icon: TriangleAlert,
    chipClass: "bg-warn-50 text-warn-600",
    fillClass: "bg-warn-500",
    textClass: "text-warn-600",
  },
  anomaly: {
    label: "Cihaz Anomalisi",
    description: "Bir cihaz güvenli limiti sürekli aşıyor.",
    icon: Radar,
    chipClass: "bg-anomaly-50 text-anomaly-600",
    fillClass: "bg-anomaly-500",
    textClass: "text-anomaly-600",
  },
  breach: {
    label: "Bütçe Aşıldı",
    description: "Ceza tarifesi uygulanıyor.",
    icon: OctagonAlert,
    chipClass: "bg-danger-50 text-danger-600",
    fillClass: "bg-danger-500",
    textClass: "text-danger-600",
  },
};
