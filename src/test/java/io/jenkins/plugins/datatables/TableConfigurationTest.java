package io.jenkins.plugins.datatables;

import io.jenkins.plugins.datatables.options.SelectStyle;
import org.junit.jupiter.api.Test;

import static io.jenkins.plugins.datatables.TableConfigurationAssert.*;

/**
 * Tests for the class {@link TableConfiguration}.
 *
 * @author Andreas Pabst
 */
public class TableConfigurationTest {

    @Test
    void shouldCreateEmptyConfiguration() {
        TableConfiguration configuration = new TableConfiguration();

        assertThat(configuration).hasConfiguration("{}");
        assertThat(configuration).isNotUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isNotUseResponsive();
    }

    @Test
    void shouldCreateResponsiveConfiguration() {
        TableConfiguration configuration = new TableConfiguration()
                .responsive();

        assertThat(configuration).hasConfiguration("{\"responsive\":true}");
        assertThat(configuration).isNotUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isUseResponsive();
    }

    @Test
    void shouldCreateSelectConfiguration() {
        TableConfiguration configuration = new TableConfiguration().select(SelectStyle.SINGLE);

        assertThat(configuration).hasConfiguration("{\"select\":\"single\"}");
        assertThat(configuration).isNotUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isNotUseResponsive();
        assertThat(configuration).isUseSelect();
    }

    @Test
    void shouldCreateColReorderConfiguration() {
        TableConfiguration configuration = new TableConfiguration()
                .colReorder();

        assertThat(configuration).hasConfiguration("{\"colReorder\":true}");
        assertThat(configuration).isNotUseButtons();
        assertThat(configuration).isUseColReorder();
        assertThat(configuration).isNotUseResponsive();
    }

    @Test
    void shouldCreateButtonsDefaultConfiguration() {
        TableConfiguration configuration = new TableConfiguration()
                .buttons();

        assertThat(configuration).hasConfiguration("{\"buttons\":true}");
        assertThat(configuration).isUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isNotUseResponsive();
    }

    @Test
    void shouldCreateSpecificButtonsConfiguration() {
        TableConfiguration configuration = new TableConfiguration()
                .buttons("colvis", "print");

        assertThat(configuration).hasConfiguration("{\"buttons\":[\"colvis\",\"print\"]}");
        assertThat(configuration).isUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isNotUseResponsive();
    }

    @Test
    void shouldCreateSaveStateConfiguration() {
        TableConfiguration configuration = new TableConfiguration()
                .stateSave();

        assertThat(configuration).hasConfiguration("{\"stateSave\":true}");
        assertThat(configuration).isNotUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isNotUseResponsive();
    }

}
