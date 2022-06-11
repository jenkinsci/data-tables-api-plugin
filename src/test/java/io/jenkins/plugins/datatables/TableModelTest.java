package io.jenkins.plugins.datatables;

import org.junit.jupiter.api.Test;

import io.jenkins.plugins.datatables.TableModel.DetailedCell;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.*;

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
}
