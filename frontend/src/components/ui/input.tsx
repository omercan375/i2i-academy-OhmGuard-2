import * as React from "react";
import { cn } from "@/lib/utils";

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  invalid?: boolean;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, invalid, ...props }, ref) => {
    return (
      <input
        ref={ref}
        className={cn(
          "flex h-9 w-full rounded-md border bg-surface px-3 text-sm text-ink-900 placeholder:text-ink-300 transition-colors",
          "border-border-strong focus-visible:outline-2 focus-visible:outline-brand-500 focus-visible:outline-offset-1",
          "disabled:cursor-not-allowed disabled:opacity-50",
          invalid && "border-danger-500 focus-visible:outline-danger-500",
          className,
        )}
        aria-invalid={invalid || undefined}
        {...props}
      />
    );
  },
);
Input.displayName = "Input";
