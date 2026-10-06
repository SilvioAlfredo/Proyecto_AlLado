import './globals.css';
import { Navbar } from '@/components/layout/Navbar';
import { Providers } from './providers';

export const metadata = {
  title: 'AlLado - Servicios Locales',
  description: 'Personas de tu barrio para lo que necesitás.',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="es">
      <body className="min-h-screen flex flex-col bg-[#0b0b0c] text-zinc-100">
        <Providers>
          <Navbar />
          <main className="flex-1 w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 pb-24 md:pb-10">
            {children}
          </main>
        </Providers>
      </body>
    </html>
  );
}
