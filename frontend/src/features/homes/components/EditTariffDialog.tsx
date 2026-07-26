import * as React from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Pencil } from "lucide-react";
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
import { useUpdateTariffRates } from "@/features/homes/hooks";
import { updateTariffSchema, type UpdateTariffFormInput, type UpdateTariffFormValues } from "@/features/homes/schemas";
import { ApiError } from "@/lib/apiClient";

export function EditTariffDialog({
  homeId,
  currentNormalRate,
  currentPenaltyRate,
}: {
  homeId: string;
  currentNormalRate: number;
  currentPenaltyRate: number;
}) {
  const [open, setOpen] = React.useState(false);
  const [formError, setFormError] = React.useState<string | null>(null);
  const updateTariff = useUpdateTariffRates(homeId);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<UpdateTariffFormInput, unknown, UpdateTariffFormValues>({
    resolver: zodResolver(updateTariffSchema),
    defaultValues: { normTariffRate: currentNormalRate, penaltyTariffRate: currentPenaltyRate },
  });

  const onSubmit = async (values: UpdateTariffFormValues) => {
    setFormError(null);
    try {
      await updateTariff.mutateAsync({ homeId, ...values });
      toast.success("Tarife oranları güncellendi.");
      setOpen(false);
    } catch (err) {
      setFormError(err instanceof ApiError ? err.message : "Tarife güncellenemedi.");
    }
  };

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        setOpen(next);
        if (next) reset({ normTariffRate: currentNormalRate, penaltyTariffRate: currentPenaltyRate });
        else setFormError(null);
      }}
    >
      <DialogTrigger asChild>
        <Button variant="ghost" size="sm">
          <Pencil className="size-3.5" aria-hidden="true" />
          Tarifeyi düzenle
        </Button>
      </DialogTrigger>
      <DialogContent size="sm">
        <DialogHeader>
          <DialogTitle>Tarife oranlarını güncelle</DialogTitle>
          <DialogDescription>Normal ve ceza tarifesi birim fiyatlarını değiştirin.</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4 px-5 py-4">
          <Field label="Normal tarife (₺/kWh)" htmlFor="normTariffRate" error={errors.normTariffRate?.message}>
            <Input id="normTariffRate" type="number" step="0.01" invalid={!!errors.normTariffRate} {...register("normTariffRate")} />
          </Field>
          <Field label="Ceza tarifesi (₺/kWh)" htmlFor="penaltyTariffRate" error={errors.penaltyTariffRate?.message}>
            <Input id="penaltyTariffRate" type="number" step="0.01" invalid={!!errors.penaltyTariffRate} {...register("penaltyTariffRate")} />
          </Field>
          {formError && (
            <p role="alert" className="rounded-md bg-danger-50 px-3 py-2 text-[13px] font-medium text-danger-600">
              {formError}
            </p>
          )}
        </form>
        <DialogFooter>
          <Button type="button" variant="secondary" onClick={() => setOpen(false)}>
            Vazgeç
          </Button>
          <Button onClick={handleSubmit(onSubmit)} loading={isSubmitting}>
            Kaydet
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
