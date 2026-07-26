import { api } from "@/lib/apiClient";
import type { NotificationDto } from "@/types/dto";

export function fetchMyNotifications(signal?: AbortSignal) {
  return api.get<NotificationDto[]>("/api/notifications/mine", { signal });
}
