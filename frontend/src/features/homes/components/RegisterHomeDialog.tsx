import * as React from "react";
import { useFieldArray, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Plus, Trash2 } from "lucide-react";
import { toast } from "sonner";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Field } from "@/components/ui/label";
import { useCreateHome } from "@/features/homes/hooks";
import { registerHomeSchema, type RegisterHomeFormInput, type RegisterHomeFormValues } from "@/features/homes/schemas";
import { ApiError } from "@/lib/apiClient";

export function RegisterHomeDialog({ trigger }: { trigger: React.ReactNode }) {
  const [open, setOpen] = React.useState(false);
  const [formError, setFormError] = React.useState<string | null>(null);
  const createHome = useCreateHome();

  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<RegisterHomeFormInput, unknown, RegisterHomeFormValues>({
    resolver: zodResolver(registerHomeSchema),
    defaultValues: {
      name: "",
      contactEmail: "",
      budgetLimit: undefined,
      normalTariffRate: undefined,
      penaltyTariffRate: undefined,
      appliances: [{ name: "", safeWattLimit: undefined }],
    },
  });

  const { fields, append, remove } = useFieldArray({ control, name: "appliances" });

  const onSubmit = async (values: RegisterHomeFormValues) => {
    setFormError(null);
    try {
      const appliances = values.appliances.filter((a) => a.name.trim().length > 0);
      await createHome.mutateAsync({ ...values, appliances });
      toast.success(`${values.name} eklendi`);
      reset();
      setOpen(false);
    } catch (err) {
      setFormError(err instanceof ApiError ? err.message : "Ev kaydedilemedi.");
    }
  };

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        setOpen(next);
        if (!next) {
          reset();
          setFormError(null);
        }
      }}
    >
      <DialogTrigger asChild>{trigger}</DialogTrigger>
      <DialogContent size="lg">
        <DialogHeader>
          <DialogTitle>Yeni ev ekle</DialogTitle>
          <DialogDescription>Bütçe ve tarife bilgilerini girin, isterseniz cihazlarınızı da ekleyin.</DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-5 px-5 py-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <Field label="Ev adı" htmlFor="home-name" error={errors.name?.message}>
              <Input id="home-name" invalid={!!errors.name} placeholder="Sahil Evi" {...register("name")} />
            </Field>
            <Field label="İletişim e-postası" htmlFor="home-email" error={errors.contactEmail?.message}>
              <Input
                id="home-email"
                type="email"
                invalid={!!errors.contactEmail}
                placeholder="ev@eposta.com"
                {...register("contactEmail")}
              />
            </Field>
          </div>

          <div className="grid gap-4 sm:grid-cols-3">
            <Field label="Bütçe limiti (₺)" htmlFor="budgetLimit" error={errors.budgetLimit?.message}>
              <Input id="budgetLimit" type="number" step="0.01" invalid={!!errors.budgetLimit} {...register("budgetLimit")} />
            </Field>
            <Field label="Normal tarife (₺/kWh)" htmlFor="normalTariffRate" error={errors.normalTariffRate?.message}>
              <Input id="normalTariffRate" type="number" step="0.01" invalid={!!errors.normalTariffRate} {...register("normalTariffRate")} />
            </Field>
            <Field label="Ceza tarifesi (₺/kWh)" htmlFor="penaltyTariffRate" error={errors.penaltyTariffRate?.message}>
              <Input id="penaltyTariffRate" type="number" step="0.01" invalid={!!errors.penaltyTariffRate} {...register("penaltyTariffRate")} />
            </Field>
          </div>

          <div>
            <div className="mb-2 flex items-center justify-between">
              <span className="text-sm font-medium text-ink-600">Cihazlar (opsiyonel)</span>
              <Button type="button" variant="ghost" size="sm" onClick={() => append({ name: "", safeWattLimit: undefined })}>
                <Plus className="size-3.5" aria-hidden="true" />
                Cihaz ekle
              </Button>
            </div>
            <div className="space-y-2">
              {fields.map((field, index) => (
                <div key={field.id} className="flex items-start gap-2">
                  <div className="flex-1">
                    <Input
                      placeholder="Cihaz adı (örn. Klima)"
                      invalid={!!errors.appliances?.[index]?.name}
                      {...register(`appliances.${index}.name` as const)}
                    />
                  </div>
                  <div className="w-32">
                    <Input
                      type="number"
                      step="1"
                      placeholder="Watt"
                      invalid={!!errors.appliances?.[index]?.safeWattLimit}
                      {...register(`appliances.${index}.safeWattLimit` as const)}
                    />
                  </div>
                  <Button
                    type="button"
                    variant="ghost"
                    size="icon"
                    onClick={() => remove(index)}
                    aria-label="Cihazı kaldır"
                    disabled={fields.length === 1}
                  >
                    <Trash2 className="size-4 text-ink-400" aria-hidden="true" />
                  </Button>
                </div>
              ))}
            </div>
          </div>

          {formError && (
            <p role="alert" className="rounded-md bg-danger-50 px-3 py-2 text-[13px] font-medium text-danger-600">
              {formError}
            </p>
          )}

          <DialogFooter className="-mx-5 -mb-4">
            <Button type="button" variant="secondary" onClick={() => setOpen(false)}>
              Vazgeç
            </Button>
            <Button type="submit" loading={isSubmitting}>
              Evi kaydet
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
