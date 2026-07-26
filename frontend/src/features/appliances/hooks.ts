import { useMutation, useQueryClient } from "@tanstack/react-query";
import * as appliancesApi from "@/features/appliances/api";
import { homesKeys } from "@/features/homes/hooks";
import type { AddApplianceRequest, UpdateSafeWattLimitRequest } from "@/types/dto";

export function useAddAppliance(homeId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: AddApplianceRequest) => appliancesApi.addAppliance(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: homesKeys.status(homeId) });
      queryClient.invalidateQueries({ queryKey: homesKeys.dashboard });
    },
  });
}

export function useDeleteAppliance(homeId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (applianceId: string) => appliancesApi.deleteAppliance(homeId, applianceId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: homesKeys.status(homeId) });
      queryClient.invalidateQueries({ queryKey: homesKeys.dashboard });
    },
  });
}

export function useUpdateApplianceLimit(homeId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: UpdateSafeWattLimitRequest) => appliancesApi.updateSafeWattLimit(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: homesKeys.status(homeId) });
    },
  });
}
