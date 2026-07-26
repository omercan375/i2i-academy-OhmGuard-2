import { api } from "@/lib/apiClient";
import type { CreateAccountRequest, CurrentUser, LoginRequest } from "@/types/dto";

export function login(payload: LoginRequest) {
  return api.post<string>("/api/auth/login", payload, { auth: false, responseType: "text" });
}

export function createAccount(payload: CreateAccountRequest) {
  return api.post<string>("/api/auth/create-account", payload, { auth: false, responseType: "text" });
}

export function fetchCurrentUser(signal?: AbortSignal) {
  return api.get<CurrentUser>("/api/auth/me", { signal });
}
