import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { QueryClientProvider } from "@tanstack/react-query";
import { Toaster } from "sonner";
import "./index.css";
import App from "./App.tsx";
import { queryClient } from "@/app/queryClient";
import { AuthProvider } from "@/features/auth/AuthContext";
import { TooltipProvider } from "@/components/ui/tooltip";
import { ErrorBoundary } from "@/components/error-boundary";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ErrorBoundary>
      <QueryClientProvider client={queryClient}>
        <BrowserRouter>
          <AuthProvider>
            <TooltipProvider delayDuration={200}>
              <App />
              <Toaster
                position="top-right"
                toastOptions={{
                  classNames: {
                    toast: "!rounded-md !border !border-border !bg-surface !shadow-[var(--shadow-dialog)] !text-ink-900",
                    title: "!text-sm !font-medium",
                    success: "!text-ok-600",
                    error: "!text-danger-600",
                  },
                }}
              />
            </TooltipProvider>
          </AuthProvider>
        </BrowserRouter>
      </QueryClientProvider>
    </ErrorBoundary>
  </StrictMode>,
);
