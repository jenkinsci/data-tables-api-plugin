package io.jenkins.plugins.datatables;

import org.junit.jupiter.api.Test;

import io.jenkins.plugins.datatables.TableConfiguration.SelectStyle;

import static io.jenkins.plugins.datatables.TableConfigurationAssert.*;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.*;

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
        assertThat(configuration).isNotUseStateSave();
        assertThat(configuration).isUsePaging();

        assertThatJson(configuration).satisfiesAnyOf(
                t -> assertThatJson(t).node("configuration").asString().isEqualTo("{}"),
                t -> assertThatJson(t).node("useResponsive").isEqualTo(false),
                t -> assertThatJson(t).node("useColReorder").isEqualTo(false),
                t -> assertThatJson(t).node("useButtons").isEqualTo(false),
                t -> assertThatJson(t).node("stateSave").isEqualTo(false),
                t -> assertThatJson(t).node("useSelect").isEqualTo(false),
                t -> assertThatJson(t).node("paging").isEqualTo(true)
        );
    }

    @Test
    void shouldCreateResponsiveConfiguration() {
        TableConfiguration configuration = new TableConfiguration()
                .responsive();

        assertThat(configuration).hasConfiguration("{\"responsive\":true}");
        assertThat(configuration).isNotUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isUseResponsive();
        assertThat(configuration).isNotUseStateSave();
        assertThat(configuration).isUsePaging();

        assertThatJson(configuration).satisfiesAnyOf(
                t -> assertThatJson(t).node("configuration").asString().isEqualTo("{}"),
                t -> assertThatJson(t).node("useResponsive").isEqualTo(true),
                t -> assertThatJson(t).node("useColReorder").isEqualTo(false),
                t -> assertThatJson(t).node("useButtons").isEqualTo(false),
                t -> assertThatJson(t).node("stateSave").isEqualTo(false),
                t -> assertThatJson(t).node("useSelect").isEqualTo(false),
                t -> assertThatJson(t).node("paging").isEqualTo(true)
        );
    }

    @Test
    void shouldCreateSelectConfiguration() {
        TableConfiguration configuration = new TableConfiguration().select(SelectStyle.SINGLE);

        assertThat(configuration).hasConfiguration("{\"select\":\"single\"}");
        assertThat(configuration).isNotUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isNotUseResponsive();
        assertThat(configuration).isUseSelect();
        assertThat(configuration).isNotUseStateSave();
        assertThat(configuration).isUsePaging();

        assertThatJson(configuration).satisfiesAnyOf(
                t -> assertThatJson(t).node("configuration").asString().isEqualTo("{}"),
                t -> assertThatJson(t).node("useResponsive").isEqualTo(false),
                t -> assertThatJson(t).node("useColReorder").isEqualTo(false),
                t -> assertThatJson(t).node("useButtons").isEqualTo(false),
                t -> assertThatJson(t).node("stateSave").isEqualTo(false),
                t -> assertThatJson(t).node("useSelect").asString().isEqualTo("single"),
                t -> assertThatJson(t).node("paging").isEqualTo(true)
        );
    }

    @Test
    void shouldCreateColReorderConfiguration() {
        TableConfiguration configuration = new TableConfiguration()
                .colReorder();

        assertThat(configuration).hasConfiguration("{\"colReorder\":true}");
        assertThat(configuration).isNotUseButtons();
        assertThat(configuration).isUseColReorder();
        assertThat(configuration).isNotUseResponsive();
        assertThat(configuration).isNotUseStateSave();
        assertThat(configuration).isUsePaging();

        assertThatJson(configuration).satisfiesAnyOf(
                t -> assertThatJson(t).node("configuration").asString().isEqualTo("{}"),
                t -> assertThatJson(t).node("useResponsive").isEqualTo(false),
                t -> assertThatJson(t).node("useColReorder").isEqualTo(true),
                t -> assertThatJson(t).node("useButtons").isEqualTo(false),
                t -> assertThatJson(t).node("stateSave").isEqualTo(false),
                t -> assertThatJson(t).node("useSelect").isEqualTo(false),
                t -> assertThatJson(t).node("paging").isEqualTo(true)
        );
    }

    @Test
    void shouldCreateButtonsDefaultConfiguration() {
        TableConfiguration configuration = new TableConfiguration()
                .buttons();

        assertThat(configuration).hasConfiguration("{\"buttons\":true}");
        assertThat(configuration).isUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isNotUseResponsive();
        assertThat(configuration).isNotUseStateSave();
        assertThat(configuration).isUsePaging();

        assertThatJson(configuration).satisfiesAnyOf(
                t -> assertThatJson(t).node("configuration").asString().isEqualTo("{}"),
                t -> assertThatJson(t).node("useResponsive").isEqualTo(false),
                t -> assertThatJson(t).node("useColReorder").isEqualTo(false),
                t -> assertThatJson(t).node("useButtons").isEqualTo(true),
                t -> assertThatJson(t).node("stateSave").isEqualTo(false),
                t -> assertThatJson(t).node("useSelect").isEqualTo(false),
                t -> assertThatJson(t).node("paging").isEqualTo(true)
        );
    }

    @Test
    void shouldCreateSpecificButtonsConfiguration() {
        TableConfiguration configuration = new TableConfiguration()
                .buttons("colvis", "print");

        assertThat(configuration).hasConfiguration("{\"buttons\":[\"colvis\",\"print\"]}");
        assertThat(configuration).isUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isNotUseResponsive();
        assertThat(configuration).isNotUseStateSave();
        assertThat(configuration).isUsePaging();

        assertThatJson(configuration).satisfiesAnyOf(
                t -> assertThatJson(t).node("configuration").asString().isEqualTo("{}"),
                t -> assertThatJson(t).node("useResponsive").isEqualTo(false),
                t -> assertThatJson(t).node("useColReorder").isEqualTo(false),
                t -> assertThatJson(t).node("useButtons").isEqualTo("colvis: \"print\""),
                t -> assertThatJson(t).node("stateSave").isEqualTo(false),
                t -> assertThatJson(t).node("useSelect").isEqualTo(false),
                t -> assertThatJson(t).node("paging").isEqualTo(true)
        );
    }

    @Test
    void shouldCreateSaveStateConfiguration() {
        TableConfiguration configuration = new TableConfiguration()
                .stateSave();

        assertThat(configuration).hasConfiguration("{\"stateSave\":true}");
        assertThat(configuration).isNotUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isNotUseResponsive();
        assertThat(configuration).isUseStateSave();
        assertThat(configuration).isUsePaging();

        assertThatJson(configuration).satisfiesAnyOf(
                t -> assertThatJson(t).node("configuration").asString().isEqualTo("{}"),
                t -> assertThatJson(t).node("useResponsive").isEqualTo(false),
                t -> assertThatJson(t).node("useColReorder").isEqualTo(false),
                t -> assertThatJson(t).node("useButtons").isEqualTo(false),
                t -> assertThatJson(t).node("useSelect").isEqualTo(false),
                t -> assertThatJson(t).node("stateSave").isEqualTo(true),
                t -> assertThatJson(t).node("paging").isEqualTo(true)
        );
    }

    @Test
    void shouldCreateNoPagingConfiguration() {
        TableConfiguration configuration = new TableConfiguration()
                .noPaging();

        assertThat(configuration).hasConfiguration("{\"paging\":false}");
        assertThat(configuration).isNotUseButtons();
        assertThat(configuration).isNotUseColReorder();
        assertThat(configuration).isNotUseResponsive();
        assertThat(configuration).isNotUseStateSave();
        assertThat(configuration).isNotUsePaging();

        assertThatJson(configuration).satisfiesAnyOf(
                t -> assertThatJson(t).node("configuration").asString().isEqualTo("{}"),
                t -> assertThatJson(t).node("useResponsive").isEqualTo(false),
                t -> assertThatJson(t).node("useColReorder").isEqualTo(false),
                t -> assertThatJson(t).node("useButtons").isEqualTo(false),
                t -> assertThatJson(t).node("useSelect").isEqualTo(false),
                t -> assertThatJson(t).node("stateSave").isEqualTo(false),
                t -> assertThatJson(t).node("paging").isEqualTo(false)
        );
    }
}
