import { QueryClient } from "@tanstack/react-query";
import { ApiError } from "@/lib/apiClient";

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: (failureCount, error) => {
        if (error instanceof ApiError && error.status >= 400 && error.status < 500) {
          return false;
        }
        return failureCount < 2;
      },
      refetchOnWindowFocus: false,
      staleTime: 1000,
    },
    mutations: {
      retry: false,
    },
  },
});
