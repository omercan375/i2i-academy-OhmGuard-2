import { getToken, notifyUnauthorized } from "./authToken";

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export class ApiError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

const FALLBACK_MESSAGES: Record<number, string> = {
  0: "Sunucuya ulaşılamıyor. İnternet bağlantınızı kontrol edip tekrar deneyin.",
  400: "Gönderilen bilgiler geçersiz. Lütfen alanları kontrol edin.",
  401: "Oturumunuzun süresi doldu. Lütfen tekrar giriş yapın.",
  403: "Bu işlem için yetkiniz yok.",
  404: "Aradığınız kayıt bulunamadı.",
  409: "Bu kayıt zaten mevcut.",
  408: "İstek zaman aşımına uğradı. Lütfen tekrar deneyin.",
  500: "Sunucuda beklenmeyen bir hata oluştu. Lütfen daha sonra tekrar deneyin.",
};

function friendlyMessage(status: number, bodyText: string) {
  const trimmed = bodyText.trim();
  const looksLikeStackTrace = /Exception|\bat [a-z]+\.[a-z]/i.test(trimmed) && trimmed.length > 200;
  if (trimmed && !looksLikeStackTrace && trimmed.length < 300) {
    return trimmed;
  }
  return FALLBACK_MESSAGES[status] ?? "Beklenmeyen bir hata oluştu.";
}

interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "DELETE";
  body?: unknown;
  signal?: AbortSignal;
  auth?: boolean;
  responseType?: "json" | "text" | "void";
  timeoutMs?: number;
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = "GET", body, signal, auth = true, responseType = "json", timeoutMs = 15000 } = options;

  const headers: Record<string, string> = {};
  if (body !== undefined) headers["Content-Type"] = "application/json";
  if (auth) {
    const token = getToken();
    if (token) headers["Authorization"] = `Bearer ${token}`;
  }

  const timeoutController = new AbortController();
  const timeoutId = setTimeout(() => timeoutController.abort(), timeoutMs);
  const combinedSignal = signal ? anySignal([signal, timeoutController.signal]) : timeoutController.signal;

  let response: Response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
      signal: combinedSignal,
    });
  } catch (err) {
    clearTimeout(timeoutId);
    if (err instanceof DOMException && err.name === "AbortError" && signal?.aborted) {
      throw err;
    }
    throw new ApiError(0, FALLBACK_MESSAGES[0]);
  }
  clearTimeout(timeoutId);

  if (!response.ok) {
    const bodyText = await response.text().catch(() => "");
    if (response.status === 401) {
      notifyUnauthorized();
    }
    throw new ApiError(response.status, friendlyMessage(response.status, bodyText));
  }

  if (responseType === "void") {
    return undefined as T;
  }
  if (responseType === "text") {
    return (await response.text()) as T;
  }

  const text = await response.text();
  if (!text) return undefined as T;
  try {
    return JSON.parse(text) as T;
  } catch {
    return text as T;
  }
}

function anySignal(signals: AbortSignal[]): AbortSignal {
  const controller = new AbortController();
  for (const s of signals) {
    if (s.aborted) {
      controller.abort();
      break;
    }
    s.addEventListener("abort", () => controller.abort(), { once: true });
  }
  return controller.signal;
}

export const api = {
  get: <T>(path: string, opts?: Omit<RequestOptions, "method" | "body">) =>
    request<T>(path, { ...opts, method: "GET" }),
  post: <T>(path: string, body?: unknown, opts?: Omit<RequestOptions, "method" | "body">) =>
    request<T>(path, { ...opts, method: "POST", body }),
  put: <T>(path: string, body?: unknown, opts?: Omit<RequestOptions, "method" | "body">) =>
    request<T>(path, { ...opts, method: "PUT", body }),
  del: <T>(path: string, opts?: Omit<RequestOptions, "method" | "body">) =>
    request<T>(path, { ...opts, method: "DELETE" }),
};
