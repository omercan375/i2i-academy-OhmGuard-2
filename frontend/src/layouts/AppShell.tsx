import { NavLink, Outlet } from "react-router-dom";
import { Bell, ChevronDown, LayoutGrid, LogOut, User } from "lucide-react";
import { useAuth } from "@/features/auth/useAuth";
import { LogoMark } from "@/components/ui/logo-mark";
import { cn } from "@/lib/utils";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

const navItems = [
  { to: "/panel", label: "Panel", icon: LayoutGrid, end: true },
  { to: "/panel/bildirimler", label: "Bildirimler", icon: Bell, end: false },
];

export function AppShell() {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-background">
      <header className="sticky top-0 z-30 border-b border-border bg-background">
        <div className="mx-auto flex h-14 max-w-6xl items-center justify-between gap-4 px-4 sm:px-6">
          <NavLink to="/panel" className="flex items-center gap-2 text-lg font-bold text-ink-900">
            <LogoMark className="size-6" />
            Ohm Guard
          </NavLink>

          <nav className="hidden items-center gap-1 sm:flex" aria-label="Ana gezinme">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  cn(
                    "flex items-center gap-1.5 border-b-2 px-2 py-4 text-sm font-medium transition-colors",
                    isActive
                      ? "border-brand-500 text-ink-900"
                      : "border-transparent text-ink-600 hover:text-ink-900",
                  )
                }
              >
                <item.icon className="size-4" aria-hidden="true" />
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="flex items-center gap-3">
            <NavLink
              to="/panel/bildirimler"
              className="flex size-8 items-center justify-center rounded-sm text-ink-600 hover:bg-surface-sunken sm:hidden"
              aria-label="Bildirimler"
            >
              <Bell className="size-[18px]" aria-hidden="true" />
            </NavLink>

            <DropdownMenu>
              <DropdownMenuTrigger
                aria-label="Hesap menüsü"
                className="flex items-center gap-1.5 rounded-sm px-1.5 py-1 text-sm font-medium text-ink-900 transition-colors hover:bg-surface-sunken focus-visible:outline-2 focus-visible:outline-brand-500"
              >
                <span className="flex size-6 items-center justify-center rounded-full bg-brand-50 text-brand-600">
                  <User className="size-3.5" aria-hidden="true" />
                </span>
                <span className="hidden max-w-[8rem] truncate sm:inline">{user?.firstName ?? "Hesabım"}</span>
                <ChevronDown className="size-3.5 text-ink-400" aria-hidden="true" />
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuLabel>
                  {user ? `${user.firstName} ${user.lastName}` : "Hesabım"}
                  {user?.email && <div className="truncate font-normal text-ink-400">{user.email}</div>}
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem onSelect={logout} className="text-danger-600 focus:bg-danger-50 focus:text-danger-600">
                  <LogOut className="size-4" aria-hidden="true" />
                  Çıkış yap
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>

        <nav className="flex items-center gap-1 border-t border-border px-4 py-1 sm:hidden" aria-label="Ana gezinme (mobil)">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) =>
                cn(
                  "flex flex-1 items-center justify-center gap-1.5 rounded-sm px-3 py-2 text-sm font-medium transition-colors",
                  isActive ? "text-brand-500" : "text-ink-600",
                )
              }
            >
              <item.icon className="size-4" aria-hidden="true" />
              {item.label}
            </NavLink>
          ))}
        </nav>
      </header>

      <main className="mx-auto max-w-6xl px-4 py-6 sm:px-6">
        <Outlet />
      </main>
    </div>
  );
}
