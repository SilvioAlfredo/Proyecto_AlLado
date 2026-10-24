import React, { useState, useEffect } from 'react';
import {
  StyleSheet,
  Text,
  View,
  FlatList,
  TextInput,
  TouchableOpacity,
  SafeAreaView,
  StatusBar,
  KeyboardAvoidingView,
  Platform,
  TouchableWithoutFeedback,
  Keyboard,
  ActivityIndicator
} from 'react-native';

const SERVICIOS_DEFAULT = [
  { id: '1', titulo: 'Reparación de Cañerías', rubro: 'Plomería', contacto: 'Carlos M.' },
  { id: '2', titulo: 'Instalación de Tomas y Térmicas', rubro: 'Electricidad', contacto: 'Martín G.' },
  { id: '3', titulo: 'Pintura Interior y Exterior', rubro: 'Pintura', contacto: 'Lucía P.' },
  { id: '4', titulo: 'Corte de Pasto y Mantenimiento', rubro: 'Jardinería', contacto: 'Esteban R.' },
];

export default function App() {
  const [servicios, setServicios] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [busqueda, setBusqueda] = useState('');
  const [nuevoTitulo, setNuevoTitulo] = useState('');
  const [nuevoRubro, setNuevoRubro] = useState('');

  useEffect(() => {
    const cargarDatos = async () => {
      try {
        await new Promise((resolve) => setTimeout(resolve, 600));
        setServicios(SERVICIOS_DEFAULT);
      } catch (error) {
        console.error('Error al cargar datos:', error);
      } finally {
        setCargando(false);
      }
    };

    cargarDatos();
  }, []);

  const serviciosFiltrados = servicios.filter((item) =>
      item.titulo.toLowerCase().includes(busqueda.toLowerCase()) ||
      item.rubro.toLowerCase().includes(busqueda.toLowerCase())
  );

  const agregarServicio = () => {
    if (!nuevoTitulo.trim() || !nuevoRubro.trim()) return;

    const nuevo = {
      id: Date.now().toString(),
      titulo: nuevoTitulo.trim(),
      rubro: nuevoRubro.trim(),
      contacto: 'Vecino solicitante'
    };

    setServicios((prev) => [nuevo, ...prev]);
    setNuevoTitulo('');
    setNuevoRubro('');
    Keyboard.dismiss();
  };

  return (
      <SafeAreaView style={styles.contenedor}>
        <StatusBar barStyle="dark-content" />

        <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
          <KeyboardAvoidingView
              behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
              style={styles.contenido}
          >
            {/* Header */}
            <View style={styles.header}>
              <Text style={styles.nombreApp}>AlLado</Text>
              <Text style={styles.subtitulo}>Módulo Móvil de Asistencia Vecinal</Text>
            </View>

            {/* Buscador */}
            <TextInput
                style={styles.buscador}
                placeholder="Buscar oficio o servicio..."
                placeholderTextColor="#8e8e93"
                value={busqueda}
                onChangeText={setBusqueda}
                clearButtonMode="while-editing"
            />

            {/* Formulario */}
            <View style={styles.formulario}>
              <Text style={styles.formTitulo}>Publicar necesidad barrial</Text>
              <TextInput
                  style={styles.input}
                  placeholder="¿Qué servicio necesitás?"
                  placeholderTextColor="#8e8e93"
                  value={nuevoTitulo}
                  onChangeText={setNuevoTitulo}
              />
              <TextInput
                  style={styles.input}
                  placeholder="Rubro (ej: Gas, Electricidad)"
                  placeholderTextColor="#8e8e93"
                  value={nuevoRubro}
                  onChangeText={setNuevoRubro}
              />
              <TouchableOpacity style={styles.botonPublicar} onPress={agregarServicio}>
                <Text style={styles.textoBoton}>Publicar Requerimiento</Text>
              </TouchableOpacity>
            </View>

            {/* Listado */}
            <Text style={styles.tituloSeccion}>Servicios Disponibles en el Barrio</Text>

            {cargando ? (
                <ActivityIndicator size="small" color="#0055d4" style={styles.loader} />
            ) : (
                <FlatList
                    data={serviciosFiltrados}
                    keyExtractor={(item) => item.id}
                    showsVerticalScrollIndicator={false}
                    ListEmptyComponent={
                      <Text style={styles.vacioTexto}>No se encontraron resultados.</Text>
                    }
                    renderItem={({ item }) => (
                        <View style={styles.tarjeta}>
                          <View style={styles.tarjetaCabecera}>
                            <Text style={styles.rubroBadge}>{item.rubro}</Text>
                            <Text style={styles.contactoTexto}>{item.contacto}</Text>
                          </View>
                          <Text style={styles.servicioTitulo}>{item.titulo}</Text>
                        </View>
                    )}
                />
            )}
          </KeyboardAvoidingView>
        </TouchableWithoutFeedback>
      </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  contenedor: {
    flex: 1,
    backgroundColor: '#f2f2f7',
  },
  contenido: {
    flex: 1,
    paddingHorizontal: 16,
    paddingTop: 8,
  },
  header: {
    marginBottom: 14,
  },
  nombreApp: {
    fontSize: 26,
    fontWeight: '800',
    color: '#0055d4',
  },
  subtitulo: {
    fontSize: 13,
    color: '#636366',
  },
  buscador: {
    height: 42,
    backgroundColor: '#ffffff',
    borderRadius: 10,
    paddingHorizontal: 12,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: '#e5e5ea',
    fontSize: 14,
  },
  formulario: {
    backgroundColor: '#ffffff',
    padding: 12,
    borderRadius: 12,
    marginBottom: 14,
    borderWidth: 1,
    borderColor: '#e5e5ea',
  },
  formTitulo: {
    fontSize: 13,
    fontWeight: '700',
    color: '#1c1c1e',
    marginBottom: 8,
  },
  input: {
    height: 38,
    backgroundColor: '#f2f2f7',
    borderRadius: 8,
    paddingHorizontal: 10,
    marginBottom: 8,
    fontSize: 13,
  },
  botonPublicar: {
    backgroundColor: '#0055d4',
    paddingVertical: 10,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 2,
  },
  textoBoton: {
    color: '#ffffff',
    fontWeight: '600',
    fontSize: 13,
  },
  tituloSeccion: {
    fontSize: 15,
    fontWeight: '700',
    marginBottom: 8,
    color: '#1c1c1e',
  },
  loader: {
    marginTop: 20,
  },
  vacioTexto: {
    textAlign: 'center',
    color: '#8e8e93',
    marginTop: 20,
    fontSize: 13,
  },
  tarjeta: {
    backgroundColor: '#ffffff',
    borderRadius: 10,
    padding: 12,
    marginBottom: 8,
    elevation: 2,
    borderWidth: 1,
    borderColor: '#e5e5ea',
  },
  tarjetaCabecera: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 4,
  },
  rubroBadge: {
    fontSize: 11,
    fontWeight: '700',
    color: '#0055d4',
    textTransform: 'uppercase',
  },
  contactoTexto: {
    fontSize: 11,
    color: '#8e8e93',
  },
  servicioTitulo: {
    fontSize: 14,
    fontWeight: '600',
    color: '#1c1c1e',
  },
});