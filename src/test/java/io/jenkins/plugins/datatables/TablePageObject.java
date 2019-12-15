package io.jenkins.plugins.datatables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.jenkins.plugins.util.PageObject;

import static org.assertj.core.api.Assertions.*;

/**
 * Page object for a data table. Provides access to the column headers and rows.
 *
 * @author Ullrich Hafner
 */
public class TablePageObject extends PageObject {
    private final List<TableRowPageObject> rows = new ArrayList<>();
    private final List<String> columnHeaders;
    private final DomElement summary;
    private final DomElement filter;
    private final HtmlTableBody body;

    /**
     * Creates a new instance of {@link TablePageObject}.
     *
     * @param page
     *         the page that contains the table
     * @param tableId
     *         the DOM ID of the table
     */
    @SuppressFBWarnings("BC")
    public TablePageObject(final HtmlPage page, final String tableId) {
        super(page);

        DomElement tableDom = page.getElementById(tableId);
        assertThat(tableDom).isInstanceOf(HtmlTable.class);
        HtmlTable table = (HtmlTable) tableDom;

        List<HtmlTableRow> headerRows = table.getHeader().getRows();
        assertThat(headerRows).hasSize(1);

        HtmlTableRow header = headerRows.get(0);
        columnHeaders = getHeaders(header.getCells());

        waitUntilTableIsInitialized(table);

        List<HtmlTableBody> bodies = table.getBodies();
        assertThat(bodies).hasSize(1);
        body = bodies.get(0);

        waitForAjaxCall(body);

        updateVisibleRows();

        summary = page.getElementById(tableId + "_info");
        filter = page.getElementById(tableId + "_filter");
    }

    private void updateVisibleRows() {
        rows.clear();
        for (HtmlTableRow row : body.getRows()) {
            rows.add(new TableRowPageObject(getPage(), columnHeaders, row.getCells()));
        }
    }

    @SuppressWarnings("PMD.SystemPrintln")
    private void waitUntilTableIsInitialized(final HtmlTable table) {
        while (table.getBodies().isEmpty()) {
            System.out.println("Waiting for table to be initialized ...");
            table.getPage().getEnclosingWindow().getJobManager().waitForJobs(1000);
        }
    }

    @SuppressWarnings("PMD.SystemPrintln")
    private void waitForAjaxCall(final HtmlTableBody body) {
        while ("Loading - please wait ...".equals(
                body.getRows().get(0).getCells().get(0).getFirstChild().getTextContent())) {
            System.out.println("Waiting for Ajax call to populate issues table ...");
            body.getPage().getEnclosingWindow().getJobManager().waitForJobs(1000);
        }
    }

    public List<TableRowPageObject> getRows() {
        return rows;
    }

    public List<String> getColumnHeaders() {
        return columnHeaders;
    }

    private List<String> getHeaders(final List<HtmlTableCell> cells) {
        return cells.stream()
                .map(HtmlTableCell::getTextContent)
                .collect(Collectors.toList());
    }

    /**
     * Returns the row with the specified index.
     *
     * @param index
     *         index of the row
     *
     * @return the row
     */
    public TableRowPageObject getRow(final int index) {
        return rows.get(index);
    }

    public String getInfo() {
        return summary.getTextContent();
    }

    /**
     * Returns the number of visible rows.
     *
     * @return the number of visible rows
     */
    public int size() {
        return rows.size();
    }

    /**
     * Filters the table to show only elements matching the specified text.
     *
     * @param filterText
     *         the text to use as a filter
     */
    public void filter(final String filterText) {
        try {
            HtmlTextInput searchControl = (HtmlTextInput) filter.getElementsByTagName("input").get(0);
            searchControl.type(filterText);

            updateVisibleRows();
        }
        catch (IOException exception) {
            throw new AssertionError(exception);
        }
    }
}
