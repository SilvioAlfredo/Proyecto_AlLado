import type { Applicant, Category, EmployerRequest } from "../types/employer.types";

export const categories: Category[] = [
  { name: "Electricidad", icon: "⚡", active: 12 },
  { name: "Plomería", icon: "🔧", active: 8 },
  { name: "Hogar", icon: "⌂", active: 15 },
  { name: "Pintura", icon: "🎨", active: 6 },
  { name: "Jardinería", icon: "🌿", active: 9 },
];

export const requests: EmployerRequest[] = [
  { id: 1, title: "Reparación de panel LED", category: "Tecnología",
    description: "El panel no enciende, parece tener un problema en el driver.",
    distance: "A 300 m", candidates: 4, status: "active" },
  { id: 2, title: "Cambio de grifería", category: "Plomería",
    description: "Necesito cambiar la grifería de la cocina. Tengo el repuesto.",
    distance: "A 1,2 km", candidates: 2, status: "active" },
];

export const applicants: Applicant[] = [
  { id: 1, initials: "JP", name: "Juan Pérez", verified: true, pro: true, rating: 4.9, reviews: 42,
    proposal: "Tengo experiencia en instalación y reparación de luminarias LED. Puedo pasar mañana y solucionarlo.",
    budget: "$15.000", availability: "Disponible Sáb 10:00" },
  { id: 2, initials: "LM", name: "Lucas Martínez", verified: true, pro: true, rating: 4.7, reviews: 28,
    proposal: "Trabajo con iluminación LED. Incluye revisión completa y garantía.",
    budget: "$18.000", availability: "Disponible Vie 18:00" },
  { id: 3, initials: "FC", name: "Federico Castro", verified: true, pro: false, rating: 4.5, reviews: 17,
    proposal: "Puedo revisar el panel y cambiar el driver si es necesario. Cuento con herramientas.",
    budget: "$14.000", availability: "Disponible Hoy 18:00" },
];

export const specialties = [
  "Electricidad", "Plomería", "Hogar", "Tecnología", "Pintura", "Jardinería",
  "Carpintería", "Limpieza", "Albañilería", "Gas", "Aire Acondicionado", "Otros",
];
