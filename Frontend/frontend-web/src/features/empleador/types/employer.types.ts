export type EmployerScreen = "dashboard" | "applicants" | "new-request";

export interface Category { name: string; icon: string; active: number; }
export interface EmployerRequest {
  id: number; title: string; category: string; description: string;
  distance: string; candidates: number; status: "active" | "closed";
}
export interface Applicant {
  id: number; initials: string; name: string; verified: boolean; pro: boolean;
  rating: number; reviews: number; proposal: string; budget: string; availability: string;
}
export interface NewRequestForm { description: string; specialty: string; location: string; }
