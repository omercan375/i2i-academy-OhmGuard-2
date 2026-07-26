import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { fetchMyNotifications } from "@/features/notifications/api";

export function useNotifications() {
  return useQuery({
    queryKey: ["notifications", "mine"] as const,
    queryFn: ({ signal }) => fetchMyNotifications(signal),
    refetchInterval: 8000,
    placeholderData: keepPreviousData,
  });
}
