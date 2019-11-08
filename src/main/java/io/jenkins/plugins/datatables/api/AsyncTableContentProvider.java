package io.jenkins.plugins.datatables.api;

import hudson.model.ModelObject;

/**
 * Defines additional API methods a Jenkins {@link ModelObject} must provide in order to render JQuery DataTables in a
 * corresponding jelly view using the extensions of the {@code data-tables-api-plugin}. Those tables will have the data
 * loaded asynchronously using an Ajax call. In order to make that interaction happen your jelly view also needs to
 * follow the following guidelines:
 *
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
     * Returns the rows of the table model with the specified ID. The rows must use the JSON specification of the
     * {@code data} attribute of the data table. Note that this method will be invoked asynchronously using an Ajax call
     * after the page has been renderded.
     *
     *
     * @param id
     *         ID of the table model
     *
     * @return the table model with the specified ID
     */
    String getTableRows(String id);
}
