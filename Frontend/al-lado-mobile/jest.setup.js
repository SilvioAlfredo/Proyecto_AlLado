/* eslint-env jest */
/**
 * Preparación común de la suite móvil.
 *
 * - `@testing-library/react-native` v13+ trae los matchers de Jest Incorporados:
 *   basta con importarlo en algún test para que queden disponibles.
 * - `expo-secure-store` se sustituye por un almacén en memoria: en el dispositivo
 *   guarda la sesión en el llavero del sistema, y en los tests tiene que ser un
 *   simple `Map`.
 * - `expo-router` se mockea: la navegación real necesita un árbol de rutas montado
 *   y las pruebas de sesión no la ejercitan.
 */

global.IS_REACT_ACT_ENVIRONMENT = true;

jest.mock('expo-secure-store', () => {
  const almacen = new Map();
  return {
    getItemAsync: jest.fn(async (clave) => (almacen.has(clave) ? almacen.get(clave) : null)),
    setItemAsync: jest.fn(async (clave, valor) => {
      almacen.set(clave, valor);
    }),
    deleteItemAsync: jest.fn(async (clave) => {
      almacen.delete(clave);
    }),
    __almacen: almacen,
  };
});

jest.mock('expo-router', () => ({
  useRouter: () => ({
    push: jest.fn(),
    replace: jest.fn(),
    back: jest.fn(),
    dismiss: jest.fn(),
  }),
  useLocalSearchParams: () => ({}),
  useFocusEffect: (callback) => {
    const React = require('react');
    React.useEffect(() => callback(), [callback]);
  },
  router: { push: jest.fn(), replace: jest.fn(), back: jest.fn() },
  Href: jest.fn(),
}));
