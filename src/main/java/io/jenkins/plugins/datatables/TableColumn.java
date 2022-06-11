package io.jenkins.plugins.datatables;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.hm.hafner.util.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.CheckForNull;

import j2html.tags.UnescapedText;

import io.jenkins.plugins.fontawesome.api.SvgTag;
import io.jenkins.plugins.util.JenkinsFacade;

import static j2html.TagCreator.*;

/**
 * Provides a model for table columns that are rendered with JQuery DataTables. The model consists of the following
 * parts:
 *
 * <ul>
 *   <li>header label</li>
 *   <li>header CSS class</li>
 *   <li>column definition</li>
 *   <li>responsive priority</li>
 *   <li>tooltip</li>
 * </ul>
 *
 * @author Ullrich Hafner
 */
public class TableColumn {
    @VisibleForTesting
    static final String DETAILS_COLUMN_ICON_NAME = "circle-plus";

    /**
     * Renders an expandable details-column with the specified text.
     *
     * @param detailsText
     *         the text to show if the column has been expanded.
     *
     * @return the HTML div to create the details column
     */
    public static String renderDetailsColumn(final String detailsText) {
        return renderDetailsColumn(detailsText, new JenkinsFacade());
    }

    /**
     * Renders an expandable details-column with the specified text.
     *
     * @param detailsText
     *         the text to show if the column has been expanded.
     * @param jenkinsFacade
     *         facade for Jenkins API calls
     *
     * @return the HTML div to create the details column
     */
    public static String renderDetailsColumn(final String detailsText, final JenkinsFacade jenkinsFacade) {
        return div()
                .withClass("details-control")
                .attr("data-description", detailsText)
                .with(new UnescapedText(
                        new SvgTag(DETAILS_COLUMN_ICON_NAME, jenkinsFacade).withClasses("details-icon").render()))
                .render();
    }

    private final String headerLabel;
    private final String definition;

    private ColumnCss headerClass = ColumnCss.NONE;
    private int width = 1;

    private TableColumn(final String definition, final String headerLabel, final ColumnCss headerClass) {
        this.headerLabel = headerLabel;
        this.definition = definition;
        this.headerClass = headerClass;
    }

    /**
     * Creates a simple column: it maps the specified property of the row entity to the column value.
     *
     * @param headerLabel
     *         the label of the column header
     * @param dataPropertyName
     *         the property to extract from the entity, it will be shown as column value
     * @deprecated Use {@link ColumnBuilder}
     */
    @Deprecated
    public TableColumn(final String headerLabel, final String dataPropertyName) {
        this.headerLabel = headerLabel;
        definition = String.format("{"
                + "  \"data\": \"%s\","
                + "  \"defaultContent\": \"\""
                + "}", dataPropertyName);
    }

