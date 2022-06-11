package io.jenkins.plugins.datatables;

import edu.hm.hafner.util.VisibleForTesting;

import j2html.tags.UnescapedText;

import io.jenkins.plugins.datatables.TableModel.DetailedCell;
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
 *   <li>width</li>
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
    private int priority;

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
     */
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
     * Sets the priority of this column. This priority will be evaluated when the table is created with the
     * {@link TableConfiguration#responsive() responsive} option enabled. In this case the columns will automatically
     * hide columns in a table so that the table fits horizontally into the space given to it.
     *
     * @param priority
     *         the priority of this column
     *
     * @return this column
     * @see <a href="https://datatables.net/extensions/responsive/priority">DataTables Responsive API Reference</a>
     */
    public TableColumn setPriority(final int priority) {
        if (priority < 0) {
            throw new IllegalArgumentException("Priority must be a positive value");
        }
        this.priority = priority;

        return this;
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
    // FIXME: use datatables width!
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

    /**
     * Returns the column definition: if the priority is set, then this {@code resposonsivePriority} will be added to
     * the definition.
     *
     * @return the column definition
     */
    public String getDefinition() {
        if (priority > 0) {
            return definition.replaceFirst("^\\{", String.format("{\"responsivePriority\":%d,", priority));
        }
        return definition;
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
