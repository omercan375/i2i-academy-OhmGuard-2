import { useMutation, useQuery, useQueryClient, keepPreviousData } from "@tanstack/react-query";
import * as homesApi from "@/features/homes/api";
import type { RegisterHomeRequest, UpdateBudgetLimitRequest, UpdateTariffRatesRequest } from "@/types/dto";

export const homesKeys = {
  dashboard: ["homes", "dashboard"] as const,
  info: (homeId: string) => ["homes", "info", homeId] as const,
  status: (homeId: string) => ["homes", "status", homeId] as const,
  dailyHistory: (homeId: string) => ["homes", "daily-history", homeId] as const,
};

export function useDashboard() {
  return useQuery({
    queryKey: homesKeys.dashboard,
    queryFn: ({ signal }) => homesApi.fetchDashboard(signal),
    refetchInterval: 2000,
    placeholderData: keepPreviousData,
  });
}

export function useHomeInfo(homeId: string | undefined) {
  return useQuery({
    queryKey: homesKeys.info(homeId ?? ""),
    queryFn: ({ signal }) => homesApi.fetchHomeInfo(homeId as string, signal),
    enabled: Boolean(homeId),
  });
}

export function useHomeStatus(homeId: string | undefined) {
  return useQuery({
    queryKey: homesKeys.status(homeId ?? ""),
    queryFn: ({ signal }) => homesApi.fetchHomeStatus(homeId as string, signal),
    enabled: Boolean(homeId),
    refetchInterval: 1500,
    placeholderData: keepPreviousData,
  });
}

export function useDailyHistory(homeId: string | undefined) {
  return useQuery({
    queryKey: homesKeys.dailyHistory(homeId ?? ""),
    queryFn: ({ signal }) => homesApi.fetchDailyHistory(homeId as string, signal),
    enabled: Boolean(homeId),
    refetchInterval: 30000,
    placeholderData: keepPreviousData,
  });
}

export function useCreateHome() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: RegisterHomeRequest) => homesApi.createHome(payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: homesKeys.dashboard }),
  });
}

export function useDeleteHome() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (homeId: string) => homesApi.deleteHome(homeId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: homesKeys.dashboard }),
  });
}

export function useUpdateBudgetLimit(homeId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: UpdateBudgetLimitRequest) => homesApi.updateBudgetLimit(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: homesKeys.info(homeId) });
      queryClient.invalidateQueries({ queryKey: homesKeys.status(homeId) });
      queryClient.invalidateQueries({ queryKey: homesKeys.dashboard });
    },
  });
}

export function useUpdateTariffRates(homeId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: UpdateTariffRatesRequest) => homesApi.updateTariffRates(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: homesKeys.info(homeId) });
      queryClient.invalidateQueries({ queryKey: homesKeys.status(homeId) });
      queryClient.invalidateQueries({ queryKey: homesKeys.dashboard });
    },
  });
}
