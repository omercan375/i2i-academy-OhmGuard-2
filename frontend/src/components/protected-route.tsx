import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "@/features/auth/useAuth";
import { Spinner } from "@/components/ui/spinner";

export function ProtectedRoute() {
  const { status } = useAuth();
  const location = useLocation();

  if (status === "loading") {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <Spinner label="Oturum kontrol ediliyor" />
      </div>
    );
  }

  if (status === "unauthenticated") {
    return <Navigate to="/giris" state={{ from: location }} replace />;
  }

  return <Outlet />;
}

export function PublicOnlyRoute() {
  const { status } = useAuth();

  if (status === "loading") {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <Spinner label="Yükleniyor" />
      </div>
    );
  }

  if (status === "authenticated") {
    return <Navigate to="/panel" replace />;
  }

  return <Outlet />;
}
