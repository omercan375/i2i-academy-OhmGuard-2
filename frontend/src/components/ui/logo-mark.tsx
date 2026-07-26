export function LogoMark({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 32 32" className={className} aria-hidden="true">
      <path
        d="M16 2 L28 6.5 V15 C28 22.5 22.5 28.5 16 30.5 C9.5 28.5 4 22.5 4 15 V6.5 Z"
        fill="#1F4D3A"
      />
      <text
        x="16"
        y="21.5"
        textAnchor="middle"
        fontFamily="Georgia, 'Times New Roman', serif"
        fontSize="14"
        fontWeight="700"
        fill="#F5F3EE"
      >
        Ω
      </text>
    </svg>
  );
}
