package com.al_lado.e2e.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Genera la evidencia de cada caso: captura PNG, HTML de la pagina y URL final.
 *
 * <p>Es la evidencia que el plan de pruebas (IEEE 829, seccion 9) exige linkear
 * por caso: "Screenshot UI para TC-G/TC-L/TC-M".</p>
 */
public final class EvidenceCapture {

    private EvidenceCapture() {
    }

    /**
     * Guarda la evidencia de una ejecucion.
     *
     * @param driver       navegador activo; puede ser {@code null} si el fallo ocurrio
     *                     antes de crearlo
     * @param testId       identificador del caso (por ejemplo {@code TC-L-001})
     * @param description  descripcion corta del paso que se estaba ejecutando
     */
    public static void capture(WebDriver driver, String testId, String description) {
        try {
            Path dir = Paths.get(E2EConfig.evidenceDir(), sanitize(testId));
            Files.createDirectories(dir);

            if (description != null && !description.isBlank()) {
                Files.writeString(dir.resolve("paso.txt"),
                        description + System.lineSeparator(), StandardCharsets.UTF_8);
            }

            if (driver == null) {
                Files.writeString(dir.resolve("error.txt"),
                        "No habia navegador activo para capturar la evidencia."
                                + System.lineSeparator(), StandardCharsets.UTF_8);
                return;
            }

            try {
                byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Files.write(dir.resolve("captura.png"), png);
            } catch (RuntimeException e) {
                writeProblem(dir, "No se pudo capturar la imagen: " + e);
            }

            try {
                Files.writeString(dir.resolve("pagina.html"),
                        driver.getPageSource(), StandardCharsets.UTF_8);
            } catch (RuntimeException e) {
                writeProblem(dir, "No se pudo guardar el HTML: " + e);
            }

            try {
                Files.writeString(dir.resolve("url.txt"),
                        driver.getCurrentUrl() + System.lineSeparator(), StandardCharsets.UTF_8);
            } catch (RuntimeException e) {
                writeProblem(dir, "No se pudo guardar la URL: " + e);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir la evidencia de " + testId, e);
        }
    }

    private static void writeProblem(Path dir, String message) {
        try {
            Files.writeString(dir.resolve("error.txt"),
                    message + System.lineSeparator(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // La evidencia es best effort: no debe enmascarar el fallo real.
        }
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^A-Za-z0-9._-]+", "_");
    }
}
