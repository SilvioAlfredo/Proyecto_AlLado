/**
 * Suite unitaria del cliente móvil (AlLado, Expo / React Native).
 *
 * - `jest-expo` es el preset oficial de Expo: replica el runtime de la app
 *   (transformaciones de Babel, mocks de los módulos nativos y resolución de
 *   assets) para que los tests corran como en el dispositivo.
 * - `setupFiles` carga los matchers de `@testing-library/react-native`.
 * - `transformIgnorePatterns` deja pasar los paquetes de Expo y React Native, que
 *   vienen sin compilar.
 * - `moduleNameMapper` respeta el alias `@/*` del `tsconfig.json`.
 */
module.exports = {
  preset: 'jest-expo',
  setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/$1',
  },
  testMatch: ['<rootDir>/src/**/*.test.{ts,tsx}', '<rootDir>/app/**/*.test.{ts,tsx}'],
  // Salida JUnit XML: la consume el generador del informe de pruebas (XML por
  // caso, con su estado y su duracion).
  reporters: [
    'default',
    // `classNameTemplate` deja la ruta del archivo de prueba en el classname:
    // el generador del informe la usa para la trazabilidad de cada caso.
    ['jest-junit', { outputDirectory: 'test-results', outputName: 'junit.xml',
                     classNameTemplate: '{filepath}::{title}' }],
  ],
  collectCoverageFrom: ['src/**/*.{ts,tsx}', '!src/**/*.test.{ts,tsx}'],
};
