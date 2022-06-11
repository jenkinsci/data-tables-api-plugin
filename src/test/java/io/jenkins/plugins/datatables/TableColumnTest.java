package io.jenkins.plugins.datatables;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import io.jenkins.plugins.util.JenkinsFacade;

import static io.jenkins.plugins.datatables.TableColumn.*;
import static io.jenkins.plugins.datatables.assertions.Assertions.*;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the classes {@link TableColumn} and {@link ColumnBuilder}.
 *
 * @author Ullrich Hafner
 */
class TableColumnTest {
    private static final String LABEL = "label";
    private static final String RESPONSIVE_PRIORITY = "responsivePriority";
    private static final String DATA = "data";
    private static final String TYPE = "type";
    private static final String RENDER = "render";
    private static final String KEY = "one";

    @Test
    void shouldEnsureValidBuilderState() {
        ColumnBuilder builder = new ColumnBuilder();

        assertThatIllegalArgumentException().isThrownBy(() -> builder.withResponsivePriority(-1));

        assertThatIllegalArgumentException().isThrownBy(builder::build).withMessageContainingAll("withHeaderLabel");
        builder.withHeaderLabel(LABEL);

        assertThatIllegalArgumentException().isThrownBy(builder::build).withMessageContainingAll("withDataPropertyKey");
        builder.withDataPropertyKey(KEY);

        assertThat(builder.build()).hasHeaderLabel(LABEL).hasHeaderClass(StringUtils.EMPTY);
    }

    @Test
    void shouldCreateColumnWithSimpleDescription() {
        ColumnBuilder builder = new ColumnBuilder();

        TableColumn noPriority = builder.withHeaderLabel(LABEL)
                .withDataPropertyKey(KEY)
                .build();

        assertThat(noPriority).hasHeaderLabel(LABEL).hasHeaderClass(StringUtils.EMPTY);
        assertThatJson(noPriority.getDefinition()).node(DATA).isEqualTo(KEY);
        assertThatJson(noPriority.getDefinition()).node(RESPONSIVE_PRIORITY).isAbsent();

        TableColumn withPriority = builder.withHeaderLabel(LABEL)
                .withDataPropertyKey(KEY)
                .withResponsivePriority(1)
                .build();

        assertThatJson(withPriority.getDefinition()).node(RESPONSIVE_PRIORITY).isEqualTo(1);
    }

    @Test
    void shouldCreateColumnWithComplexDescription() {
        ColumnBuilder builder = new ColumnBuilder();

        TableColumn noPriority = builder.withHeaderLabel(LABEL)
                .withDataPropertyKey(KEY)
                .withType("integer")
                .withDetailedCell()
                .build();

        assertThat(noPriority).hasHeaderLabel(LABEL).hasHeaderClass(StringUtils.EMPTY);
        assertThatJson(noPriority.getDefinition()).node(DATA).isEqualTo(KEY);
        assertThatJson(noPriority.getDefinition()).node(TYPE).isEqualTo("integer");
        assertThatJson(noPriority.getDefinition()).node(RENDER).node("_").isEqualTo("display");
        assertThatJson(noPriority.getDefinition()).node(RENDER).node("sort").isEqualTo("sort");
        assertThatJson(noPriority.getDefinition()).node(RESPONSIVE_PRIORITY).isAbsent();

        TableColumn withPriority = builder.withResponsivePriority(1).build();
        assertThatJson(withPriority.getDefinition()).node(RESPONSIVE_PRIORITY).isEqualTo(1);
    }

    @Test
    void shouldCreateColumnWithOtherProperties() {
        ColumnBuilder builder = new ColumnBuilder();

        TableColumn column = builder.withHeaderLabel(LABEL)
                .withDataPropertyKey(KEY)
                .withHeaderClass(ColumnCss.DATE)
                .withType("integer")
                .build();

        assertThat(column).hasHeaderLabel(LABEL).hasHeaderClass("date");
        assertThatJson(column.getDefinition()).node(DATA).isEqualTo(KEY);
        assertThatJson(column.getDefinition()).node(TYPE).isEqualTo("integer");
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
}
