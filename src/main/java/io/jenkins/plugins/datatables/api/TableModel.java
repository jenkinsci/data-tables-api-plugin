package io.jenkins.plugins.datatables.api;

import java.util.List;
import java.util.StringJoiner;

/**
 * Provides a model for tables that are rendered with JQuery DataTables.
 * The model consists of the following parts:
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
     * Returns the table header labels of the table.
     *
     * @return the table headers
     */
    public abstract List<String> getHeaders();

    /**
     * Returns the widths of the table columns.
     *
     * @return the width of the table columns
     */
    public abstract List<Integer> getWidths();

    /**
     * Returns the column definitions of the table.
     *
     * @return the column definitions
     */
    public final String getColumnsDefinition() {
        ColumnDefinitionBuilder builder = new ColumnDefinitionBuilder();
        configureColumns(builder);
        return builder.toString();
    }

    /**
     * Configures the columns of the report table.
     *
     * @param builder
     *         the columns definition builder
     */
    protected abstract void configureColumns(ColumnDefinitionBuilder builder);

    /**
     * Returns the rows of the table.
     *
     * @return the rows
     */
    public abstract List<Object> getRows();

    /**
     * A JQuery DataTables column definition builder. Provides simple columns that extract a given entity property as
     * column value or complex columns that provide different properties to sort and display a column.
     */
    public static class ColumnDefinitionBuilder {
        private final StringJoiner columns = new StringJoiner(",", "[", "]");

        /**
         * Adds a new simple column that maps the specified property of the row entity to the column value.
         *
         * @param dataPropertyName
         *         the property to extract from the entity, it will be shown as column value
         *
         * @return this
         */
        public ColumnDefinitionBuilder add(final String dataPropertyName) {
            columns.add(String.format("{\"data\": \"%s\"}", dataPropertyName));

            return this;
        }

        /**
         * Adds a new complex column that maps the specified property of the row entity to the display and sort
         * attributes of the column. The property {@code dataPropertyName} must be of type {@link
         * DetailedColumnDefinition}.
         *
         * @param dataPropertyName
         *         the property to extract from the entity, it will be shown as column value
         * @param columnDataType
         *         JQuery DataTables data type of the column
         *
         * @return this
         * @see DetailedColumnDefinition
         */
        public ColumnDefinitionBuilder add(final String dataPropertyName, final String columnDataType) {
            columns.add(String.format("{"
                    + "  \"type\": \"%s\","
                    + "  \"data\": \"%s\","
                    + "  \"render\": {"
                    + "     \"_\": \"display\","
                    + "     \"sort\": \"sort\""
                    + "  }"
                    + "}", columnDataType, dataPropertyName));

            return this;
        }

        @Override
        public String toString() {
            return columns.toString();
        }
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
