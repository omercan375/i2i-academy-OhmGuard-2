import * as React from "react";
import { createAccount, fetchCurrentUser, login as loginRequest } from "@/features/auth/api";
import { clearToken, getToken, setToken, setUnauthorizedHandler } from "@/lib/authToken";
import { AuthContext, type AuthContextValue, type AuthStatus } from "@/features/auth/context";
import type { CreateAccountRequest, CurrentUser } from "@/types/dto";

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [status, setStatus] = React.useState<AuthStatus>("loading");
  const [user, setUser] = React.useState<CurrentUser | null>(null);

  const restoreSession = React.useCallback(async () => {
    const token = getToken();
    if (!token) {
      setStatus("unauthenticated");
      return;
    }
    try {
      const currentUser = await fetchCurrentUser();
      setUser(currentUser);
      setStatus("authenticated");
    } catch {
      clearToken();
      setUser(null);
      setStatus("unauthenticated");
    }
  }, []);

  React.useEffect(() => {
    restoreSession();
  }, [restoreSession]);

  React.useEffect(() => {
    setUnauthorizedHandler(() => {
      clearToken();
      setUser(null);
      setStatus("unauthenticated");
    });
    return () => setUnauthorizedHandler(null);
  }, []);

  const login = React.useCallback(async (email: string, password: string) => {
    const token = await loginRequest({ email, password });
    setToken(token);
    try {
      const currentUser = await fetchCurrentUser();
      setUser(currentUser);
      setStatus("authenticated");
    } catch (err) {
      clearToken();
      throw err;
    }
  }, []);

  const register = React.useCallback(
    async (payload: CreateAccountRequest) => {
      await createAccount(payload);
      await login(payload.email, payload.password);
    },
    [login],
  );

  const logout = React.useCallback(() => {
    clearToken();
    setUser(null);
    setStatus("unauthenticated");
  }, []);

  const value = React.useMemo<AuthContextValue>(
    () => ({ status, user, login, register, logout }),
    [status, user, login, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
