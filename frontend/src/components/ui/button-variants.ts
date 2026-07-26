import { cva } from "class-variance-authority";

export const buttonVariants = cva(
  "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-colors disabled:pointer-events-none disabled:opacity-50 focus-visible:outline-2 focus-visible:outline-brand-500 focus-visible:outline-offset-2",
  {
    variants: {
      variant: {
        primary: "bg-brand-500 text-white hover:bg-brand-600 active:bg-brand-700",
        secondary: "bg-surface text-ink-900 border border-border-strong hover:bg-surface-sunken",
        ghost: "text-ink-600 hover:bg-surface-sunken hover:text-ink-900",
        destructive: "bg-danger-500 text-white hover:bg-danger-600",
        link: "text-brand-500 underline-offset-4 hover:underline p-0 h-auto",
      },
      size: {
        sm: "h-8 px-3 text-[13px]",
        md: "h-9 px-3.5",
        lg: "h-11 px-5 text-[15px]",
        icon: "h-9 w-9 shrink-0",
      },
    },
    defaultVariants: {
      variant: "primary",
      size: "md",
    },
  },
);
