export type ActiveTariff = "NORMAL" | "PENALTY";

export interface CreateAccountRequest {
  email: string;
  password: string;
  firstName: string;
  secondName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface CurrentUser {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface RegisterApplianceInput {
  name: string;
  safeWattLimit: number;
}

export interface RegisterHomeRequest {
  name: string;
  contactEmail: string;
  budgetLimit: number;
  normalTariffRate: number;
  penaltyTariffRate: number;
  appliances?: RegisterApplianceInput[];
}

export interface DashboardHomeDto {
  homeId: string;
  name: string;
  contactEmail: string;
  budgetLimit: number;
  accumulatedCost: number;
  accumulatedKwh: number;
  usagePercent: number;
  activeTariff: ActiveTariff;
  alert80Sent: boolean;
  alert100Sent: boolean;
  anomalyCount: number;
  applianceCount: number;
  live: boolean;
}

export interface ApplianceStatusDto {
  applianceId: string;
  name: string;
  lastMeasuredWatt: number;
  safeWattLimit: number;
  consecutiveBreachCount: number;
  anomalous: boolean;
  lastMeasuredAt: string;
}

export interface HomeStatusDto {
  homeId: string;
  accumulatedKwh: number;
  accumulatedCost: number;
  budgetLimit: number;
  activeTariff: ActiveTariff;
  alert80Sent: boolean;
  alert100Sent: boolean;
  appliances: ApplianceStatusDto[];
}

export interface DailyConsumptionSnapshot {
  energyKwh: number;
  totalCost: number;
  recordedAt: string;
}

export interface NotificationDto {
  homeName: string;
  triggerType: string;
  recipientEmail: string;
  status: string;
  text: string;
  createdAt: string;
}

export interface AddApplianceRequest {
  homeId: string;
  name: string;
  safeWattLimit: number;
  active: boolean;
}

export interface HomeInfo {
  name: string;
  contactEmail: string;
  budgetLimit: number;
  normalTariffRate: number;
  penaltyTariffRate: number;
  active: boolean;
}

export interface UpdateBudgetLimitRequest {
  homeId: string;
  newBudgetLimit: number;
}

export interface UpdateTariffRatesRequest {
  homeId: string;
  normTariffRate: number;
  penaltyTariffRate: number;
}

export interface UpdateSafeWattLimitRequest {
  applianceId: string;
  homeId: string;
  newWattLimit: number;
}

export type HomeStatus = "ok" | "warn" | "anomaly" | "breach";

export function statusOf(home: Pick<DashboardHomeDto, "activeTariff" | "anomalyCount" | "alert80Sent" | "usagePercent">): HomeStatus {
  if (home.activeTariff === "PENALTY") return "breach";
  if (home.anomalyCount > 0) return "anomaly";
  if (home.alert80Sent || home.usagePercent >= 80) return "warn";
  return "ok";
}
