package io.jenkins.plugins.datatables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;

import io.jenkins.plugins.util.PageObject;

import static org.assertj.core.api.Assertions.*;

/**
 * Page object for a row in a data table. Basically contains a map of row IDs to a corresponding cell value.
 *
 * @author Ullrich Hafner
 */
public class TableRowPageObject extends PageObject {
    private final Map<String, String> valueByColumn = new HashMap<>();
    private final Map<String, HtmlTableCell> cellsByColumn = new HashMap<>();

    /**
     * Creates a new row based on a list of HTML cells and columns.
     *
     * @param page
     *         the page that contains the table
     * @param columnLabels
     *         the labels of the columns
     * @param values
     *         the cell values of the columns
     */
    TableRowPageObject(final HtmlPage page, final List<String> columnLabels,
            final List<HtmlTableCell> values) {
        super(page);

        assertThat(columnLabels.size()).as("Size of column headers and values must match").isEqualTo(values.size());

        for (int pos = 0; pos < columnLabels.size(); pos++) {
            String key = columnLabels.get(pos);
            HtmlTableCell cell = values.get(pos);
            cellsByColumn.put(key, cell);
            valueByColumn.put(key, getCellContent(cell));
        }
    }

    private String getCellContent(final HtmlTableCell cell) {
        DomElement child = cell.getFirstElementChild();
        if (child != null) {
            String dataDescription = getDescriptionContent(child);
            if (StringUtils.isNotBlank(dataDescription)) {
                return dataDescription;
            }
        }
        return cell.getTextContent();
    }

    private String getDescriptionContent(final DomElement child) {
        String dataDescription = child.getAttributeDirect("data-description");
        if (StringUtils.isNotBlank(dataDescription)) {
            return dataDescription
                    .replace("<p><strong>", "")
                    .replace("</strong></p>", "");
        }
        return StringUtils.EMPTY;
    }

    /**
     * Returns whether the specified column contains a link.
     *
     * @param column
     *         the column
     *
     * @return {@code true} if the column contains a link, {@code false} if the column contains plain text
     */
    public boolean hasLink(final String column) {
        return getLink(column) instanceof HtmlAnchor;
    }

    /**
     * Returns the link of a column.
     *
     * @param column
     *         the column
     *
     * @return the link of the column
     */
    public DomElement getLink(final String column) {
        return cellsByColumn.get(column).getFirstElementChild();
    }

    /**
     * Clicks the specified column link.
     *
     * @param column
     *         the column
     *
     * @return the page the browser has been redirected to
     */
    public HtmlPage clickColumnLink(final String column) {
        assertThat(hasLink(column)).isTrue();

        DomElement link = getLink(column);
        return clickOnElement(link);
    }

    public Map<String, String> getValuesByColumnLabel() {
        return valueByColumn;
    }
}
