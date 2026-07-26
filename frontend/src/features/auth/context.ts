import * as React from "react";
import type { CreateAccountRequest, CurrentUser } from "@/types/dto";

export type AuthStatus = "loading" | "authenticated" | "unauthenticated";

export interface AuthContextValue {
  status: AuthStatus;
  user: CurrentUser | null;
  login: (email: string, password: string) => Promise<void>;
  register: (payload: CreateAccountRequest) => Promise<void>;
  logout: () => void;
}

export const AuthContext = React.createContext<AuthContextValue | null>(null);
