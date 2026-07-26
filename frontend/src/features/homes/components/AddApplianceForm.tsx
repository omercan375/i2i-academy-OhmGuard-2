import * as React from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Plus } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useAddAppliance } from "@/features/appliances/hooks";
import { addApplianceSchema, type AddApplianceFormInput, type AddApplianceFormValues } from "@/features/homes/schemas";
import { ApiError } from "@/lib/apiClient";

export function AddApplianceForm({ homeId }: { homeId: string }) {
  const [open, setOpen] = React.useState(false);
  const [formError, setFormError] = React.useState<string | null>(null);
  const addAppliance = useAddAppliance(homeId);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<AddApplianceFormInput, unknown, AddApplianceFormValues>({ resolver: zodResolver(addApplianceSchema) });

  if (!open) {
    return (
      <Button variant="ghost" size="sm" onClick={() => setOpen(true)}>
        <Plus className="size-3.5" aria-hidden="true" />
        Cihaz ekle
      </Button>
    );
  }

  const onSubmit = async (values: AddApplianceFormValues) => {
    setFormError(null);
    try {
      await addAppliance.mutateAsync({ homeId, name: values.name, safeWattLimit: values.safeWattLimit, active: true });
      toast.success(`${values.name} eklendi`);
      reset();
      setOpen(false);
    } catch (err) {
      setFormError(err instanceof ApiError ? err.message : "Cihaz eklenemedi.");
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate className="flex flex-wrap items-start gap-2 rounded-md border border-dashed border-border-strong p-3">
      <div className="min-w-[10rem] flex-1">
        <Input placeholder="Cihaz adı" invalid={!!errors.name} {...register("name")} />
        {errors.name && <p className="mt-1 text-[12px] text-danger-600">{errors.name.message}</p>}
      </div>
      <div className="w-28">
        <Input type="number" step="1" placeholder="Watt" invalid={!!errors.safeWattLimit} {...register("safeWattLimit")} />
        {errors.safeWattLimit && <p className="mt-1 text-[12px] text-danger-600">{errors.safeWattLimit.message}</p>}
      </div>
      <Button type="submit" size="sm" loading={isSubmitting}>
        Ekle
      </Button>
      <Button type="button" variant="ghost" size="sm" onClick={() => { setOpen(false); reset(); setFormError(null); }}>
        Vazgeç
      </Button>
      {formError && <p className="w-full text-[12px] font-medium text-danger-600">{formError}</p>}
    </form>
  );
}
