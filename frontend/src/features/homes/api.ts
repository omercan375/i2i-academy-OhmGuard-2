import { api } from "@/lib/apiClient";
import type {
  DailyConsumptionSnapshot,
  DashboardHomeDto,
  HomeInfo,
  HomeStatusDto,
  RegisterHomeRequest,
  UpdateBudgetLimitRequest,
  UpdateTariffRatesRequest,
} from "@/types/dto";

export function fetchDashboard(signal?: AbortSignal) {
  return api.get<DashboardHomeDto[]>("/api/home/dashboard/mine", { signal });
}

export function fetchHomeInfo(homeId: string, signal?: AbortSignal) {
  return api.get<HomeInfo>(`/api/home/info?homeId=${homeId}`, { signal });
}

export function fetchHomeStatus(homeId: string, signal?: AbortSignal) {
  return api.get<HomeStatusDto>(`/api/home/status?homeId=${homeId}`, { signal });
}

export function fetchDailyHistory(homeId: string, signal?: AbortSignal) {
  return api.get<DailyConsumptionSnapshot[]>(`/api/consumption-service/daily-history?homeId=${homeId}`, { signal });
}

export function createHome(payload: RegisterHomeRequest) {
  return api.post<void>("/api/home/create-full", payload, { responseType: "void" });
}

export function deleteHome(homeId: string) {
  return api.del<void>(`/api/home/mine/${homeId}`, { responseType: "void" });
}

export function updateBudgetLimit(payload: UpdateBudgetLimitRequest) {
  return api.put<void>("/api/home/update/budget-limit", payload, { responseType: "void" });
}

export function updateTariffRates(payload: UpdateTariffRatesRequest) {
  return api.put<void>("/api/home/update/tariff-rates", payload, { responseType: "void" });
}
