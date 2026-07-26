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
import { useUpdateBudgetLimit } from "@/features/homes/hooks";
import { updateBudgetSchema, type UpdateBudgetFormInput, type UpdateBudgetFormValues } from "@/features/homes/schemas";
import { ApiError } from "@/lib/apiClient";

export function EditBudgetDialog({ homeId, currentBudget }: { homeId: string; currentBudget: number }) {
  const [open, setOpen] = React.useState(false);
  const [formError, setFormError] = React.useState<string | null>(null);
  const updateBudget = useUpdateBudgetLimit(homeId);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<UpdateBudgetFormInput, unknown, UpdateBudgetFormValues>({
    resolver: zodResolver(updateBudgetSchema),
    defaultValues: { newBudgetLimit: currentBudget },
  });

  const onSubmit = async (values: UpdateBudgetFormValues) => {
    setFormError(null);
    try {
      await updateBudget.mutateAsync({ homeId, newBudgetLimit: values.newBudgetLimit });
      toast.success("Bütçe limiti güncellendi.");
      setOpen(false);
    } catch (err) {
      setFormError(err instanceof ApiError ? err.message : "Bütçe güncellenemedi.");
    }
  };

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        setOpen(next);
        if (next) reset({ newBudgetLimit: currentBudget });
        else setFormError(null);
      }}
    >
      <DialogTrigger asChild>
        <Button variant="ghost" size="sm">
          <Pencil className="size-3.5" aria-hidden="true" />
          Bütçeyi düzenle
        </Button>
      </DialogTrigger>
      <DialogContent size="sm">
        <DialogHeader>
          <DialogTitle>Bütçe limitini güncelle</DialogTitle>
          <DialogDescription>Aylık harcama tavanını değiştirin.</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} noValidate className="px-5 py-4">
          <Field label="Yeni bütçe limiti (₺)" htmlFor="newBudgetLimit" error={errors.newBudgetLimit?.message}>
            <Input id="newBudgetLimit" type="number" step="0.01" invalid={!!errors.newBudgetLimit} {...register("newBudgetLimit")} />
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
