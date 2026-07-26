import * as React from "react";
import * as DialogPrimitive from "@radix-ui/react-dialog";
import { X } from "lucide-react";
import { cn } from "@/lib/utils";

export const Dialog = DialogPrimitive.Root;
export const DialogTrigger = DialogPrimitive.Trigger;
export const DialogClose = DialogPrimitive.Close;

export function DialogContent({
  className,
  children,
  size = "md",
  ...props
}: React.ComponentPropsWithoutRef<typeof DialogPrimitive.Content> & {
  size?: "sm" | "md" | "lg";
}) {
  const sizeClass = {
    sm: "sm:max-w-sm",
    md: "sm:max-w-lg",
    lg: "sm:max-w-2xl",
  }[size];

  return (
    <DialogPrimitive.Portal>
      <DialogPrimitive.Overlay className="fixed inset-0 z-50 bg-ink-900/35 data-[state=open]:animate-fade-in" />
      <DialogPrimitive.Content
        className={cn(
          "fixed inset-x-0 bottom-0 top-auto z-50 max-h-[88vh] w-full overflow-y-auto rounded-t-lg border-t border-border bg-surface p-0 shadow-[var(--shadow-dialog)] focus:outline-none data-[state=open]:animate-fade-in",
          "sm:inset-x-auto sm:left-1/2 sm:top-1/2 sm:bottom-auto sm:w-[calc(100%-2rem)] sm:max-h-[85vh] sm:-translate-x-1/2 sm:-translate-y-1/2 sm:rounded-lg sm:border",
          sizeClass,
          className,
        )}
        {...props}
      >
        <div className="mx-auto mb-1 mt-2 h-1 w-9 shrink-0 rounded-full bg-border-strong sm:hidden" aria-hidden="true" />
        {children}
        <DialogPrimitive.Close className="absolute right-3 top-3 rounded-sm p-1.5 text-ink-400 transition-colors hover:bg-surface-sunken hover:text-ink-900 focus-visible:outline-2 focus-visible:outline-brand-500">
          <X className="size-4" aria-hidden="true" />
          <span className="sr-only">Kapat</span>
        </DialogPrimitive.Close>
      </DialogPrimitive.Content>
    </DialogPrimitive.Portal>
  );
}

export function DialogHeader({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return <div className={cn("space-y-1 border-b border-border px-5 py-4", className)} {...props} />;
}

export const DialogTitle = React.forwardRef<
  React.ElementRef<typeof DialogPrimitive.Title>,
  React.ComponentPropsWithoutRef<typeof DialogPrimitive.Title>
>(({ className, ...props }, ref) => (
  <DialogPrimitive.Title
    ref={ref}
    className={cn("text-base font-semibold text-ink-900", className)}
    {...props}
  />
));
DialogTitle.displayName = "DialogTitle";

export const DialogDescription = React.forwardRef<
  React.ElementRef<typeof DialogPrimitive.Description>,
  React.ComponentPropsWithoutRef<typeof DialogPrimitive.Description>
>(({ className, ...props }, ref) => (
  <DialogPrimitive.Description ref={ref} className={cn("text-sm text-ink-400", className)} {...props} />
));
DialogDescription.displayName = "DialogDescription";

export function DialogFooter({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn(
        "flex flex-col-reverse gap-2 border-t border-border px-5 py-4 sm:flex-row sm:justify-end",
        className,
      )}
      {...props}
    />
  );
}
