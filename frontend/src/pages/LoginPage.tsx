import * as React from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Eye, EyeOff, ShieldCheck } from "lucide-react";
import { toast } from "sonner";
import { useAuth } from "@/features/auth/useAuth";
import { loginSchema, type LoginFormValues } from "@/features/auth/schemas";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Field } from "@/components/ui/label";
import { LogoMark } from "@/components/ui/logo-mark";
import { ApiError } from "@/lib/apiClient";

export default function LoginPage() {
  const { login, adminLogin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [showPassword, setShowPassword] = React.useState(false);
  const [formError, setFormError] = React.useState<string | null>(null);
  const [adminLoading, setAdminLoading] = React.useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({ resolver: zodResolver(loginSchema) });

  const from = (location.state as { from?: Location })?.from?.pathname ?? "/panel";

  const onSubmit = async (values: LoginFormValues) => {
    setFormError(null);
    try {
      await login(values.email, values.password);
      toast.success("Giriş başarılı.");
      navigate(from, { replace: true });
    } catch (err) {
      const message =
        err instanceof ApiError
          ? err.status === 404 || err.status === 400
            ? "E-posta veya şifre hatalı."
            : err.message
          : "Beklenmeyen bir hata oluştu.";
      setFormError(message);
    }
  };

  const onAdminEnter = async () => {
    setFormError(null);
    setAdminLoading(true);
    try {
      await adminLogin();
      toast.success("Yönetici olarak giriş yapıldı.");
      navigate("/panel", { replace: true });
    } catch (err) {
      const message =
        err instanceof ApiError
          ? err.status === 404 || err.status === 400
            ? "Yönetici hesabı bulunamadı."
            : err.message
          : "Yönetici girişi yapılamadı.";
      setFormError(message);
    } finally {
      setAdminLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4 py-10">
      <div className="w-full max-w-sm">
        <Link to="/" className="mb-8 flex items-center justify-center gap-2 text-lg font-bold text-ink-900">
          <LogoMark className="size-7" />
          Ohm Guard
        </Link>

        <div className="rounded-lg border border-border bg-surface p-6 shadow-[var(--shadow-panel)]">
          <h1 className="text-xl font-bold text-ink-900">Giriş yap</h1>
          <p className="mt-1 text-sm text-ink-600">Evlerinizi izlemek için oturum açın.</p>

          <form className="mt-6 space-y-4" onSubmit={handleSubmit(onSubmit)} noValidate>
            <Field label="E-posta" htmlFor="email" error={errors.email?.message}>
              <Input
                id="email"
                type="email"
                autoComplete="email"
                invalid={!!errors.email}
                placeholder="ornek@eposta.com"
                {...register("email")}
              />
            </Field>

            <Field label="Şifre" htmlFor="password" error={errors.password?.message}>
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  autoComplete="current-password"
                  invalid={!!errors.password}
                  placeholder="••••••••"
                  className="pr-10"
                  {...register("password")}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((v) => !v)}
                  className="absolute right-2 top-1/2 -translate-y-1/2 rounded-sm p-1.5 text-ink-400 hover:text-ink-900"
                  aria-label={showPassword ? "Şifreyi gizle" : "Şifreyi göster"}
                >
                  {showPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                </button>
              </div>
            </Field>

            {formError && (
              <p role="alert" className="rounded-md bg-danger-50 px-3 py-2 text-[13px] font-medium text-danger-600">
                {formError}
              </p>
            )}

            <Button type="submit" className="w-full" loading={isSubmitting}>
              Giriş yap
            </Button>
          </form>

          <div className="mt-5 flex items-center gap-3">
            <span className="h-px flex-1 bg-border" />
            <span className="text-xs text-ink-400">veya</span>
            <span className="h-px flex-1 bg-border" />
          </div>

          <Button
            type="button"
            variant="secondary"
            className="mt-4 w-full"
            loading={adminLoading}
            onClick={onAdminEnter}
          >
            <ShieldCheck className="size-4" aria-hidden="true" />
            Yönetici girişi
          </Button>
        </div>

        <p className="mt-5 text-center text-sm text-ink-600">
          Hesabınız yok mu?{" "}
          <Link to="/kayit" className="font-medium text-brand-500 hover:underline">
            Kayıt olun
          </Link>
        </p>
      </div>
    </div>
  );
}
