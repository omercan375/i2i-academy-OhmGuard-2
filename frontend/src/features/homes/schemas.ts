import { z } from "zod";

export const applianceInputSchema = z.object({
  name: z.string().min(2, "En az 2 karakter").max(150),
  safeWattLimit: z.coerce.number({ message: "Sayı girin" }).positive("0'dan büyük olmalı"),
});

export const registerHomeSchema = z.object({
  name: z.string().min(2, "En az 2 karakter").max(150),
  contactEmail: z.string().min(5).max(255).email("Geçerli bir e-posta girin"),
  budgetLimit: z.coerce.number({ message: "Sayı girin" }).positive("0'dan büyük olmalı"),
  normalTariffRate: z.coerce.number({ message: "Sayı girin" }).positive("0'dan büyük olmalı"),
  penaltyTariffRate: z.coerce.number({ message: "Sayı girin" }).positive("0'dan büyük olmalı"),
  // Rows left completely blank are dropped on submit — only rows the user actually
  // started filling in are validated, and errors stay pinned to their original row.
  appliances: z
    .array(
      z.object({
        name: z.string().max(150),
        safeWattLimit: z.coerce.number({ message: "Sayı girin" }),
      }),
    )
    .superRefine((rows, ctx) => {
      rows.forEach((row, index) => {
        const hasName = row.name.trim().length > 0;
        const hasWatt = row.safeWattLimit > 0;
        if (!hasName && !hasWatt) return;
        if (row.name.trim().length < 2) {
          ctx.addIssue({ code: z.ZodIssueCode.custom, message: "En az 2 karakter", path: [index, "name"] });
        }
        if (!hasWatt) {
          ctx.addIssue({ code: z.ZodIssueCode.custom, message: "0'dan büyük olmalı", path: [index, "safeWattLimit"] });
        }
      });
    }),
});

export type RegisterHomeFormInput = z.input<typeof registerHomeSchema>;
export type RegisterHomeFormValues = z.output<typeof registerHomeSchema>;

export const addApplianceSchema = applianceInputSchema;
export type AddApplianceFormInput = z.input<typeof addApplianceSchema>;
export type AddApplianceFormValues = z.output<typeof addApplianceSchema>;

export const updateBudgetSchema = z.object({
  newBudgetLimit: z.coerce.number({ message: "Sayı girin" }).positive("0'dan büyük olmalı"),
});
export type UpdateBudgetFormInput = z.input<typeof updateBudgetSchema>;
export type UpdateBudgetFormValues = z.output<typeof updateBudgetSchema>;

export const updateTariffSchema = z.object({
  normTariffRate: z.coerce.number({ message: "Sayı girin" }).positive("0'dan büyük olmalı"),
  penaltyTariffRate: z.coerce.number({ message: "Sayı girin" }).positive("0'dan büyük olmalı"),
});
export type UpdateTariffFormInput = z.input<typeof updateTariffSchema>;
export type UpdateTariffFormValues = z.output<typeof updateTariffSchema>;

export const updateWattLimitSchema = z.object({
  newWattLimit: z.coerce.number({ message: "Sayı girin" }).positive("0'dan büyük olmalı"),
});
export type UpdateWattLimitFormInput = z.input<typeof updateWattLimitSchema>;
export type UpdateWattLimitFormValues = z.output<typeof updateWattLimitSchema>;
