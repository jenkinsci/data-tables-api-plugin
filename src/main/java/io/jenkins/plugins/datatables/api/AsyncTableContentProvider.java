package io.jenkins.plugins.datatables.api;

import hudson.model.ModelObject;

/**
 * Defines additional API methods a Jenkins {@link ModelObject} must provide in order to render JQuery DataTables in a
 * corresponding jelly view using the tag {@code table.jelly} of the {@code data-tables-api-plugin}. Those tables will
 * have the data loaded asynchronously using an Ajax call.
 * <p>
 * Typically, you do not need to implement this interface directly. Preferred way is to use the default implementation
 * in {@link DefaultAsyncTableContentProvider} that already provides the correct extraction of the data rows as JSON
 * objects.
 * </p>
 *
 * @author Ullrich Hafner
 */
public interface AsyncTableContentProvider {
    /**
     * Returns the table model with the specified ID. A model object may show several data tables, each table must use a
     * unique ID.
     *
     * @param id
     *         ID of the table model
     *
     * @return the table model with the specified ID
     */
    TableModel getTableModel(String id);

    /**
     * Returns the rows of the table model with the specified ID. The rows must use the JSON specification of the {@code
     * data} attribute of the data table. Note that this method will be invoked asynchronously using an Ajax call after
     * the page has been rendered.
     *
     * @param id
     *         ID of the table model
     *
     * @return the table model with the specified ID
     */
    String getTableRows(String id);
}
