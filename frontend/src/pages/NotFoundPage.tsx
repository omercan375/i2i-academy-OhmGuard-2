import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";

export default function NotFoundPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-3 bg-background px-6 text-center">
      <p className="text-sm font-semibold tabular-nums text-ink-400">404</p>
      <h1 className="text-lg font-semibold text-ink-900">Sayfa bulunamadı</h1>
      <p className="max-w-sm text-sm text-ink-600">Aradığınız sayfa taşınmış veya hiç var olmamış olabilir.</p>
      <Button asChild className="mt-2">
        <Link to="/">Ana sayfaya dön</Link>
      </Button>
    </div>
  );
}
