package io.jenkins.plugins.datatables;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides a model for tables that are rendered with JQuery DataTables. The model consists of the following parts:
 *
 * <ul>
 *   <li>id for the table</li>
 *   <li>column model for each column</li>
 *   <li>content for each row</li>
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
    public String getColumnsDefinition() {
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
     * Returns the configuration of a table. This may be overridden to change the configuration of a table.
     *
     * @return the configuration
     */
    public TableConfiguration getTableConfiguration() {
        return new TableConfiguration();
    }

    /**
     * Returns the configuration of this table as JSON object.
     *
     * @return the table configuration JSON
     */
    public String getTableConfigurationDefinition() {
        return getTableConfiguration().getConfiguration();
    }

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
         *         the value that should be used to display the column
         * @param sort
         *         the value that should be used to sort the column
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
