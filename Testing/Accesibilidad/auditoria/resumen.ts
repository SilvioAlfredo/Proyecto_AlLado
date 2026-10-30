import type { Reporter, TestCase, TestResult } from '@playwright/test/reporter';
import fs from 'node:fs';
import path from 'node:path';

export default class Summary implements Reporter {
  rows: Record<string, unknown>[] = [];
  async onTestEnd(test: TestCase, result: TestResult) {
    async function read(name: string) {
      const a = result.attachments.find(a => a.name === name);
      if (!a) return null;
      return JSON.parse(a.body ? a.body.toString('utf8') : await fs.promises.readFile(a.path!, 'utf8'));
    }
    const axe = await read('resultado-axe.json');
    const scope = await read('alcance.json');
    const http = await read('diagnostico-http.json');
    this.rows.push({
      ruta: scope?.route ?? test.titlePath().join(' / '), rol: scope?.role,
      proyecto: test.parent.project()?.name, estadoRunner: result.status,
      accesibilidad: !axe ? 'NO AUDITADA' : axe.violations.length ? 'FALLOS CONFIRMADOS'
        : axe.incomplete.length ? 'REVISIÓN MANUAL PENDIENTE' : 'SIN INFRACCIONES AUTOMÁTICAS',
      violations: axe?.violations.length ?? null,
      incomplete: axe?.incomplete.length ?? null,
      nodosInconclusos: axe?.incomplete.reduce((n: number, r: any) => n + r.nodes.length, 0) ?? null,
      passes: axe?.passes.length ?? null, timestamp: axe?.timestamp,
      erroresHttp: http,
      motivo: scope?.excluded || test.annotations.filter(a => a.type === 'skip').map(a => a.description).join('; '),
      salida: scope?.outputDir,
    });
  }
  async onEnd() {
    const dir = path.resolve('reporte-accesibilidad');
    await fs.promises.mkdir(dir, { recursive: true });
    await fs.promises.writeFile(path.join(dir, 'resumen.json'), JSON.stringify(this.rows, null, 2));
    const keys = ['ruta','rol','proyecto','estadoRunner','accesibilidad','violations','incomplete','nodosInconclusos','passes','timestamp','motivo','salida'];
    const quote = (v: unknown) => '"' + String(v ?? '').replace(/"/g, '""') + '"';
    await fs.promises.writeFile(path.join(dir, 'resumen.csv'), '\uFEFF' + [keys, ...this.rows.map(r => keys.map(k => r[k]))].map(r => r.map(quote).join(',')).join('\n'));
    console.log('\nResumen: reporte-accesibilidad/resumen.json y resumen.csv');
  }
}
