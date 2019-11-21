package io.jenkins.plugins.datatables.api;

import io.jenkins.plugins.datatables.api.TableModel.DetailedColumnDefinition;

/**
 * Provides a model for table columns that are rendered with JQuery DataTables. The model consists of the following
 * parts:
 *
 * <ul>
 * <li>header label</li>
 * <li>header CSS class</li>
 * <li>column definition</li>
 * <li>width</li>
 * <li>tooltip</li>
 * </ul>
 *
 * @author Ullrich Hafner
 */
public class TableColumn {
    private final String headerLabel;
    private final String definition;

    private ColumnCss headerClass = ColumnCss.NONE;
    private int width = 1;

    /**
     * Creates a simple column: it maps the specified property of the row entity to the column value.
     *
     * @param headerLabel
     *         the label of the column header
     * @param dataPropertyName
     *         the property to extract from the entity, it will be shown as column value
     */
    public TableColumn(final String headerLabel, final String dataPropertyName) {
        this.headerLabel = headerLabel;
        definition = String.format("{\"data\": \"%s\"}", dataPropertyName);
    }

    /**
     * Creates a complex column: it maps the specified property of the row entity to the display and sort attributes of
     * the column. The property {@code dataPropertyName} must be of type {@link DetailedColumnDefinition}.
     *
     * @param headerLabel
     *         the label of the column header
     * @param dataPropertyName
     *         the property to extract from the entity, it will be shown as column value
     * @param columnDataType
     *         JQuery DataTables data type of the column
     */
    public TableColumn(final String headerLabel, final String dataPropertyName, final String columnDataType) {
        this.headerLabel = headerLabel;
        definition = String.format("{"
                + "  \"type\": \"%s\","
                + "  \"data\": \"%s\","
                + "  \"render\": {"
                + "     \"_\": \"display\","
                + "     \"sort\": \"sort\""
                + "  }"
                + "}", columnDataType, dataPropertyName);
    }

    public TableColumn setHeaderClass(final ColumnCss headerClass) {
        this.headerClass = headerClass;

        return this;
    }

    public TableColumn setWidth(final int width) {
        this.width = width;

        return this;
    }

    public String getHeaderLabel() {
        return headerLabel;
    }

    public String getHeaderClass() {
        return headerClass.toString();
    }

    public int getWidth() {
        return width;
    }

    public String getDefinition() {
        return definition;
    }

    /**
     * Supported CSS classes that will enable special handling or rendering for table columns.
     */
    public enum ColumnCss {
        NONE(""),
        DATE("date"),
        NO_SORT("nosort");

        private final String cssClass;

        ColumnCss(final String cssClass) {
            this.cssClass = cssClass;
        }

        @Override
        public String toString() {
            return cssClass;
        }
    }
}
