import { Suspense, lazy } from "react";
import { Routes, Route } from "react-router-dom";
import { ProtectedRoute, PublicOnlyRoute } from "@/components/protected-route";
import { AppShell } from "@/layouts/AppShell";
import { Spinner } from "@/components/ui/spinner";

const WelcomePage = lazy(() => import("@/pages/WelcomePage"));
const LoginPage = lazy(() => import("@/pages/LoginPage"));
const RegisterPage = lazy(() => import("@/pages/RegisterPage"));
const DashboardPage = lazy(() => import("@/pages/DashboardPage"));
const HomeDetailPage = lazy(() => import("@/pages/HomeDetailPage"));
const NotificationsPage = lazy(() => import("@/pages/NotificationsPage"));
const NotFoundPage = lazy(() => import("@/pages/NotFoundPage"));

function RouteFallback() {
  return (
    <div className="flex min-h-[50vh] items-center justify-center">
      <Spinner label="Sayfa yükleniyor" />
    </div>
  );
}

export default function App() {
  return (
    <Suspense fallback={<RouteFallback />}>
      <Routes>
        <Route element={<PublicOnlyRoute />}>
          <Route path="/" element={<WelcomePage />} />
          <Route path="/giris" element={<LoginPage />} />
          <Route path="/kayit" element={<RegisterPage />} />
        </Route>

        <Route element={<ProtectedRoute />}>
          <Route path="/panel" element={<AppShell />}>
            <Route index element={<DashboardPage />} />
            <Route path="evler/:homeId" element={<HomeDetailPage />} />
            <Route path="bildirimler" element={<NotificationsPage />} />
          </Route>
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Suspense>
  );
}
