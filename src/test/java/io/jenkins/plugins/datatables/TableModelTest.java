package io.jenkins.plugins.datatables;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.jenkins.plugins.datatables.TableColumn.ColumnBuilder;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link TableModel}.
 *
 * @author Ullrich Hafner
 */
class TableModelTest {
    @Test
    void shouldCreateStringSortingCell() {
        DetailedCell<String> cell = new DetailedCell<>("Display", "Sort");

        assertThatJson(cell).isEqualTo("{display: \"Display\", sort: \"Sort\"}");
    }

    @Test
    void shouldCreateIntSortingCell() {
        DetailedCell<Integer> cell = new DetailedCell<>("Display", 123);

        assertThatJson(cell).isEqualTo("{display: \"Display\", sort: 123}");
    }

    @Test
    void shouldCreateColumns() {
        TableModel tableModel = spy(TableModel.class);

        List<TableColumn> columns = new ArrayList<>();
        columns.add(createColumn("left", "leftProperty"));
        when(tableModel.getColumns()).thenReturn(columns);

        assertThatJson(tableModel.getColumnsDefinition()).isArray().hasSize(1).containsExactly(
                "{data:\"leftProperty\",\"type\":\"string\"}");

        columns.add(createColumn("right", "rightProperty"));
        assertThatJson(tableModel.getColumnsDefinition()).isArray().hasSize(2).containsExactly(
                "{data:\"leftProperty\",\"type\":\"string\"}", "{data:\"rightProperty\",\"type\":\"string\"}");
    }

    private TableColumn createColumn(final String label, final String property) {
        return new ColumnBuilder().withHeaderLabel(label).withDataPropertyKey(property).build();
    }

    @Test
    void shouldCreateEmptyConfiguration() {
        TableModel tableModel = spy(TableModel.class);

        List<TableColumn> columns = new ArrayList<>();
        columns.add(createColumn("left", "leftProperty"));
        when(tableModel.getColumns()).thenReturn(columns);

        assertThatJson(tableModel.getTableConfiguration()).satisfiesAnyOf(
                t -> assertThatJson(t).node("configuration").asString().isEqualTo("{}"),
                t -> assertThatJson(t).node("useResponsive").isEqualTo(false),
                t -> assertThatJson(t).node("useColReorder").isEqualTo(false),
                t -> assertThatJson(t).node("useButtons").isEqualTo(false),
                t -> assertThatJson(t).node("useSelect").isEqualTo(false)
        );
    }
}
