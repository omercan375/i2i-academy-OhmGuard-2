import * as React from "react";
import { AlertOctagon } from "lucide-react";
import { Button } from "@/components/ui/button";

interface Props {
  children: React.ReactNode;
}

interface State {
  hasError: boolean;
}

export class ErrorBoundary extends React.Component<Props, State> {
  state: State = { hasError: false };

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  componentDidCatch(error: unknown) {
    console.error("Beklenmeyen arayüz hatası:", error);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex min-h-screen flex-col items-center justify-center gap-4 bg-background px-6 text-center">
          <div className="flex size-14 items-center justify-center rounded-full bg-danger-50 text-danger-500">
            <AlertOctagon className="size-7" aria-hidden="true" />
          </div>
          <div className="space-y-1.5">
            <h1 className="text-xl font-semibold text-ink-900">
              Bir şeyler ters gitti
            </h1>
            <p className="max-w-sm text-sm text-ink-400">
              Sayfa beklenmedik bir hatayla karşılaştı. Sayfayı yenileyerek tekrar deneyebilirsiniz.
            </p>
          </div>
          <Button onClick={() => window.location.reload()}>Sayfayı yenile</Button>
        </div>
      );
    }
    return this.props.children;
  }
}