    /**
     * Creates a complex column: it maps the specified property of the row entity to the display and sort attributes of
     * the column. The property {@code dataPropertyName} must be of type {@link DetailedCell}.
     *
     * @param headerLabel
     *         the label of the column header
     * @param dataPropertyName
     *         the property to extract from the entity, it will be shown as column value
     * @param columnDataType
     *         JQuery DataTables data type of the column
     * @deprecated Use {@link ColumnBuilder}
     */
    @Deprecated
    public TableColumn(final String headerLabel, final String dataPropertyName, final String columnDataType) {
        this.headerLabel = headerLabel;
        definition = String.format("{"
                + "  \"type\": \"%s\","
                + "  \"data\": \"%s\","
                + "  \"defaultContent\": \"\","
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
     * @deprecated Use {@link ColumnBuilder}
     */
    @Deprecated
    public TableColumn setHeaderClass(final ColumnCss headerClass) {
        this.headerClass = headerClass;

        return this;
    }

    /**
     * Not supported anymore.
     *
     * @param width
     *         the width
     *
     * @return this column
     * @see ColumnBuilder#withResponsivePriority(int)
     * @deprecated it makes more sense to let DataTables decide which columns to show
     */
    @Deprecated
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

    /**
     * Returns the width of the column.
     *
     * @return the width
     * @deprecated it makes more sense to let DataTables decide which columns to show
     */
    @Deprecated
    public int getWidth() {
        return width;
    }

    public String getDefinition() {
        return definition;
    }

    /**
     * Builder for {@link TableColumn} instances.
     */
    public static class ColumnBuilder {
        private static final int DEFAULT_PRIORITY = 10000;
        @CheckForNull
        private String header;
        @CheckForNull
        private String propertyKey;
        @CheckForNull
        private String type;
        private int responsivePriority = DEFAULT_PRIORITY; // default priority of datatables
        private ColumnCss headerCssClass = ColumnCss.NONE; // No specific class
        private boolean isDetailedCellEnabled = false; // disabled by default

        /**
         * Sets the data type of the column.
         *
         * @param dataType
         *         type of the column
         *
         * @return this
         * @see <a href="https://datatables.net/reference/option/columns.type">DataTables columns types API
         *         Reference</a>
         */
        // FIXME: enum?
        public ColumnBuilder withType(final String dataType) {
            this.type = dataType;

            return this;
        }

        /**
         * Sets the key of the JSON property in the corresponding row entities that should be shown in this column.
         *
         * @param dataPropertyKey
         *         name of the property in the corresponding row entity that should be shown in this column
         *
         * @return this
         */
        public ColumnBuilder withDataPropertyKey(final String dataPropertyKey) {
            this.propertyKey = dataPropertyKey;

            return this;
        }

        /**
         * Sets the priority of this column. This priority will be evaluated when the table is created with the
         * {@link TableConfiguration#responsive() responsive} option enabled. In this case the columns will
         * automatically hide columns in a table so that the table fits horizontally into the space given to it. If not
         * set, the DataTables default of {@link #DEFAULT_PRIORITY} will be used.
         *
         * @param priority
         *         the priority of this column
         *
         * @return this column
         * @see <a href="https://datatables.net/extensions/responsive/priority">DataTables Responsive API
         *         Reference</a>
         */
        public ColumnBuilder withResponsivePriority(final int priority) {
            if (priority < 0) {
                throw new IllegalArgumentException("Responsive priority " + priority + " must be a positive value");
            }

            this.responsivePriority = priority;

            return this;
        }

        /**
         * Sets the label of the header for this column. This header will be used as text in the {@code <th>} tag of the
         * corresponding table.
         *
         * @param headerLabel
         *         label of the column
         *
         * @return this
         */
        public ColumnBuilder withHeaderLabel(final String headerLabel) {
            this.header = headerLabel;

            return this;
        }

        /**
         * Sets the CSS class for the column {@code <th>} tag.
         *
         * @param headerClass
         *         the CSS class(es) for the {@code <th>} tag
         *
         * @return this column
         */
        public ColumnBuilder withHeaderClass(final ColumnCss headerClass) {
            this.headerCssClass = headerClass;

            return this;
        }

        /**
         * Enables the rendering of cells that use the {@link DetailedCell} format. Such cells can use different
         * properties to sort and display the cell values.
         *
         * @return this column
         */
        public ColumnBuilder withDetailedCell() {
            this.isDetailedCellEnabled = true;

            return this;
        }

        /**
         * Creates a new {@link TableColumn} based on the specified builder configuration.
         *
          * @return the created {@link TableColumn}
         * @throws IllegalArgumentException if the configuration is invalid
         */
        public TableColumn build() {
            if (StringUtils.isBlank(header)) {
                throw new IllegalArgumentException("Empty header label, see #withHeaderLabel");
            }
            return new TableColumn(createDefinition(), header, headerCssClass);
        }

        private String createDefinition() {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode columnDefinition = mapper.createObjectNode();
            try {
                if (propertyKey == null) {
                    throw new IllegalArgumentException("No 'dataPropertyKey' defined, see #withDataPropertyKey");
                }
                columnDefinition.put("data", propertyKey);
                if (type != null) { // optional property
                    columnDefinition.put("type", type);
                }
                if (responsivePriority != DEFAULT_PRIORITY) { // optional property
                    columnDefinition.put("responsivePriority", responsivePriority);
                }
                if (isDetailedCellEnabled) {
                    ObjectNode detailedRenderer = mapper.createObjectNode();
                    detailedRenderer.put("_", "display");
                    detailedRenderer.put("sort", "sort");
                    columnDefinition.set("render", detailedRenderer);
                }
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(columnDefinition);
            }
            catch (JsonProcessingException exception) {
                throw new IllegalArgumentException("Can't convert to JSON: " + columnDefinition);
            }
        }
    }

    /**
     * Supported CSS classes that will enable special handling or rendering for table columns.
     */
    public enum ColumnCss {
        /** No special rendering, the display property will be shown as such. */
        NONE(""),
        /**
         * Dates will be shown using Luxon. The display value will be a human friendly relative time like "two weeks
         * ago", rather than an absolute time. A tooltip is available that shows the absolute time.
         */
        DATE("date"),
        /**
         * Percentages (values in the interval [0,1]) will be rendered correctly as a percentage using the native JS
         * locale sensitive rendering.
         */
        PERCENTAGE("percentage"),
        /**
         * Numbers will be shown right aligned, so they can be compared more easily.
         */
        NUMBER("text-end"),
        /** Disables sorting of the column. Rendering is the same as with {@code NONE}. */
        NO_SORT("nosort"),
        /** Hides the column for view. It still exists and responds to searching */
        HIDDEN("hidden");

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
