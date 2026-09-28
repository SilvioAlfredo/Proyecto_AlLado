export interface TrabajoRealizado {
    id: number;
    titulo: string;
    subtitulo: string;
    fecha: string;
    esPro: boolean;
}

export interface Certificado {
    id: number;
    titulo: string;
    institucion: string;
    vencimiento: string;
}

export interface PerfilTrabajadorData {
    nombre: string;
    oficio: string;
    calificacion: number;
    verificado: boolean;
    selloPro: boolean;
    matricula: string;
    zonas: string;
    whatsappUrl: string;
    trabajos: TrabajoRealizado[];
    certificados: Certificado[];
}

export const MOCK_PERFIL: PerfilTrabajadorData = {
    nombre: "Juan Pérez",
    oficio: "Electricista & Programador",
    calificacion: 4.9,
    verificado: true,
    selloPro: true,
    matricula: "Matriculado Ley 284",
    zonas: "Zona Norte / Sur",
    whatsappUrl: "https://wa.me/5491112345678",
    trabajos: [
        { id: 1, titulo: "Tablero Trifásico", subtitulo: "Instalación completa", fecha: "Marzo 2026", esPro: true },
        { id: 2, titulo: "Domótica IoT Casa", subtitulo: "Luces y sensores", fecha: "Febrero 2026", esPro: true },
    ],
    certificados: [
        { id: 1, titulo: "Técnico Electricista Categoría A", institucion: "Colegio Profesional", vencimiento: "Exp. 2028" },
    ],
};

export const MOCK_ESTADISTICAS = {
    ingresos: "$485.600",
    porcentajeCrecimiento: "+12.4%",
    periodo: "Abril 2026 • 18 servicios",
    promedioPorTrabajo: "$26.977",
    distribucionSemanas: [
        { semana: "S1", cantidad: 3, activo: false },
        { semana: "S2", cantidad: 5, activo: false },
        { semana: "S3", cantidad: 8, activo: true },
        { semana: "S4", cantidad: 6, activo: false },
    ],
    tendencias: [
        { rubro: "Electricidad e Iluminación", porcentaje: 78, nivel: "Alta" },
        { rubro: "Plomería & Gas", porcentaje: 54, nivel: "Media" },
    ],
    impacto: {
        vecinosAyudados: 14,
        radioKm: 3,
    },
};