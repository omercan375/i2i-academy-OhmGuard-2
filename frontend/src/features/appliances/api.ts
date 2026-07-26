import { api } from "@/lib/apiClient";
import type { AddApplianceRequest, UpdateSafeWattLimitRequest } from "@/types/dto";

export function addAppliance(payload: AddApplianceRequest) {
  return api.post<void>("/api/appliances/add", payload, { responseType: "void" });
}

export function deleteAppliance(homeId: string, applianceId: string) {
  return api.del<void>(`/api/appliances/delete?homeId=${homeId}&applianceId=${applianceId}`, {
    responseType: "void",
  });
}

export function updateSafeWattLimit(payload: UpdateSafeWattLimitRequest) {
  return api.put<void>("/api/appliances/update/safe-watt-limit", payload, { responseType: "void" });
}
