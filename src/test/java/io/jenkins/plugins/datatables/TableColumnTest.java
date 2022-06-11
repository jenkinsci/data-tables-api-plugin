package io.jenkins.plugins.datatables;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import io.jenkins.plugins.util.JenkinsFacade;

import static io.jenkins.plugins.datatables.TableColumn.*;
import static io.jenkins.plugins.datatables.assertions.Assertions.*;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link TableColumn}.
 *
 * @author Ullrich Hafner
 */
class TableColumnTest {
    private static final String LABEL = "label";
    private static final int WIDTH = 2;
    private static final String RESPONSIVE_PRIORITY = "responsivePriority";
    private static final String DATA = "data";
    private static final String DEFAULT_CONTENT = "defaultContent";
    private static final String TYPE = "type";

    @Test
    void shouldCreateColumnWithSimpleDescription() {
        TableColumn column = new TableColumn(LABEL, "one");

        assertThat(column).hasHeaderLabel(LABEL);
        assertThat(column).hasDefinition("{  \"data\": \"one\",  \"defaultContent\": \"\"}");
        assertThat(column).hasHeaderClass(StringUtils.EMPTY);
        assertThat(column).hasWidth(1);

        assertThatJson(column.getDefinition()).node(DATA).isEqualTo("one");
        assertThatJson(column.getDefinition()).node(DEFAULT_CONTENT).asString().isEmpty();
        assertThatJson(column.getDefinition()).node(RESPONSIVE_PRIORITY).isAbsent();

        column.setPriority(1);
        assertThatJson(column.getDefinition()).node(RESPONSIVE_PRIORITY).isEqualTo(1);
    }

    @Test
    void shouldCreateColumnWithComplexDescription() {
        TableColumn column = new TableColumn(LABEL, "one", "integer");

        assertThat(column).hasHeaderLabel(LABEL);
        assertThat(column).hasDefinition("{  \"type\": \"integer\",  "
                + "\"data\": \"one\",  "
                + "\"defaultContent\": \"\",  "
                + "\"render\": {     \"_\": \"display\",     \"sort\": \"sort\"  }}");
        assertThat(column).hasHeaderClass(StringUtils.EMPTY);
        assertThat(column).hasWidth(1);

        column.setPriority(1);
        assertThatJson(column.getDefinition()).node(DATA).isEqualTo("one");
        assertThatJson(column.getDefinition()).node(DEFAULT_CONTENT).asString().isEmpty();
        assertThatJson(column.getDefinition()).node(RESPONSIVE_PRIORITY).isEqualTo(1);
        assertThatJson(column.getDefinition()).node(TYPE).isEqualTo("integer");
        assertThatJson(column.getDefinition()).node("render").node("_").isEqualTo("display");
        assertThatJson(column.getDefinition()).node("render").node("sort").isEqualTo("sort");
    }

    @Test
    void shouldCreateColumnWithOtherProperties() {
        TableColumn column = new TableColumn(LABEL, "simple")
                .setHeaderClass(ColumnCss.DATE)
                .setWidth(WIDTH);

        assertThat(column).hasHeaderLabel(LABEL);
        assertThat(column).hasDefinition("{  \"data\": \"simple\",  \"defaultContent\": \"\"}");
        assertThat(column).hasHeaderClass("date");
        assertThat(column).hasWidth(WIDTH);
    }

    @Test
    void shouldCreateDetailsColumnDiv() {
        JenkinsFacade jenkinsFacade = mock(JenkinsFacade.class);
        when(jenkinsFacade.getImagePath(DETAILS_COLUMN_ICON_NAME)).thenReturn("/path/to/icon");

        assertThat(renderDetailsColumn("details text", jenkinsFacade))
                .isEqualTo("<div class=\"details-control\" data-description=\"details text\">"
                        + "<svg class=\"details-icon svg-icon\"><use href></use>"
                        + "</svg>"
                        + "</div>");
    }

    @Test
    void shouldCreateHiddenColumn() {
        TableColumn column = new TableColumn(LABEL, "Hidden", "String")
                .setHeaderClass(ColumnCss.HIDDEN)
                .setWidth(0);

        assertThat(column).hasHeaderLabel(LABEL);
        assertThat(column).hasDefinition("{  \"type\": \"String\",  "
                + "\"data\": \"Hidden\",  "
                + "\"defaultContent\": \"\",  "
                + "\"render\": {     \"_\": \"display\",     \"sort\": \"sort\"  }}");
        assertThat(column).hasHeaderClass("hidden");
        assertThat(column).hasWidth(0);
    }
}
