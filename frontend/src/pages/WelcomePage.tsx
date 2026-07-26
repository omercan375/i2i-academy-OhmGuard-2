import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { ThresholdMeter } from "@/components/ui/threshold-meter";
import { StatusChip } from "@/components/ui/status-chip";
import { LogoMark } from "@/components/ui/logo-mark";

const steps = [
  {
    n: "01",
    title: "Evinizi kaydedin",
    body: "Bütçe limitinizi, tarife oranlarınızı ve cihazlarınızın güvenli watt sınırlarını tanımlayın.",
  },
  {
    n: "02",
    title: "Telemetri akışı başlar",
    body: "Cihazlarınızdan gelen anlık tüketim verisi Kafka üzerinden saniyeler içinde işlenir.",
  },
  {
    n: "03",
    title: "Uyarı gönderilir",
    body: "Bütçe %80'i aştığında veya bir cihaz anormal davrandığında, kişiselleştirilmiş bir e-posta gönderilir.",
  },
];

const capabilities = [
  {
    term: "Canlı tüketim",
    body: "Tüm evlerinizin anlık kWh ve maliyet durumu, 1.5-2 saniyelik aralıklarla güncellenir.",
  },
  {
    term: "Anomali tespiti",
    body: "Bir cihaz üst üste güvenli limitin üzerine çıkarsa otomatik olarak işaretlenir.",
  },
  {
    term: "Ceza tarifesi",
    body: "Bütçe aşıldığında sistem otomatik olarak daha yüksek birim fiyata geçer.",
  },
  {
    term: "Kişiselleştirilmiş uyarılar",
    body: "Ev bazlı verilerden yola çıkılarak size özel tasarruf önerisi yazılır.",
  },
  {
    term: "Günlük tüketim geçmişi",
    body: "Geçmiş günlerin enerji ve maliyet trendini grafik üzerinden takip edin.",
  },
  {
    term: "Tek formda kurulum",
    body: "Evinizi ve cihazlarınızı tek seferde tanımlayıp izlemeye başlayın.",
  },
];

export default function WelcomePage() {
  return (
    <div className="bg-background">
      <header className="border-b border-border">
        <div className="mx-auto flex h-14 max-w-5xl items-center justify-between px-4 sm:px-6">
          <div className="flex items-center gap-2 text-lg font-bold text-ink-900">
            <LogoMark className="size-6" />
            Ohm Guard
          </div>
          <div className="flex items-center gap-2">
            <Button asChild variant="ghost" size="sm">
              <Link to="/giris">Giriş yap</Link>
            </Button>
            <Button asChild size="sm">
              <Link to="/kayit">Hesap oluştur</Link>
            </Button>
          </div>
        </div>
      </header>

      <section className="mx-auto max-w-5xl px-4 py-14 sm:px-6">
        <div className="grid gap-10 lg:grid-cols-[1.1fr_0.9fr] lg:items-start">
          <div>
            <p className="eyebrow">Ev enerji takip sistemi</p>
            <h1 className="mt-2 max-w-lg text-4xl font-extrabold leading-[1.1] text-ink-900 sm:text-5xl">
              Enerji tüketiminizi ve bütçenizi tek yerden denetleyin.
            </h1>
            <p className="mt-5 max-w-md text-base leading-relaxed text-ink-600">
              Ohm Guard, evinizdeki cihazların anlık tüketimini izler, bütçe kotalarını denetler ve bir
              eşik aşılmadan önce sizi bilgilendirir.
            </p>
            <div className="mt-7 flex items-center gap-3">
              <Button asChild size="lg">
                <Link to="/kayit">Hesap oluştur</Link>
              </Button>
              <Button asChild variant="secondary" size="lg">
                <Link to="/giris">Giriş yap</Link>
              </Button>
            </div>
          </div>

          <div className="rounded-lg border border-border bg-surface p-5">
            <p className="eyebrow">Örnek gösterge</p>
            <p className="mt-1 text-xs text-ink-400">Kullanım göstergesi böyle okunur — %80'de uyarı, %100'de ceza tarifesi.</p>
            <div className="mt-4 space-y-4">
              {[
                { name: "Yazlık", pct: 34, status: "ok" as const },
                { name: "Sahil Evi", pct: 84, status: "warn" as const },
                { name: "Merkez Daire", pct: 104, status: "breach" as const },
              ].map((home) => (
                <div key={home.name}>
                  <div className="mb-1.5 flex items-center justify-between">
                    <span className="text-sm font-medium text-ink-900">{home.name}</span>
                    <StatusChip status={home.status} />
                  </div>
                  <ThresholdMeter percent={home.pct} status={home.status} size="sm" />
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      <section className="border-y border-border bg-surface py-12">
        <div className="mx-auto max-w-5xl px-4 sm:px-6">
          <p className="eyebrow">Nasıl çalışır</p>
          <div className="mt-4 grid gap-8 sm:grid-cols-3 sm:divide-x sm:divide-border">
            {steps.map((step) => (
              <div key={step.n} className="sm:pl-6 sm:first:pl-0">
                <span className="text-xs font-semibold tabular-nums text-ink-400">{step.n}</span>
                <h3 className="mt-1.5 text-base font-bold text-ink-900">{step.title}</h3>
                <p className="mt-1.5 text-sm leading-relaxed text-ink-600">{step.body}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-5xl px-4 py-12 sm:px-6">
        <p className="eyebrow">Neler sunuyor</p>
        <dl className="mt-4 divide-y divide-border border-t border-border">
          {capabilities.map((item) => (
            <div key={item.term} className="grid gap-1 py-4 sm:grid-cols-[12rem_1fr] sm:gap-6">
              <dt className="text-base font-bold text-ink-900">{item.term}</dt>
              <dd className="text-sm leading-relaxed text-ink-600">{item.body}</dd>
            </div>
          ))}
        </dl>
      </section>

      <section className="border-t border-border">
        <div className="mx-auto flex max-w-5xl flex-col items-start justify-between gap-4 px-4 py-10 sm:flex-row sm:items-center sm:px-6">
          <div>
            <h2 className="text-2xl font-bold text-ink-900">Enerji faturanızı sürpriz olmaktan çıkarın.</h2>
            <p className="mt-1 text-sm text-ink-600">Kurulum bir dakikadan az sürer.</p>
          </div>
          <Button asChild size="lg">
            <Link to="/kayit">Hesap oluştur</Link>
          </Button>
        </div>
      </section>

      <footer className="border-t border-border px-4 py-6 text-center text-xs text-ink-400 sm:px-6">
        Ohm Guard — i2i Academy eğitim projesi.
      </footer>
    </div>
  );
}
