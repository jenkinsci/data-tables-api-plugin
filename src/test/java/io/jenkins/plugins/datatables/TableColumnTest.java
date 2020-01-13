package io.jenkins.plugins.datatables;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import io.jenkins.plugins.datatables.TableColumn.*;
import io.jenkins.plugins.util.JenkinsFacade;

import static io.jenkins.plugins.datatables.TableColumn.*;
import static io.jenkins.plugins.datatables.assertions.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link TableColumn}.
 *
 * @author Ullrich Hafner
 */
class TableColumnTest {
    private static final String LABEL = "label";
    private static final int WIDTH = 2;

    @Test
    void shouldCreateColumnWithSimpleDescription() {
        TableColumn column = new TableColumn(LABEL, "one");

        assertThat(column).hasHeaderLabel(LABEL);
        assertThat(column).hasDefinition("{\"data\": \"one\"}");
        assertThat(column).hasHeaderClass(StringUtils.EMPTY);
        assertThat(column).hasWidth(1);
    }

    @Test
    void shouldCreateColumnWithComplexDescription() {
        TableColumn column = new TableColumn(LABEL, "one", "integer");

        assertThat(column).hasHeaderLabel(LABEL);
        assertThat(column).hasDefinition("{  \"type\": \"integer\",  "
                + "\"data\": \"one\",  "
                + "\"render\": {     \"_\": \"display\",     \"sort\": \"sort\"  }}");
        assertThat(column).hasHeaderClass(StringUtils.EMPTY);
        assertThat(column).hasWidth(1);
    }

    @Test
    void shouldCreateColumnWithOtherProperties() {
        TableColumn column = new TableColumn(LABEL, "simple")
                .setHeaderClass(ColumnCss.DATE)
                .setWidth(WIDTH);

        assertThat(column).hasHeaderLabel(LABEL);
        assertThat(column).hasDefinition("{\"data\": \"simple\"}");
        assertThat(column).hasHeaderClass("date");
        assertThat(column).hasWidth(WIDTH);
    }

    @Test
    void shouldCreateDetailsColumnDiv() {
        JenkinsFacade jenkinsFacade = mock(JenkinsFacade.class);
        when(jenkinsFacade.getImagePath(DETAILS_COLUMN_ICON_NAME)).thenReturn("/path/to/icon");

        assertThat(TableColumn.renderDetailsColumn("details text", jenkinsFacade))
                .isEqualTo("<div class=\"details-control\" data-description=\"details text\">"
                        + "<svg class=\"details-icon svg-icon\"><use href></use>"
                        + "</svg>"
                        + "</div>");
    }
}
