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
import { useUpdateApplianceLimit } from "@/features/appliances/hooks";
import { updateWattLimitSchema, type UpdateWattLimitFormInput, type UpdateWattLimitFormValues } from "@/features/homes/schemas";
import { ApiError } from "@/lib/apiClient";

export function EditApplianceLimitDialog({
  homeId,
  applianceId,
  applianceName,
  currentLimit,
}: {
  homeId: string;
  applianceId: string;
  applianceName: string;
  currentLimit: number;
}) {
  const [open, setOpen] = React.useState(false);
  const [formError, setFormError] = React.useState<string | null>(null);
  const updateLimit = useUpdateApplianceLimit(homeId);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<UpdateWattLimitFormInput, unknown, UpdateWattLimitFormValues>({
    resolver: zodResolver(updateWattLimitSchema),
    defaultValues: { newWattLimit: currentLimit },
  });

  const onSubmit = async (values: UpdateWattLimitFormValues) => {
    setFormError(null);
    try {
      await updateLimit.mutateAsync({ homeId, applianceId, newWattLimit: values.newWattLimit });
      toast.success(`${applianceName} limiti güncellendi.`);
      setOpen(false);
    } catch (err) {
      setFormError(err instanceof ApiError ? err.message : "Limit güncellenemedi.");
    }
  };

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        setOpen(next);
        if (next) reset({ newWattLimit: currentLimit });
        else setFormError(null);
      }}
    >
      <DialogTrigger asChild>
        <Button variant="ghost" size="icon" aria-label={`${applianceName} güvenli limitini düzenle`}>
          <Pencil className="size-4 text-ink-400" aria-hidden="true" />
        </Button>
      </DialogTrigger>
      <DialogContent size="sm">
        <DialogHeader>
          <DialogTitle>Güvenli limiti güncelle</DialogTitle>
          <DialogDescription>{applianceName} için watt sınırını değiştirin.</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} noValidate className="px-5 py-4">
          <Field label="Güvenli limit (W)" htmlFor="newWattLimit" error={errors.newWattLimit?.message}>
            <Input id="newWattLimit" type="number" step="1" invalid={!!errors.newWattLimit} {...register("newWattLimit")} />
          </Field>
          {formError && (
            <p role="alert" className="mt-3 rounded-md bg-danger-50 px-3 py-2 text-[13px] font-medium text-danger-600">
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
