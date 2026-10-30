package com.al_lado.e2e.tests.accesibilidad;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.al_lado.e2e.core.BaseE2ETest;
import com.al_lado.e2e.core.E2EConfig;
import com.al_lado.e2e.core.Waits;
import com.al_lado.e2e.pages.LoginPage;
import com.deque.html.axecore.results.CheckedNode;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;

@DisplayName("TC-ACC-001: ACC-01 auditoría axe-core WCAG 2.2 AA")
class AccessibilityAxeTest extends BaseE2ETest {

    private static final String WCAG_TAG = "wcag22aa";

    @Test
    @DisplayName("TC-ACC-001: inicio, login y dashboard trabajador sin violations WCAG 2.2 AA")
    void pantallasPrincipalesCumplenWcag22Aa() throws IOException {
        List<PageScan> scans = new ArrayList<>();

        driver.get(E2EConfig.baseUrl() + "/");
        Waits.visible(driver, By.cssSelector("body"));
        scans.add(analyze("Inicio público"));

        new LoginPage(driver).open();
        scans.add(analyze("Login"));

        loginAsWorker();
        scans.add(analyze("Dashboard trabajador"));

        Path report = Path.of("target", "accesibilidad", "axe-report.md");
        writeReport(report, scans);

        int violations = scans.stream().mapToInt(scan -> scan.results().getViolations().size()).sum();
        assertThat(violations)
                .as("Violations axe-core; revisar %s", report.toAbsolutePath())
                .isZero();
    }

    private PageScan analyze(String label) {
        Results results = new AxeBuilder()
                .withTags(List.of(WCAG_TAG))
                .analyze(driver);
        return new PageScan(label, driver.getCurrentUrl(), results);
    }

    private static void writeReport(Path report, List<PageScan> scans) throws IOException {
        Files.createDirectories(report.getParent());
        StringBuilder markdown = new StringBuilder("# Informe axe-core - WCAG 2.2 AA\n\n");
        markdown.append("Generated from Selenium E2E; runtime violations are not inferred from source.\n\n");

        int total = 0;
        for (PageScan scan : scans) {
            List<Rule> violations = scan.results().getViolations();
            total += violations.size();
            markdown.append("## ").append(scan.label()).append("\n\n")
                    .append("URL: ").append(scan.url()).append("\n\n")
                    .append("Violations: ").append(violations.size()).append("\n\n");
            for (Rule violation : violations) {
                markdown.append("### ").append(violation.getId())
                        .append(" (impact: ").append(violation.getImpact()).append(")\n\n")
                        .append(violation.getDescription()).append("\n\n")
                        .append("Help: ").append(violation.getHelpUrl()).append("\n\n");
                for (CheckedNode node : violation.getNodes()) {
                    markdown.append("- Target: `").append(node.getTarget())
                            .append("`\n  Failure: ").append(node.getFailureSummary())
                            .append("\n  HTML: `").append(node.getHtml()).append("`\n");
                }
                markdown.append("\n");
            }
        }

        markdown.insert(markdown.indexOf("\n\n") + 2,
                "Total violations: " + total + "\n\n");
        Files.writeString(report, markdown.toString());
    }

    private record PageScan(String label, String url, Results results) {
    }
}