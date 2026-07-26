import * as React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Eye, EyeOff } from "lucide-react";
import { toast } from "sonner";
import { useAuth } from "@/features/auth/useAuth";
import { registerSchema, type RegisterFormValues } from "@/features/auth/schemas";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Field } from "@/components/ui/label";
import { LogoMark } from "@/components/ui/logo-mark";
import { ApiError } from "@/lib/apiClient";

export default function RegisterPage() {
  const { register: registerAccount } = useAuth();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = React.useState(false);
  const [formError, setFormError] = React.useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormValues>({ resolver: zodResolver(registerSchema) });

  const onSubmit = async (values: RegisterFormValues) => {
    setFormError(null);
    try {
      await registerAccount({
        firstName: values.firstName,
        secondName: values.secondName,
        email: values.email,
        password: values.password,
      });
      toast.success("Hesabınız oluşturuldu.");
      navigate("/panel", { replace: true });
    } catch (err) {
      const message =
        err instanceof ApiError
          ? err.status === 409
            ? "Bu e-posta adresi zaten kayıtlı."
            : err.message
          : "Beklenmeyen bir hata oluştu.";
      setFormError(message);
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
          <h1 className="text-xl font-bold text-ink-900">Hesap oluştur</h1>
          <p className="mt-1 text-sm text-ink-600">Evlerinizi izlemeye birkaç dakikada başlayın.</p>

          <form className="mt-6 space-y-4" onSubmit={handleSubmit(onSubmit)} noValidate>
            <div className="grid grid-cols-2 gap-3">
              <Field label="Ad" htmlFor="firstName" error={errors.firstName?.message}>
                <Input id="firstName" autoComplete="given-name" invalid={!!errors.firstName} {...register("firstName")} />
              </Field>
              <Field label="Soyad" htmlFor="secondName" error={errors.secondName?.message}>
                <Input id="secondName" autoComplete="family-name" invalid={!!errors.secondName} {...register("secondName")} />
              </Field>
            </div>

            <Field label="E-posta" htmlFor="email" error={errors.email?.message}>
              <Input id="email" type="email" autoComplete="email" invalid={!!errors.email} placeholder="ornek@eposta.com" {...register("email")} />
            </Field>

            <Field label="Şifre" htmlFor="password" error={errors.password?.message} hint="En az 4 karakter">
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  autoComplete="new-password"
                  invalid={!!errors.password}
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

            <Field label="Şifre (tekrar)" htmlFor="confirmPassword" error={errors.confirmPassword?.message}>
              <Input
                id="confirmPassword"
                type={showPassword ? "text" : "password"}
                autoComplete="new-password"
                invalid={!!errors.confirmPassword}
                {...register("confirmPassword")}
              />
            </Field>

            {formError && (
              <p role="alert" className="rounded-md bg-danger-50 px-3 py-2 text-[13px] font-medium text-danger-600">
                {formError}
              </p>
            )}

            <Button type="submit" className="w-full" loading={isSubmitting}>
              Hesap oluştur
            </Button>
          </form>
        </div>

        <p className="mt-5 text-center text-sm text-ink-600">
          Zaten hesabınız var mı?{" "}
          <Link to="/giris" className="font-medium text-brand-500 hover:underline">
            Giriş yapın
          </Link>
        </p>
      </div>
    </div>
  );
}
