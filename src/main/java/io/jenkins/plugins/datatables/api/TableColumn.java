package io.jenkins.plugins.datatables.api;

import io.jenkins.plugins.datatables.api.TableModel.DetailedColumnDefinition;

/**
 * Provides a model for table columns that are rendered with JQuery DataTables. The model consists of the following
 * parts:
 *
 * <ul>
 *   <li>header label</li>
 *   <li>header CSS class</li>
 *   <li>column definition</li>
 *   <li>width</li>
 *   <li>tooltip</li>
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

    /**
     * Sets the CSS class for the column {@code <th>} tag. Multiple classes need to be separated using a space.
     *
     * @param headerClass
     *         the CSS class(es) for the {@code <th>} tag
     *
     * @return this column
     */
    public TableColumn setHeaderClass(final ColumnCss headerClass) {
        this.headerClass = headerClass;

        return this;
    }

    /**
     * Sets the width of the column. Will be expanded to the class {@code col-width-[width]}, see
     * {@code jenkins-style.css} for details about the actual percentages.
     *
     * @param width
     *         the width CSS class to select for the column
     *
     * @return this column
     */
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
        /** No special rendering, the display property will be shown as such. */
        NONE(""),
        /**
         * Dates will be shown using Luxon. The display value will be a human friendly relative time like
         * "two weeks ago", rather than an absolute time. A tooltip is available that shows the absolute time.
         */
        DATE("date"),
        /** Disables sorting of the column. Rendering is the same as with {@code NONE}. */
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
