package io.jenkins.plugins.datatables.api;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides a model for tables that are rendered with JQuery DataTables. The model consists of the following parts:
 *
 * <ul>
 * <li>header name for each column</li>
 * <li>column definition for each column</li>
 * <li>width for each column</li>
 * <li>row content</li>
 * <li>content for each row</li>
 * </ul>
 *
 * @author Ullrich Hafner
 */
public abstract class TableModel {
    /**
     * Returns the ID of the table. All IDs must be unique on a given web page.
     *
     * @return the table ID
     */
    public abstract String getId();

    /**
     * Returns the table columns.
     *
     * @return the table columns
     */
    public abstract List<TableColumn> getColumns();

    /**
     * Returns the column definitions of this table as JSON array.
     *
     * @return the column definitions
     * @see <a href="https://datatables.net/manual/data/#Objects">DataTables API Reference</a>
     */
    public final String getColumnsDefinition() {
        return getColumns().stream()
                .map(TableColumn::getDefinition)
                .collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * Returns the rows of the table.
     *
     * @return the rows
     */
    public abstract List<Object> getRows();

    /**
     * A column value attribute that provides a {@code display} and {@code sort} property so that a JQuery DataTables
     * can use different properties to sort and display a column.
     */
    public static class DetailedColumnDefinition {
        private final String display;
        private final String sort;

        /**
         * Creates a new {@link DetailedColumnDefinition}.
         *
         * @param display
         *         the entity property that should be used to display the column
         * @param sort
         *         the entity property that should be used to sort the column
         *
         * @see <a href="https://datatables.net/reference/option/columns.type">DataTables Column Types</a>
         */
        public DetailedColumnDefinition(final String display, final String sort) {
            this.display = display;
            this.sort = sort;
        }

        public String getDisplay() {
            return display;
        }

        public String getSort() {
            return sort;
        }
    }
}
