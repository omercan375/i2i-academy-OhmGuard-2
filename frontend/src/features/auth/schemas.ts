import { z } from "zod";

export const loginSchema = z.object({
  email: z.string().min(5, "E-posta en az 5 karakter olmalı").max(255).email("Geçerli bir e-posta girin"),
  password: z.string().min(3, "Şifre en az 3 karakter olmalı").max(255),
});

export type LoginFormValues = z.infer<typeof loginSchema>;

export const registerSchema = z
  .object({
    firstName: z.string().min(3, "En az 3 karakter olmalı").max(100),
    secondName: z.string().min(2, "En az 2 karakter olmalı").max(100),
    email: z.string().min(5).max(255).email("Geçerli bir e-posta girin"),
    password: z.string().min(4, "Şifre en az 4 karakter olmalı").max(255),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Şifreler eşleşmiyor",
    path: ["confirmPassword"],
  });

export type RegisterFormValues = z.infer<typeof registerSchema>;
