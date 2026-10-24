import React, { useCallback, useState } from 'react';
import { Text, ScrollView, StyleSheet } from 'react-native';
import { useFocusEffect } from 'expo-router';
import { Screen, Brand, Title, AppInput, AppButton, Note } from '../../../src/components/ui';
import { empresaService } from '../../../src/api/services';
import { useAuth } from '../../../src/auth/AuthContext';
import { colors } from '../../../src/theme';

export default function Empresa() {
    const { userId } = useAuth();
    const [eid, setEid] = useState<number | null>(userId);
    const [nombreEmpresa, setNombreEmpresa] = useState('');
    const [cuit, setCuit] = useState('');
    const [desc, setDesc] = useState('');
    const [web, setWeb] = useState('');
    const [verificado, setVerificado] = useState(false);
    const [msg, setMsg] = useState('');

    useFocusEffect(useCallback(() => {
        (async () => {
            const id = userId;
            setEid(id);
            if (!id) return;
            const p = await empresaService.perfil(id).catch(() => null);
            if (p) {
                const data = p as Record<string, unknown>;
                // MOB05: Leer nombreEmpresa (con fallback a nombre por datos legados)
                setNombreEmpresa(String(data.nombreEmpresa ?? data.nombre ?? ''));
                setCuit(String(data.cuit ?? ''));
                setDesc(String(data.descripcion ?? ''));
                setWeb(String(data.sitioWeb ?? ''));
                setVerificado(Boolean(data.verificado));
            }
        })();
    }, [userId]));

    return (
        <Screen><ScrollView>
            <Brand />
            <Title title="Tu empresa en AlLado" subtitle={verificado ? 'Empresa verificada' : 'Sin verificar (verifica el admin)'} />
            <AppInput label="Nombre de la empresa *" value={nombreEmpresa} onChangeText={setNombreEmpresa} />
            <AppInput label="CUIT" value={cuit} onChangeText={setCuit} />
            <AppInput label="Descripción" value={desc} onChangeText={setDesc} multiline />
            <AppInput label="Sitio web" value={web} onChangeText={setWeb} autoCapitalize="none" />
            <AppButton
                title="Guardar perfil empresarial"
                onPress={async () => {
                    if (eid == null) return;
                    // MOB05: Enviar nombreEmpresa en vez de nombre
                    await empresaService.guardar(eid, {
                        nombreEmpresa,
                        cuit,
                        descripcion: desc,
                        sitioWeb: web
                    });
                    setMsg('Guardado.');
                }}
            />
            {msg ? <Text style={styles.ok}>{msg}</Text> : null}
            <Note>Solo lectura para verificación. El admin marca verificado.</Note>
        </ScrollView></Screen>
    );
}
const styles = StyleSheet.create({ ok: { color: colors.success } });