const STORAGE_KEY = "ohmguard.token";

type Listener = (token: string | null) => void;

let currentToken: string | null = localStorage.getItem(STORAGE_KEY);
const listeners = new Set<Listener>();

export function getToken() {
  return currentToken;
}

export function setToken(token: string) {
  currentToken = token;
  localStorage.setItem(STORAGE_KEY, token);
  listeners.forEach((listener) => listener(token));
}

export function clearToken() {
  currentToken = null;
  localStorage.removeItem(STORAGE_KEY);
  listeners.forEach((listener) => listener(null));
}

export function onTokenChange(listener: Listener) {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

type UnauthorizedHandler = () => void;
let unauthorizedHandler: UnauthorizedHandler | null = null;

export function setUnauthorizedHandler(handler: UnauthorizedHandler | null) {
  unauthorizedHandler = handler;
}

export function notifyUnauthorized() {
  unauthorizedHandler?.();
}
