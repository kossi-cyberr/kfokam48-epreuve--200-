import type { Metadata } from "next";
import Link from "next/link";
import "./globals.css";

export const metadata: Metadata = {
  title: "KOS — Présence & relecture par les pairs",
  description: "Épreuve finale fullstack KFOKAM48",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="fr">
      <body>
        <header className="entete">
          <Link href="/" className="marque">
            KOS
          </Link>
          <nav>
            <Link href="/">Formateur</Link>
            <Link href="/etudiant">Étudiant</Link>
            <Link href="/relecteur">Relecteur</Link>
          </nav>
        </header>
        <main>{children}</main>
      </body>
    </html>
  );
}
